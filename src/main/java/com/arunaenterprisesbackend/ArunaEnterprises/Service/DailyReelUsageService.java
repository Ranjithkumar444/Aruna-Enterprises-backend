package com.arunaenterprisesbackend.ArunaEnterprises.Service;


import com.arunaenterprisesbackend.ArunaEnterprises.DTO.DailyReelUsageDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderReelUsage;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderReelUsageRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DailyReelUsageService {

    private final OrderReelUsageRepository orderReelUsageRepository;

    public DailyReelUsageService(OrderReelUsageRepository orderReelUsageRepository) {
        this.orderReelUsageRepository = orderReelUsageRepository;
    }

    /**
     * Retrieves daily reel usage data, showing individual reel usage entries
     * for orders that were SHIPPED, within a specified date range.
     * This provides a detailed list that can be aggregated further on the client side.
     *
     * @param startDate The start date for the usage records (inclusive, based on courgationOut).
     * @param endDate   The end date for the usage records (inclusive, based on courgationOut).
     * @return A list of DailyReelUsageDTOs.
     */
    public List<DailyReelUsageDTO> getDailyReelUsagesForShippedOrders(ZonedDateTime startDate, ZonedDateTime endDate) {
        // Use the repository method to find usages in the period where the order status is SHIPPED
        List<OrderReelUsage> usageRecords = orderReelUsageRepository.findUsagesInPeriod(startDate, endDate);

        // Map the entity records to DTOs
        return usageRecords.stream()
                .map(this::convertToDailyReelUsageDTO)
                .collect(Collectors.toList());
    }

    private DailyReelUsageDTO convertToDailyReelUsageDTO(OrderReelUsage oru) {
        DailyReelUsageDTO dto = new DailyReelUsageDTO();
        dto.setOrderReelUsageId(oru.getId());
        dto.setWeightConsumed(oru.getWeightConsumed());
        dto.setCourgationIn(oru.getCourgationIn());
        dto.setCourgationOut(oru.getCourgationOut());
        dto.setPreviousWeight(oru.getPreviousWeight()); // Assuming this is int in DTO as well.
        dto.setUsageType(oru.getUsageType());
        dto.setRecordedBy(oru.getRecordedBy());

        // Populate from Order entity
        if (oru.getOrder() != null) {
            dto.setOrderId(oru.getOrder().getId());
            dto.setClientName(oru.getOrder().getClient());
            dto.setProductType(oru.getOrder().getProductType());
            dto.setOrderCreatedDate(oru.getOrder().getOrderCreatedDate());
            dto.setTypeOfProduct(oru.getOrder().getTypeOfProduct());
            dto.setProductName(oru.getOrder().getProductName());
        }

        // Populate from Reel entity
        if (oru.getReel() != null) {
            dto.setReelId(oru.getReel().getId());
            dto.setReelBarcodeId(oru.getReel().getBarcodeId()); // Assuming Reel has barcodeId
            dto.setReelNo(Math.toIntExact(oru.getReel().getReelNo()));
            dto.setBf(oru.getReel().getBurstFactor());
            dto.setDeckle(oru.getReel().getDeckle());
            dto.setCurrentWeight(oru.getReel().getCurrentWeight());
            dto.setGsm(oru.getReel().getGsm());
            dto.setPaperType(oru.getReel().getPaperType());
            dto.setInitialWeight(oru.getReel().getInitialWeight());
        }

        return dto;
    }
}