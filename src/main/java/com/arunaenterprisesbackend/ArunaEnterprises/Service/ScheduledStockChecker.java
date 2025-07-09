package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledStockChecker {

    @Autowired
    private ReelStockAlertService reelStockAlertService;


    @Scheduled(cron = "0 0 * * * *")
    public void checkReelStockEveryHour() {
        System.out.println("Running scheduled stock alert check...");
        reelStockAlertService.checkAndGenerateStockAlerts();
    }
}
