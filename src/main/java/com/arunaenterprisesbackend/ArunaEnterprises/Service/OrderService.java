package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;


//@Service
//public class OrderService {
//
//    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private SuggestedReelRepository suggestedReelRepository;
//
//    @Autowired
//    private ReelRepository reelRepository;
//
//    @Autowired
//    private OrderSuggestedReelsRepository orderSuggestedReelsRepository;
//
//    @Transactional
//    public SuggestedReelsResponseDTO createOrder(OrderDTO orderDTO) {
//        try {
//            // 1. Create and save the order
//            Order order = createAndSaveOrder(orderDTO);
//
//            // 2. Fetch suggested reel configuration
//            Optional<SuggestedReel> suggestedOpt = suggestedReelRepository
//                    .findByClientNormalizerAndSize(order.getNormalizedClient(), order.getSize());
//
//            if (suggestedOpt.isEmpty()) {
//                return createEmptyResponse("Order created successfully, but no suggested reels were found");
//            }
//
//            SuggestedReel suggested = suggestedOpt.get();
//            boolean needsFlute = (suggested.getFluteGsm() != suggested.getLinerGsm());
//
//            // 3. Fetch available reels
//            List<Reel> topReels = fetchReels(suggested.getTopGsm(), suggested.getDeckle());
//            List<Reel> bottomReels = fetchReels(suggested.getBottomGsm(), suggested.getDeckle());
//            List<Reel> fluteReels = needsFlute ? fetchReels(suggested.getFluteGsm(), suggested.getDeckle()) : Collections.emptyList();
//
//            // 4. Convert to DTOs for response
//            List<SuggestedReelDTO> topDTOs = convertReelsToDTOs(topReels, suggested);
//            List<SuggestedReelDTO> bottomDTOs = convertReelsToDTOs(bottomReels, suggested);
//            List<SuggestedReelDTO> fluteDTOs = needsFlute ? convertReelsToDTOs(fluteReels, suggested) : Collections.emptyList();
//
//            // 5. Store suggestions in database
//            storeSuggestions(order, topReels, bottomReels, fluteReels, needsFlute);
//
//            // 6. Build and return response
//            return buildResponse(topDTOs, bottomDTOs, fluteDTOs, needsFlute);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to create order or fetch suggested reels: " + e.getMessage(), e);
//        }
//    }
//
//
//    public SuggestedReelsResponseDTO getSuggestedReels(Long orderId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        if (order.getStatus() == OrderStatus.COMPLETED) {
//            throw new RuntimeException("Suggestions not available for completed orders");
//        }
//
//        OrderSuggestedReels suggestions = orderSuggestedReelsRepository.findByOrder(order)
//                .orElseThrow(() -> new RuntimeException("No suggestions found for this order"));
//
//        return mapToResponse(suggestions);
//    }
//
//    // Helper methods
//    private Order createAndSaveOrder(OrderDTO orderDTO) {
//        Order order = new Order();
//        order.setClient(orderDTO.getClient());
//        order.setCreatedAt(ZonedDateTime.now(IST_ZONE));
//        order.setTypeOfProduct(orderDTO.getTypeOfProduct());
//        order.setCreatedBy(orderDTO.getCreatedBy());
//        order.setSize(orderDTO.getSize());
//        order.setStatus(OrderStatus.TODO);
//        order.setDeliveryAddress(orderDTO.getDeliveryAddress());
//        order.setQuantity(orderDTO.getQuantity());
//        order.setExpectedCompletionDate(orderDTO.getExpectedCompletionDate());
//        order.setProductType(orderDTO.getProductType());
//        order.setMaterialGrade(orderDTO.getMaterialGrade());
//        order.setUpdatedAt(ZonedDateTime.now(IST_ZONE));
//        order.setUnit(orderDTO.getUnit());
//        order.setTransportNumber(orderDTO.getTransportNumber());
//
//        String normalizedClient = orderDTO.getClient().toLowerCase().replaceAll("[^a-z0-9]", "");
//        order.setNormalizedClient(normalizedClient);
//
//        return orderRepository.save(order);
//    }
//
//    private List<Reel> fetchReels(int gsm, int deckle) {
//        List<ReelStatus> statuses = List.of(
//                ReelStatus.PARTIALLY_USED_AVAILABLE,
//                ReelStatus.NOT_IN_USE
//        );
//        return reelRepository.findAvailableByGsmAndDeckleSorted(gsm, deckle, statuses);
//    }
//
//    private void storeSuggestions(Order order, List<Reel> topReels, List<Reel> bottomReels,
//                                  List<Reel> fluteReels, boolean needsFlute) {
//        OrderSuggestedReels storedSuggestions = new OrderSuggestedReels();
//        storedSuggestions.setOrder(order);
//
//        // Convert Reel lists to SuggestedReelItem lists
//        List<SuggestedReelItem> topItems = convertToItems(topReels);
//        List<SuggestedReelItem> bottomItems = convertToItems(bottomReels);
//
//        storedSuggestions.setTopReels(topItems);
//        storedSuggestions.setBottomReels(bottomItems);
//
//        if (needsFlute) {
//            List<SuggestedReelItem> fluteItems = convertToItems(fluteReels);
//            storedSuggestions.setFluteReels(fluteItems);
//        }
//
//        orderSuggestedReelsRepository.save(storedSuggestions);
//    }
//
//
//    private List<SuggestedReelItem> convertToItems(List<Reel> reels) {
//        return reels.stream()
//                .map(r -> new SuggestedReelItem(
//                        r.getBarcodeId(),
//                        r.getReelNo(),
//                        r.getGsm(),
//                        r.getDeckle(),
//                        r.getCurrentWeight(),
//                        r.getUnit(),
//                        r.getStatus(),
//                        r.getReelSet()
//                ))
//                .collect(Collectors.toList());
//    }
//
//    private SuggestedReelsResponseDTO mapToResponse(OrderSuggestedReels suggestions) {
//        if (suggestions == null) {
//            throw new IllegalArgumentException("Suggestions cannot be null");
//        }
//
//        SuggestedReelsResponseDTO response = new SuggestedReelsResponseDTO();
//
//        response.setTopGsmReels(safeMapToDTOs(suggestions.getTopReels()));
//        response.setBottomGsmReels(safeMapToDTOs(suggestions.getBottomReels()));
//        response.setFluteGsmReels(safeMapToDTOs(suggestions.getFluteReels()));
//
//        response.setFluteRequired(suggestions.getFluteReels() != null && !suggestions.getFluteReels().isEmpty());
//        response.setMessage("Suggested reels retrieved successfully");
//
//        return response;
//    }
//
//
//    private List<SuggestedReelDTO> safeMapToDTOs(List<SuggestedReelItem> items) {
//        return items == null ? Collections.emptyList() :
//                items.stream().map(this::mapToDTO).collect(Collectors.toList());
//    }
//
//
//    private SuggestedReelDTO mapToDTO(SuggestedReelItem item) {
//        SuggestedReelDTO dto = new SuggestedReelDTO();
//        dto.setBarcodeId(item.getBarcodeId());
//        dto.setReelNo(item.getReelNo());
//        dto.setGsm(item.getGsm());
//        dto.setDeckle(item.getDeckle());
//        dto.setCurrentWeight(item.getCurrentWeight());
//        dto.setUnit(item.getUnit());
//        dto.setStatus(item.getStatus());
//        return dto;
//    }
//
//
//    private SuggestedReelsResponseDTO buildResponse(List<SuggestedReelDTO> topDTOs,
//                                                    List<SuggestedReelDTO> bottomDTOs,
//                                                    List<SuggestedReelDTO> fluteDTOs,
//                                                    boolean needsFlute) {
//        SuggestedReelsResponseDTO response = new SuggestedReelsResponseDTO();
//        response.setTopGsmReels(topDTOs);
//        response.setBottomGsmReels(bottomDTOs);
//        response.setFluteGsmReels(fluteDTOs);
//        response.setFluteRequired(needsFlute);
//        response.setMessage("Order created successfully with suggested reels.");
//        return response;
//    }
//
//    private SuggestedReelsResponseDTO createEmptyResponse(String message) {
//        SuggestedReelsResponseDTO response = new SuggestedReelsResponseDTO();
//        response.setTopGsmReels(Collections.emptyList());
//        response.setBottomGsmReels(Collections.emptyList());
//        response.setFluteGsmReels(Collections.emptyList());
//        response.setFluteRequired(false);
//        response.setMessage(message);
//        return response;
//    }
//
//    private List<SuggestedReelDTO> convertReelsToDTOs(List<Reel> reels, SuggestedReel suggested) {
//        return reels.stream()
//                .filter(r -> r.getReelNo() != null)
//                .sorted(Comparator
//                        .comparing((Reel r) -> r.getStatus() == ReelStatus.PARTIALLY_USED_AVAILABLE ? 0 : 1)
//                        .thenComparingInt(Reel::getCurrentWeight))
//                .map(r -> buildSuggestedDTO(r, suggested))
//                .collect(Collectors.toList());
//    }
//
//    private SuggestedReelDTO buildSuggestedDTO(Reel reel, SuggestedReel suggested) {
//        SuggestedReelDTO dto = new SuggestedReelDTO();
//        dto.setBarcodeId(reel.getBarcodeId());
//        dto.setReelNo(reel.getReelNo() != null ? reel.getReelNo() : 0L);
//        dto.setClient(suggested.getClient());
//        dto.setClientNormalizer(suggested.getClientNormalizer());
//        dto.setProduct(suggested.getProduct());
//        dto.setSize(suggested.getSize());
//        dto.setPly(suggested.getPly());
//        dto.setGsm(reel.getGsm());
//        dto.setDeckle(reel.getDeckle());
//        dto.setCuttingLength(suggested.getCuttingLength());
//        dto.setTopGsm(suggested.getTopGsm());
//        dto.setLinerGsm(suggested.getLinerGsm());
//        dto.setFluteGsm(suggested.getFluteGsm());
//        dto.setMadeUpOf(suggested.getMadeUpOf());
//        dto.setPaperTypeTop(suggested.getPaperTypeTop());
//        dto.setPaperTypeBottom(suggested.getPaperTypeBottom());
//        dto.setCurrentWeight(reel.getCurrentWeight());
//        dto.setUnit(reel.getUnit());
//        dto.setReelSet(reel.getReelSet());
//        dto.setStatus(reel.getStatus());
//        return dto;
//    }
//
//
//    public void updateOrderStatus(Long orderId, OrderStatus newStatus, String transportNumber) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        ZonedDateTime now = ZonedDateTime.now(IST_ZONE);
//        order.setUpdatedAt(now);
//        order.setStatus(newStatus);
//
//        switch (newStatus) {
//            case COMPLETED:
//                order.setCompletedAt(now);
//                order.setShippedAt(null);
//                order.setTransportNumber(null);
//                break;
//
//            case SHIPPED:
//                order.setShippedAt(now);
//                order.setTransportNumber(transportNumber);
//                break;
//
//            default:
//                order.setCompletedAt(null);
//                order.setShippedAt(null);
//                order.setTransportNumber(null);
//                break;
//        }
//
//        orderRepository.save(order);
//    }
//}


