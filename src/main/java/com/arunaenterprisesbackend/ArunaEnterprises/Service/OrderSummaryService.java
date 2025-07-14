package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderSummaryService {

    private final OrderRepository orderRepository;
    private final OrderReelUsageRepository orderReelUsageRepository;
    private final DailyOrderSummaryRepository dailyOrderSummaryRepository;
    private final ReelUsageHistoryRepository reelUsageHistoryRepository;
    private final ReelRepository reelRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Transactional
    public void generateDailySummary(LocalDate date) {
        ZonedDateTime startOfDay = date.atStartOfDay(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime endOfDay = date.plusDays(1).atStartOfDay(ZoneId.of("Asia/Kolkata"));


        // Check if summary already exists
        if (dailyOrderSummaryRepository.findBySummaryDate(date).isPresent()) {
            return;
        }

        List<Order> shippedOrders = orderRepository.findByStatusAndShippedAtBetween(
                OrderStatus.SHIPPED, startOfDay, endOfDay);


        if (shippedOrders.isEmpty()) {
            return;
        }

        DailyOrderSummary summary = new DailyOrderSummary();
        summary.setSummaryDate(date);
        summary.setTotalOrdersShipped(shippedOrders.size());

        double totalWeight = 0;
        double totalProfit = 0;
        double totalRevenue = 0;
        double totalWastage = 0;
        List<OrderSummaryDetail> details = new ArrayList<>();

        for (Order order : shippedOrders) {
            OrderSummaryDetail detail = processOrder(order);
            details.add(detail);

            totalWeight += detail.getTotalWeightConsumed();
            totalProfit += detail.getProfit();
            totalRevenue += detail.getRevenue();
            totalWastage += detail.getTotalReelWastage();
        }

        summary.setTotalWeightConsumed(totalWeight);
        summary.setOrderDetails(details);

        // Set financial metrics
        summary.setTotalProfitOfDay(totalProfit);
        summary.setTotalRevenueOfDay(totalRevenue);
        summary.setTotalReelWastageOfDay(totalWastage);

        if(totalRevenue > 0) {
            summary.setTotalProfitOfDayPercentage(
                    String.format("%.2f%%", (totalProfit/totalRevenue)*100));
            summary.setTotalRevenueOfDayPercentage("100.00%");
        }

        if(totalWeight > 0) {
            summary.setTotalReelWastageOfDayPercentage(
                    String.format("%.2f%%", (totalWastage/totalWeight)*100));
        }

        dailyOrderSummaryRepository.save(summary);
    }

    public List<ReelUsageHistory> getReelUsageHistory(ZonedDateTime start, ZonedDateTime end) {
        return reelUsageHistoryRepository.findByUsedAtBetween(start, end);
    }

    private OrderSummaryDetail processOrder(Order order) {
        OrderSummaryDetail detail = new OrderSummaryDetail();
        detail.setOrder(order);

        List<OrderReelUsage> usages = orderReelUsageRepository.findByOrder(order);
        List<ReelUsageSummary> reelSummaries = new ArrayList<>();

        double topWeight = 0;
        double linerWeight = 0;
        double fluteWeight = 0;
        double totalWeight = 0;
        double totalWastage = 0;

        // Calculate financials for the order
        Optional<Clients> clientOpt = clientRepository.findByClientNormalizerAndSize(
                order.getNormalizedClient(), order.getSize());

        if(clientOpt.isPresent()) {
            Clients client = clientOpt.get();
            double revenue = client.getSellingPricePerBox() * order.getQuantity();
            double productionCost = client.getProductionCostPerBox() * order.getQuantity();
            double profit = revenue - productionCost;

            detail.setRevenue(revenue);
            detail.setProfit(profit);
            detail.setProfitPercentage(String.format("%.2f%%", (profit/revenue)*100));
            detail.setRevenuePercentage("100.00%");
        }

        for (OrderReelUsage usage : usages) {
            ReelUsageSummary reelSummary = new ReelUsageSummary();
            reelSummary.setReelNo(usage.getReel().getReelNo());
            reelSummary.setBarcodeId(usage.getReel().getBarcodeId());
            reelSummary.setReelSet(usage.getReel().getReelSet());
            reelSummary.setWeightConsumed(usage.getWeightConsumed());
            reelSummary.setUsageType(usage.getUsageType());

            // Calculate reel wastage
            double wastagePercent = calculateReelWastagePercent(usage);
            double wastageKg = (usage.getWeightConsumed() * wastagePercent) / 100;

            reelSummary.setReelWastage(wastageKg);
            reelSummary.setReelWastagePercentage(String.format("%.2f%%", wastagePercent));
            totalWastage += wastageKg;

            reelSummaries.add(reelSummary);
            totalWeight += usage.getWeightConsumed();

            // Update reel usage history
            ReelUsageHistory history = new ReelUsageHistory();
            history.setReelNo(usage.getReel().getReelNo());
            history.setBarcodeId(usage.getReel().getBarcodeId());
            history.setUsedWeight(usage.getWeightConsumed());
            history.setUsedAt(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
            history.setUsedBy(usage.getRecordedBy());
            history.setReelSet(usage.getReel().getReelSet());
            history.setBoxDetails("Boxes: " + usage.getHowManyBox());
            history.setUsageType(usage.getUsageType());
            reelUsageHistoryRepository.save(history);

            // Update reel status and weight
            Reel reel = usage.getReel();
            reel.setPreviousWeight(reel.getCurrentWeight());
            reel.setCurrentWeight(reel.getCurrentWeight() - (int) usage.getWeightConsumed());

            if (reel.getCurrentWeight() <= 12) {
                reel.setStatus(ReelStatus.USE_COMPLETED);
            } else {
                reel.setStatus(ReelStatus.PARTIALLY_USED_AVAILABLE);
            }
            reelRepository.save(reel);

            // Sum weights by type
            switch (usage.getUsageType().toUpperCase()) {
                case "TOP" -> topWeight += usage.getWeightConsumed();
                case "BOTTOM", "LINER" -> linerWeight += usage.getWeightConsumed();
                case "FLUTE" -> fluteWeight += usage.getWeightConsumed();
            }
        }

        detail.setTopWeightConsumed(topWeight);
        detail.setLinerWeightConsumed(linerWeight);
        detail.setFluteWeightConsumed(fluteWeight);
        detail.setTotalWeightConsumed(totalWeight);
        detail.setReelUsages(reelSummaries);
        detail.setTotalReelWastage(totalWastage);

        if(totalWeight > 0) {
            detail.setTotalReelWastagePercentage(
                    String.format("%.2f%%", (totalWastage/totalWeight)*100));
        }

        return detail;
    }

    private double calculateReelWastagePercent(OrderReelUsage usage) {
        // Implement your specific wastage calculation logic here
        // This is just an example - adjust based on your business rules

        if(usage.getUsageType().equalsIgnoreCase("FLUTE")) {
            return 5.0; // 5% wastage for flute
        } else if(usage.getUsageType().equalsIgnoreCase("TOP")) {
            return 3.0; // 3% wastage for top
        } else {
            return 2.5; // 2.5% wastage for liner/bottom
        }
    }

    public DailyOrderSummaryDTO getDailySummary(LocalDate date) {
        DailyOrderSummary summary = dailyOrderSummaryRepository.findBySummaryDate(date)
                .orElseThrow(() -> new RuntimeException("No summary found for date: " + date));
        return mapToDTO(summary);
    }

    public List<DailyOrderSummaryDTO> getSummaryBetweenDates(LocalDate start, LocalDate end) {
        List<DailyOrderSummary> summaries = dailyOrderSummaryRepository.findBySummaryDateBetween(start, end);
        return summaries.stream().map(this::mapToDTO).toList();
    }

    private DailyOrderSummaryDTO mapToDTO(DailyOrderSummary summary) {
        DailyOrderSummaryDTO dto = new DailyOrderSummaryDTO();
        dto.setId(summary.getId());
        dto.setSummaryDate(summary.getSummaryDate());
        dto.setTotalOrdersShipped(summary.getTotalOrdersShipped());
        dto.setTotalWeightConsumed(summary.getTotalWeightConsumed());
        dto.setCreatedAt(summary.getCreatedAt());

        // Map new fields
        dto.setTotalProfitOfDay(summary.getTotalProfitOfDay());
        dto.setTotalRevenueOfDay(summary.getTotalRevenueOfDay());
        dto.setTotalProfitOfDayPercentage(summary.getTotalProfitOfDayPercentage());
        dto.setTotalRevenueOfDayPercentage(summary.getTotalRevenueOfDayPercentage());
        dto.setTotalReelWastageOfDay(summary.getTotalReelWastageOfDay());
        dto.setTotalReelWastageOfDayPercentage(summary.getTotalReelWastageOfDayPercentage());

        List<OrderSummaryDetailDTO> detailDTOs = summary.getOrderDetails().stream()
                .map(this::mapDetailToDTO)
                .toList();
        dto.setOrderDetails(detailDTOs);

        return dto;
    }

    private OrderSummaryDetailDTO mapDetailToDTO(OrderSummaryDetail detail) {
        OrderSummaryDetailDTO dto = new OrderSummaryDetailDTO();
        dto.setOrderId(detail.getOrder().getId());
        dto.setClient(detail.getOrder().getClient());
        dto.setProductType(detail.getOrder().getProductType());
        dto.setQuantity(detail.getOrder().getQuantity());
        dto.setSize(detail.getOrder().getSize());
        dto.setTopWeightConsumed(detail.getTopWeightConsumed());
        dto.setLinerWeightConsumed(detail.getLinerWeightConsumed());
        dto.setFluteWeightConsumed(detail.getFluteWeightConsumed());
        dto.setTotalWeightConsumed(detail.getTotalWeightConsumed());

        // Map new fields
        dto.setProfit(detail.getProfit());
        dto.setProfitPercentage(detail.getProfitPercentage());
        dto.setRevenue(detail.getRevenue());
        dto.setRevenuePercentage(detail.getRevenuePercentage());
        dto.setTotalReelWastage(detail.getTotalReelWastage());
        dto.setTotalReelWastagePercentage(detail.getTotalReelWastagePercentage());

        List<ReelUsageSummaryDTO> reelDTOs = detail.getReelUsages().stream()
                .map(this::mapReelUsageToDTO)
                .toList();
        dto.setReelUsages(reelDTOs);

        return dto;
    }

    private ReelUsageSummaryDTO mapReelUsageToDTO(ReelUsageSummary reel) {
        ReelUsageSummaryDTO dto = new ReelUsageSummaryDTO();
        dto.setReelNo(reel.getReelNo());
        dto.setBarcodeId(reel.getBarcodeId());
        dto.setReelSet(reel.getReelSet());
        dto.setWeightConsumed(reel.getWeightConsumed());
        dto.setUsageType(reel.getUsageType());

        // Map new fields
        dto.setReelWastage(reel.getReelWastage());
        dto.setReelWastagePercentage(reel.getReelWastagePercentage());

        return dto;
    }


    public FinancialMetricsDTO getFinancialMetrics(LocalDate start, LocalDate end) {
        List<DailyOrderSummary> summaries = dailyOrderSummaryRepository
                .findBySummaryDateBetween(start, end);

        FinancialMetricsDTO dto = new FinancialMetricsDTO();
        double totalRevenue = 0;
        double totalProfit = 0;
        List<FinancialMetricsDTO.DailyFinancialData> dailyData = new ArrayList<>();

        for(DailyOrderSummary summary : summaries) {
            totalRevenue += summary.getTotalRevenueOfDay();
            totalProfit += summary.getTotalProfitOfDay();

            dailyData.add(new FinancialMetricsDTO.DailyFinancialData(
                    summary.getSummaryDate(),
                    summary.getTotalRevenueOfDay(),
                    summary.getTotalProfitOfDay()
            ));
        }

        dto.setTotalRevenue(totalRevenue);
        dto.setTotalProfit(totalProfit);
        dto.setProfitMargin(totalRevenue > 0 ? (totalProfit/totalRevenue)*100 : 0);
        dto.setDailyData(dailyData);

        return dto;
    }

    public WastageMetricsDTO getWastageMetrics(LocalDate start, LocalDate end) {
        List<DailyOrderSummary> summaries = dailyOrderSummaryRepository
                .findBySummaryDateBetween(start, end);

        WastageMetricsDTO dto = new WastageMetricsDTO();
        double totalWastage = 0;
        double totalWeight = 0;
        Map<String, Double> wastageByType = new HashMap<>();
        wastageByType.put("TOP", 0.0);
        wastageByType.put("LINER", 0.0);
        wastageByType.put("FLUTE", 0.0);

        List<WastageMetricsDTO.DailyWastageData> dailyData = new ArrayList<>();

        for(DailyOrderSummary summary : summaries) {
            totalWastage += summary.getTotalReelWastageOfDay();
            totalWeight += summary.getTotalWeightConsumed();

            dailyData.add(new WastageMetricsDTO.DailyWastageData(
                    summary.getSummaryDate(),
                    summary.getTotalReelWastageOfDay(),
                    Double.parseDouble(summary.getTotalReelWastageOfDayPercentage().replace("%", ""))
            ));

            for(OrderSummaryDetail detail : summary.getOrderDetails()) {
                for(ReelUsageSummary reel : detail.getReelUsages()) {
                    String type = reel.getUsageType().toUpperCase();
                    wastageByType.put(type, wastageByType.getOrDefault(type, 0.0) + reel.getReelWastage());
                }
            }
        }

        dto.setTotalWastage(totalWastage);
        dto.setAverageWastagePercent(totalWeight > 0 ? (totalWastage/totalWeight)*100 : 0);
        dto.setWastageByMaterialType(wastageByType);
        dto.setDailyData(dailyData);

        return dto;
    }
}