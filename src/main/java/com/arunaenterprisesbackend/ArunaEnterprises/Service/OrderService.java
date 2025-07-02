package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.OrderDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.SuggestedReelDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.SuggestedReelsResponseDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderSuggestedReelsRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.SuggestedReelRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class OrderService {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SuggestedReelRepository suggestedReelRepository;

    @Autowired
    private ReelRepository reelRepository;

    @Autowired
    private OrderSuggestedReelsRepository orderSuggestedReelsRepository;

    @Transactional
    public SuggestedReelsResponseDTO createOrder(OrderDTO orderDTO) {
        try {
            // 1. Create and save the order
            Order order = createAndSaveOrder(orderDTO);

            // 2. Fetch suggested reel configuration
            Optional<SuggestedReel> suggestedOpt = suggestedReelRepository
                    .findByClientNormalizerAndSize(order.getNormalizedClient(), order.getSize());

            if (suggestedOpt.isEmpty()) {
                return createEmptyResponse("Order created successfully, but no suggested reels were found");
            }

            SuggestedReel suggested = suggestedOpt.get();
            boolean needsFlute = (suggested.getFluteGsm() != suggested.getLinerGsm());

            // 3. Fetch available reels
            List<Reel> topReels = fetchReels(suggested.getTopGsm(), suggested.getDeckle());
            List<Reel> bottomReels = fetchReels(suggested.getBottomGsm(), suggested.getDeckle());
            List<Reel> fluteReels = needsFlute ? fetchReels(suggested.getFluteGsm(), suggested.getDeckle()) : Collections.emptyList();

            // 4. Convert to DTOs for response
            List<SuggestedReelDTO> topDTOs = convertReelsToDTOs(topReels, suggested);
            List<SuggestedReelDTO> bottomDTOs = convertReelsToDTOs(bottomReels, suggested);
            List<SuggestedReelDTO> fluteDTOs = needsFlute ? convertReelsToDTOs(fluteReels, suggested) : Collections.emptyList();

            // 5. Store suggestions in database
            storeSuggestions(order, topReels, bottomReels, fluteReels, needsFlute);

            // 6. Build and return response
            return buildResponse(topDTOs, bottomDTOs, fluteDTOs, needsFlute);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create order or fetch suggested reels: " + e.getMessage(), e);
        }
    }


    public SuggestedReelsResponseDTO getSuggestedReels(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Suggestions not available for completed orders");
        }

        OrderSuggestedReels suggestions = orderSuggestedReelsRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("No suggestions found for this order"));

        return mapToResponse(suggestions);
    }

    // Helper methods
    private Order createAndSaveOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setClient(orderDTO.getClient());
        order.setCreatedAt(ZonedDateTime.now(IST_ZONE));
        order.setTypeOfProduct(orderDTO.getTypeOfProduct());
        order.setCreatedBy(orderDTO.getCreatedBy());
        order.setSize(orderDTO.getSize());
        order.setStatus(OrderStatus.TODO);
        order.setDeliveryAddress(orderDTO.getDeliveryAddress());
        order.setQuantity(orderDTO.getQuantity());
        order.setExpectedCompletionDate(orderDTO.getExpectedCompletionDate());
        order.setProductType(orderDTO.getProductType());
        order.setMaterialGrade(orderDTO.getMaterialGrade());
        order.setUpdatedAt(ZonedDateTime.now(IST_ZONE));
        order.setUnit(orderDTO.getUnit());
        order.setTransportNumber(orderDTO.getTransportNumber());

        String normalizedClient = orderDTO.getClient().toLowerCase().replaceAll("[^a-z0-9]", "");
        order.setNormalizedClient(normalizedClient);

        return orderRepository.save(order);
    }

    private List<Reel> fetchReels(int gsm, int deckle) {
        List<ReelStatus> statuses = List.of(
                ReelStatus.PARTIALLY_USED_AVAILABLE,
                ReelStatus.NOT_IN_USE
        );
        return reelRepository.findAvailableByGsmAndDeckleSorted(gsm, deckle, statuses);
    }

    private void storeSuggestions(Order order, List<Reel> topReels, List<Reel> bottomReels,
                                  List<Reel> fluteReels, boolean needsFlute) {
        OrderSuggestedReels storedSuggestions = new OrderSuggestedReels();
        storedSuggestions.setOrder(order);

        // Convert Reel lists to SuggestedReelItem lists
        List<SuggestedReelItem> topItems = convertToItems(topReels);
        List<SuggestedReelItem> bottomItems = convertToItems(bottomReels);

        storedSuggestions.setTopReels(topItems);
        storedSuggestions.setBottomReels(bottomItems);

        if (needsFlute) {
            List<SuggestedReelItem> fluteItems = convertToItems(fluteReels);
            storedSuggestions.setFluteReels(fluteItems);
        }

        orderSuggestedReelsRepository.save(storedSuggestions);
    }


    private List<SuggestedReelItem> convertToItems(List<Reel> reels) {
        return reels.stream()
                .map(r -> new SuggestedReelItem(
                        r.getBarcodeId(),
                        r.getReelNo(),
                        r.getGsm(),
                        r.getDeckle(),
                        r.getCurrentWeight(),
                        r.getUnit(),
                        r.getStatus(),
                        r.getReelSet()
                ))
                .collect(Collectors.toList());
    }

    private SuggestedReelsResponseDTO mapToResponse(OrderSuggestedReels suggestions) {
        if (suggestions == null) {
            throw new IllegalArgumentException("Suggestions cannot be null");
        }

        SuggestedReelsResponseDTO response = new SuggestedReelsResponseDTO();

        response.setTopGsmReels(safeMapToDTOs(suggestions.getTopReels()));
        response.setBottomGsmReels(safeMapToDTOs(suggestions.getBottomReels()));
        response.setFluteGsmReels(safeMapToDTOs(suggestions.getFluteReels()));

        response.setFluteRequired(suggestions.getFluteReels() != null && !suggestions.getFluteReels().isEmpty());
        response.setMessage("Suggested reels retrieved successfully");

        return response;
    }


    private List<SuggestedReelDTO> safeMapToDTOs(List<SuggestedReelItem> items) {
        return items == null ? Collections.emptyList() :
                items.stream().map(this::mapToDTO).collect(Collectors.toList());
    }


    private SuggestedReelDTO mapToDTO(SuggestedReelItem item) {
        SuggestedReelDTO dto = new SuggestedReelDTO();
        dto.setBarcodeId(item.getBarcodeId());
        dto.setReelNo(item.getReelNo());
        dto.setGsm(item.getGsm());
        dto.setDeckle(item.getDeckle());
        dto.setCurrentWeight(item.getCurrentWeight());
        dto.setUnit(item.getUnit());
        dto.setStatus(item.getStatus());
        return dto;
    }


    private SuggestedReelsResponseDTO buildResponse(List<SuggestedReelDTO> topDTOs,
                                                    List<SuggestedReelDTO> bottomDTOs,
                                                    List<SuggestedReelDTO> fluteDTOs,
                                                    boolean needsFlute) {
        SuggestedReelsResponseDTO response = new SuggestedReelsResponseDTO();
        response.setTopGsmReels(topDTOs);
        response.setBottomGsmReels(bottomDTOs);
        response.setFluteGsmReels(fluteDTOs);
        response.setFluteRequired(needsFlute);
        response.setMessage("Order created successfully with suggested reels.");
        return response;
    }

    private SuggestedReelsResponseDTO createEmptyResponse(String message) {
        SuggestedReelsResponseDTO response = new SuggestedReelsResponseDTO();
        response.setTopGsmReels(Collections.emptyList());
        response.setBottomGsmReels(Collections.emptyList());
        response.setFluteGsmReels(Collections.emptyList());
        response.setFluteRequired(false);
        response.setMessage(message);
        return response;
    }

    private List<SuggestedReelDTO> convertReelsToDTOs(List<Reel> reels, SuggestedReel suggested) {
        return reels.stream()
                .filter(r -> r.getReelNo() != null)
                .sorted(Comparator
                        .comparing((Reel r) -> r.getStatus() == ReelStatus.PARTIALLY_USED_AVAILABLE ? 0 : 1)
                        .thenComparingInt(Reel::getCurrentWeight))
                .map(r -> buildSuggestedDTO(r, suggested))
                .collect(Collectors.toList());
    }

    private SuggestedReelDTO buildSuggestedDTO(Reel reel, SuggestedReel suggested) {
        SuggestedReelDTO dto = new SuggestedReelDTO();
        dto.setBarcodeId(reel.getBarcodeId());
        dto.setReelNo(reel.getReelNo() != null ? reel.getReelNo() : 0L);
        dto.setClient(suggested.getClient());
        dto.setClientNormalizer(suggested.getClientNormalizer());
        dto.setProduct(suggested.getProduct());
        dto.setSize(suggested.getSize());
        dto.setPly(suggested.getPly());
        dto.setGsm(reel.getGsm());
        dto.setDeckle(reel.getDeckle());
        dto.setCuttingLength(suggested.getCuttingLength());
        dto.setTopGsm(suggested.getTopGsm());
        dto.setLinerGsm(suggested.getLinerGsm());
        dto.setFluteGsm(suggested.getFluteGsm());
        dto.setMadeUpOf(suggested.getMadeUpOf());
        dto.setPaperTypeTop(suggested.getPaperTypeTop());
        dto.setPaperTypeBottom(suggested.getPaperTypeBottom());
        dto.setCurrentWeight(reel.getCurrentWeight());
        dto.setUnit(reel.getUnit());
        dto.setReelSet(reel.getReelSet());
        dto.setStatus(reel.getStatus());
        return dto;
    }


    public void updateOrderStatus(Long orderId, OrderStatus newStatus, String transportNumber) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        ZonedDateTime now = ZonedDateTime.now(IST_ZONE);
        order.setUpdatedAt(now);
        order.setStatus(newStatus);

        switch (newStatus) {
            case COMPLETED:
                order.setCompletedAt(now);
                order.setShippedAt(null);
                order.setTransportNumber(null);
                break;

            case SHIPPED:
                order.setShippedAt(now);
                order.setTransportNumber(transportNumber);
                break;

            default:
                order.setCompletedAt(null);
                order.setShippedAt(null);
                order.setTransportNumber(null);
                break;
        }

        orderRepository.save(order);
    }
}