@Service
public class OrderService {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    @Autowired private OrderRepository orderRepository;
    @Autowired private SuggestedReelRepository suggestedReelRepository;
    @Autowired private ReelRepository reelRepository;
    @Autowired private OrderSuggestedReelsRepository orderSuggestedReelsRepository;
    @Autowired private ProductionDetailRepository productionDetailRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private MachineRepository machineRepository;

    @Transactional
    public SuggestedReelsResponseDTO createOrder(OrderDTO dto) {

        Order order = createAndSaveOrder(dto);

        String productNorm = order.getProductName()
                .toLowerCase().replaceAll("[^a-z0-9]", "");

        Optional<SuggestedReel> sropt =
                suggestedReelRepository.findByClientNormalizerAndSizeAndProductNormalizer(
                        order.getNormalizedClient(),
                        order.getSize(),
                        productNorm
                );

        SuggestedReel sr = null;
        Clients client = null;

        if (sropt.isPresent()) {
            sr = sropt.get();
        } else {
            client = clientRepository
                    .findByClientNormalizerAndSizeAndProductNormalizer(
                            order.getNormalizedClient(),
                            order.getSize(),
                            productNorm
                    )
                    .orElseThrow(() -> new RuntimeException("Client master not found"));
        }

        String ply = (sr != null) ? sr.getPly() : client.getPly();
        double dkl = (sr != null) ? sr.getDeckle() : client.getDeckle();
        double ctl = (sr != null) ? sr.getCuttingLength() : client.getCuttingLength();
        int topgsm = (sr != null) ? sr.getTopGsm() : client.getTopGsm();
        int linergsm = (sr != null) ? sr.getLinerGsm() : client.getLinerGsm();
        int flutegsm = (sr != null) ? sr.getFluteGsm() : client.getFluteGsm();
        String paperTop = (sr != null) ? sr.getPaperTypeTop() : client.getPaperTypeTop();
        String paperBottom = (sr != null) ? sr.getPaperTypeBottom() : client.getPaperTypeBottom();
        String paperFlute = (sr != null) ? sr.getPaperTypeFlute() : client.getPaperTypeFlute();
        String fluteType = (sr != null) ? sr.getFluteType() : client.getFluteType();
        double cutLenOneUps = (sr != null) ? sr.getCuttingLengthOneUps() : 0;
        double cutLenTwoUps = (sr != null) ? sr.getCuttingLengthTwoUps() : 0;

        int qnt = dto.getQuantity();

        double topWeightPerSheet = (dkl * ctl * topgsm) / 10_000.0;
        double linerWeightPerSheet = (dkl * ctl * linergsm) / 10_000.0;
        double fluteWeightPerSheet = (dkl * ctl * flutegsm * 1.60) / 10_000.0;

        double toptotalweight = (topWeightPerSheet * qnt) / 1000;
        double linertotalweight = (linerWeightPerSheet * qnt) / 1000;
        double flutetotalweight = (fluteWeightPerSheet * qnt) / 1000;

        ProductionDetail prddetail = new ProductionDetail();
        prddetail.setOrder(order);
        prddetail.setClient(order.getClient());
        prddetail.setClientNormalizer(order.getNormalizedClient());
        prddetail.setProductType(dto.getProductType());
        prddetail.setTypeOfProduct(dto.getTypeOfProduct());
        prddetail.setSize(dto.getSize());
        prddetail.setPly(ply);
        prddetail.setQuantity(qnt);

        int deckleRounded = (int) Math.floor(dkl);
        prddetail.setDeckle(deckleRounded);
        prddetail.setCuttingLength(ctl);

        prddetail.setTopMaterial(paperTop);
        prddetail.setLinerMaterial(paperBottom);
        prddetail.setFluteMaterial(paperFlute);

        prddetail.setTopGsm(topgsm);
        prddetail.setLinerGsm(linergsm);
        prddetail.setFluteGsm(flutegsm);

        prddetail.setTotalTopWeightReq(toptotalweight);
        prddetail.setTotalLinerWeightReq(linertotalweight);
        prddetail.setTotalFluteWeightReq(flutetotalweight);
        prddetail.setPlain(qnt);

        String numberOnly = ply.replaceAll("[^0-9]", "");
        int plyNumber = numberOnly.isEmpty() ? 0 : Integer.parseInt(numberOnly);

        int sheets;

        if (plyNumber == 3) {
            sheets = qnt;
            prddetail.setSheets(sheets);
            prddetail.setOnePieceSheet(qnt);
            prddetail.setOnePiecePlain(qnt);
            prddetail.setTwoPiecePlain(qnt);
            prddetail.setTwoPieceSheet(qnt);
        } else if (plyNumber == 5) {
            sheets = qnt * 2;
            prddetail.setSheets(sheets);
            prddetail.setOnePieceSheet(sheets);
            prddetail.setOnePiecePlain(qnt);
            prddetail.setTwoPiecePlain(qnt * 2);
            prddetail.setTwoPieceSheet(qnt * 4);
        } else if (plyNumber == 7) {
            sheets = qnt * 3;
            prddetail.setSheets(sheets);
            prddetail.setOnePieceSheet(sheets);
            prddetail.setOnePiecePlain(qnt);
            prddetail.setTwoPiecePlain(qnt * 2);
            prddetail.setTwoPieceSheet(qnt * 3);
        } else {
            sheets = qnt * 4;
            prddetail.setSheets(sheets);
            prddetail.setOnePieceSheet(sheets);
            prddetail.setOnePiecePlain(qnt);
            prddetail.setTwoPiecePlain(qnt * 2);
            prddetail.setTwoPieceSheet(qnt * 4);
        }

        prddetail.setTwoUpsSheets(sheets / 2);
        prddetail.setThreeUpsSheets(sheets / 3);
        prddetail.setFourUpsSheets(sheets / 4);

        prddetail.setTwoUpsPlain(qnt / 2);
        prddetail.setThreeUpsPlain(qnt / 3);
        prddetail.setFourUpsPlain(qnt / 4);

        prddetail.setTwoUpsDeckle(deckleRounded * 2);
        prddetail.setThreeUpsDeckle(deckleRounded * 3);
        prddetail.setFourUpsDeckle(deckleRounded * 4);

        prddetail.setOnePieceCuttingLength(cutLenOneUps);
        prddetail.setTwoPieceCuttingLength(cutLenTwoUps);

        Machine machine = machineRepository.findByMachineName(fluteType);
        MachineCapacityDTO machineCapacityDTO = null;

        if (machine != null) {
            prddetail.setMachineName(machine.getMachineName());
            prddetail.setMaxDeckle((int) machine.getMaxDeckle());
            prddetail.setMinDeckle((int) machine.getMinDeckle());
            prddetail.setMaxCuttingLength(String.valueOf(machine.getMaxCuttingLength()));
            prddetail.setMinCuttingLength(String.valueOf(machine.getMinCuttingLength()));

            machineCapacityDTO = new MachineCapacityDTO(
                    machine.getMachineName(),
                    machine.getMaxDeckle(),
                    machine.getMinDeckle(),
                    machine.getMaxCuttingLength(),
                    machine.getMinCuttingLength(),
                    machine.getNoOfBoxPerHour(),
                    machine.getNoOfSheetsPerHour()
            );
        }

        productionDetailRepository.save(prddetail);
        order.setProductionDetail(prddetail);
        orderRepository.save(order);

        if (sr == null) {
            return new SuggestedReelsResponseDTO(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    false,
                    "Order created using client master",
                    order.getId(),
                    machineCapacityDTO
            );
        }

        OrderSuggestedReels osr = new OrderSuggestedReels();
        osr.setOrder(order);

        List<Double> candidateDeckles = List.of(
                sr.getOneUps(),
                sr.getTwoUps(),
                sr.getThreeUps(),
                sr.getFourUps()
        );

        osr.setUsedDeckle(Collections.max(candidateDeckles));

        boolean needsFlute = sr.getFluteGsm() != sr.getLinerGsm();

        for (String layer : List.of("TOP", "BOTTOM", "FLUTE")) {

            if (layer.equals("FLUTE") && !needsFlute) continue;

            int targetGsm = switch (layer) {
                case "TOP" -> sr.getTopGsm();
                case "BOTTOM" -> sr.getBottomGsm();
                default -> sr.getFluteGsm();
            };

            String paperTypeNorm = switch (layer) {
                case "TOP" -> sr.getPaperTypeTopNorm();
                case "BOTTOM" -> sr.getPaperTypeBottomNorm();
                default -> sr.getPaperTypeFluteNorm();
            };

            List<SuggestedReelItem> items = new ArrayList<>();

            for (Double rawDeckle : candidateDeckles) {
                items.addAll(findTopReels(
                        targetGsm,
                        (int) Math.floor(rawDeckle),
                        paperTypeNorm,
                        dto.getUnit()
                ));
            }

            items = items.stream().distinct().limit(10).toList();

            switch (layer) {
                case "TOP" -> osr.getTopReels().addAll(items);
                case "BOTTOM" -> osr.getBottomReels().addAll(items);
                case "FLUTE" -> osr.getFluteReels().addAll(items);
            }
        }

        orderSuggestedReelsRepository.save(osr);

        return new SuggestedReelsResponseDTO(
                mapDTO(osr.getTopReels(), sr),
                mapDTO(osr.getBottomReels(), sr),
                mapDTO(osr.getFluteReels(), sr),
                needsFlute,
                "Order created with suggested reels",
                order.getId(),
                machineCapacityDTO
        );
    }

    public ProductionDetail getProductionDetailByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        ProductionDetail pd = order.getProductionDetail();
        if (pd == null) {
            pd = new ProductionDetail();
            pd.setOrder(order);
            productionDetailRepository.save(pd);
            order.setProductionDetail(pd);
            orderRepository.save(order);
        }
        return pd;
    }

    public SuggestedReelsResponseDTO getSuggestedReels(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            return createEmptyResponse("Order is completed. No suggestions available.");
        }

        Optional<OrderSuggestedReels> opt =
                orderSuggestedReelsRepository.findByOrder(order);

        if (opt.isEmpty()) {
            return createEmptyResponse(
                    "No suggested reels available. Production created using client master."
            );
        }

        return mapToResponse(opt.get());
    }


    public void updateOrderStatus(Long orderId, OrderStatus newStatus, String transportNumber) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        ZonedDateTime now = ZonedDateTime.now(IST_ZONE);
        order.setUpdatedAt(now);
        order.setStatus(newStatus);

        switch (newStatus) {
            case COMPLETED -> {
                order.setCompletedAt(now);
                order.setShippedAt(null);
                order.setTransportNumber(null);
            }
            case SHIPPED -> {
                order.setShippedAt(now);
                order.setTransportNumber(transportNumber);
            }
            default -> {
                order.setCompletedAt(null);
                order.setShippedAt(null);
                order.setTransportNumber(null);
            }
        }

        orderRepository.save(order);
    }

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
        order.setProductName(orderDTO.getProductName());
        order.setUpdatedAt(ZonedDateTime.now(IST_ZONE));
        order.setUnit(orderDTO.getUnit());
        order.setTransportNumber(orderDTO.getTransportNumber());

        String normalizedClient = orderDTO.getClient().toLowerCase().replaceAll("[^a-z0-9]", "");
        order.setNormalizedClient(normalizedClient);

        return orderRepository.save(order);
    }


//    private List<SuggestedReelItem> findTopReels(int gsm, int deckle, String paperType) {
//        double minDeckle = (int) Math.floor(deckle);
//        double maxDeckle = (int) Math.floor(deckle + 3.0);
//
//        List<Reel> candidates = reelRepository.findAvailableByDeckleRange(
//                minDeckle, maxDeckle,
//                List.of(ReelStatus.NOT_IN_USE, ReelStatus.PARTIALLY_USED_AVAILABLE)
//        );
//
//        // Normalize paper type for comparison (optional but good for robustness)
//        String normalizedTargetPaperType = paperType;
//
//        // Filter candidates matching all: GSM + paper type
//        List<Reel> exactMatch = candidates.stream()
//                .filter(r -> r.getGsm() == gsm)
//                .filter(r -> {
//                    String reelPaperType = r.getPaperTypeNormalized() != null ? r.getPaperTypeNormalized() : "";
//                    return reelPaperType.equals(normalizedTargetPaperType);
//                })
//                .collect(Collectors.toList());
//
//        // If no exact GSM match, try near GSM (<= 20 diff) but keep paper type fixed
//        List<Reel> nearMatch = exactMatch.isEmpty()
//                ? candidates.stream()
//                .filter(r -> r.getGsm() < gsm && (gsm - r.getGsm()) <= 20)
//                .sorted(Comparator.comparingInt(r -> gsm - r.getGsm()))
//                .toList()
//                : exactMatch;
//
//        return nearMatch.stream()
//                .sorted(Comparator.comparingDouble(r -> Math.abs(r.getDeckle() - deckle)))
//                .map(r -> new SuggestedReelItem(
//                        r.getBarcodeId(),
//                        r.getReelNo(),
//                        r.getGsm(),
//                        r.getDeckle(),
//                        r.getCurrentWeight(),
//                        r.getUnit(),
//                        r.getStatus(),
//                        r.getReelSet()
//                ))
//                .collect(Collectors.toList());
//    }

    private List<SuggestedReelItem> findTopReels(int gsm, int deckle, String paperTypeNorm, String unit) {
        if (paperTypeNorm == null || paperTypeNorm.isBlank()) {
            return Collections.emptyList();
        }

        double minDeckle = deckle - 1.0;
        double maxDeckle = deckle + 3.0;

        // First find by exact paper type match
        List<Reel> candidates = reelRepository.findAvailableByDeckleRangeAndPaperTypeNorm(
                minDeckle, maxDeckle,
                paperTypeNorm,
                List.of(ReelStatus.NOT_IN_USE, ReelStatus.PARTIALLY_USED_AVAILABLE)
        );

        // Normalize the input unit to lower case for case-insensitive comparison
        String normalizedInputUnit = (unit != null) ? unit.trim().toLowerCase() : "";

        // Filter by unit (case-insensitive)
        candidates = candidates.stream()
                .filter(r -> r.getUnit() != null && r.getUnit().trim().equalsIgnoreCase(normalizedInputUnit))
                .collect(Collectors.toList());

        // Then filter by GSM
        List<Reel> exactMatch = candidates.stream()
                .filter(r -> r.getGsm() == gsm)
                .sorted(Comparator.comparingDouble(r -> Math.abs(r.getDeckle() - deckle)))
                .collect(Collectors.toList());

        // If no exact GSM match, try near GSM
        if (exactMatch.isEmpty()) {
            exactMatch = candidates.stream()
                    .filter(r -> Math.abs(r.getGsm() - gsm) <= 20) // ±20 GSM tolerance
                    .sorted(Comparator
                            .comparingInt((Reel r) -> Math.abs(r.getGsm() - gsm))
                            .thenComparingDouble(r -> Math.abs(r.getDeckle() - deckle)))
                    .collect(Collectors.toList());
        }

        return exactMatch.stream()
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

    private List<SuggestedReelDTO> mapDTO(List<SuggestedReelItem> items, SuggestedReel sr) {
        return items == null ? Collections.emptyList() : items.stream()
                .map(i -> {
                    SuggestedReelDTO dto = new SuggestedReelDTO();
                    dto.setBarcodeId(i.getBarcodeId());
                    dto.setReelNo(i.getReelNo());
                    dto.setGsm(i.getGsm());
                    dto.setDeckle(i.getDeckle());
                    dto.setClient(sr.getClient());
                    dto.setClientNormalizer(sr.getClientNormalizer());
                    dto.setProduct(sr.getProduct());
                    dto.setSize(sr.getSize());
                    dto.setPly(sr.getPly());
                    dto.setTopGsm(sr.getTopGsm());
                    dto.setLinerGsm(sr.getLinerGsm());
                    dto.setFluteGsm(sr.getFluteGsm());
                    dto.setMadeUpOf(sr.getMadeUpOf());
                    dto.setPaperTypeTop(sr.getPaperTypeTop());
                    dto.setPaperTypeBottom(sr.getPaperTypeBottom());
                    dto.setCurrentWeight(i.getCurrentWeight());
                    dto.setUnit(i.getUnit());
                    dto.setReelSet(i.getReelSet());
                    dto.setStatus(i.getStatus());
                    dto.setCuttingLength(sr.getCuttingLength());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<SuggestedReelDTO> safeMapToDTOs(List<SuggestedReelItem> items) {
        return items == null ? Collections.emptyList() :
                items.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private SuggestedReelDTO mapToDTO(SuggestedReelItem i) {
        SuggestedReelDTO dto = new SuggestedReelDTO();
        dto.setBarcodeId(i.getBarcodeId());
        dto.setReelNo(i.getReelNo());
        dto.setGsm(i.getGsm());
        dto.setDeckle(i.getDeckle());
        dto.setCurrentWeight(i.getCurrentWeight());
        dto.setUnit(i.getUnit());
        dto.setReelSet(i.getReelSet());
        dto.setStatus(i.getStatus());
        return dto;
    }

    private SuggestedReelsResponseDTO createEmptyResponse(String message) {
        SuggestedReelsResponseDTO response = new SuggestedReelsResponseDTO();
        response.setTopGsmReels(Collections.emptyList());
        response.setBottomGsmReels(Collections.emptyList());
        response.setFluteGsmReels(Collections.emptyList());
        response.setFluteRequired(false);
        response.setMessage(message);
        response.setOrderid(0);
        return response;
    }

    @Transactional
    public void splitOrder(Long originalOrderId, int firstPartQuantity, int secondPartQuantity) {
        Order original = orderRepository.findById(originalOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (firstPartQuantity <= 0 || secondPartQuantity <= 0 || (firstPartQuantity + secondPartQuantity) != original.getQuantity()) {
            throw new RuntimeException("Split quantities are invalid.");
        }

        // Update original order with first part
        original.setQuantity(firstPartQuantity);
        orderRepository.save(original);

        // Create a new order by copying data
        Order newOrder = new Order();
        BeanUtils.copyProperties(original, newOrder,
                "id", "createdAt", "updatedAt", "completedAt", "shippedAt", "transportNumber", "productionDetail");

        // Reset unique/sensitive fields and timestamps for the new order
        newOrder.setQuantity(secondPartQuantity);
        newOrder.setCreatedAt(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
        newOrder.setUpdatedAt(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
        newOrder.setTransportNumber(null);
        newOrder.setCompletedAt(null);
        newOrder.setShippedAt(null);

        // Explicitly handle related ProductionDetail object
        if (original.getProductionDetail() != null) {
            ProductionDetail newProductionDetail = new ProductionDetail();
            BeanUtils.copyProperties(original.getProductionDetail(), newProductionDetail, "id", "order");
            newOrder.setProductionDetail(newProductionDetail);
            newProductionDetail.setOrder(newOrder); // Link back to the new order
        }

        orderRepository.save(newOrder);
    }

    public List<OrderToDoListDTO> getOrdersByActiveStatus() {

        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.TODO,
                OrderStatus.IN_PROGRESS,
                OrderStatus.COMPLETED
        );

        List<Order> activeOrders =
                orderRepository.findByStatusIn(activeStatuses);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Chicago"));
        ZonedDateTime cutoff = now.minusDays(1);

        List<Order> recentShippedOrders =
                orderRepository.findByStatusAndShippedAtAfter(
                        OrderStatus.SHIPPED,
                        cutoff
                );

        activeOrders.addAll(recentShippedOrders);

        return activeOrders.stream()
                .map(this::mapToDTO)
                .toList();
    }

    private OrderToDoListDTO mapToDTO(Order order) {

        OrderToDoListDTO dto = new OrderToDoListDTO();

        // 🔹 Order data
        dto.setClient(order.getClient());
        dto.setProductType(order.getProductType());
        dto.setTypeOfProduct(order.getTypeOfProduct());
        dto.setProductName(order.getProductName());
        dto.setQuantity(order.getQuantity());
        dto.setSize(order.getSize());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setOrderCreatedDate(order.getOrderCreatedDate());
        dto.setExpectedCompletionDate(order.getExpectedCompletionDate());
        dto.setCreatedBy(order.getCreatedBy());
        dto.setCompletedAt(order.getCompletedAt());
        dto.setShippedAt(order.getShippedAt());
        dto.setUnit(order.getUnit());
        dto.setTransportNumber(order.getTransportNumber());
        dto.setNormalizedClient(order.getNormalizedClient());
        dto.setProductionDetail(order.getProductionDetail());
        dto.setId(order.getId());

        // 🔹 Client master lookup
        clientRepository
                .findByClientNormalizerAndSize(
                        order.getNormalizedClient(),
                        order.getSize()
                )
                .ifPresent(client -> {
                    dto.setDeckle((int) client.getDeckle());
                    dto.setTopGsm(client.getTopGsm());
                    dto.setLinerGsm(client.getLinerGsm());
                    dto.setFluteGsm(client.getFluteGsm());
                });

        return dto;
    }
}