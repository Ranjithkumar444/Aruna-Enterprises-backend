package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Admin;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStockAlert;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStockThreshold;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AdminRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelStockAlertRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelStockThresholdRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReelStockAlertService {

    @Autowired
    private ReelRepository reelRepository;
    @Autowired private ReelStockThresholdRepository thresholdRepository;
    @Autowired private ReelStockAlertRepository alertRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired private JavaMailSender javaMailSender;

    @Transactional
    public void checkAndGenerateStockAlerts() {
        List<Object[]> groupedData = reelRepository.getGroupedWeightByDeckleGsmAndUnit();

        for (Object[] row : groupedData) {
            int deckle = (int) row[0];
            int gsm = (int) row[1];
            String unit = (String) row[2];
            long totalWeight = (long) row[3];

            Optional<ReelStockThreshold> thresholdOpt = thresholdRepository.findByDeckleAndGsmAndUnit(deckle, gsm, unit);

            if (thresholdOpt.isPresent()) {
                int threshold = thresholdOpt.get().getMinWeightThreshold();
                if (totalWeight < threshold) {
                    ReelStockAlert alert = new ReelStockAlert(null, deckle, gsm, (int) totalWeight, threshold, unit, LocalDateTime.now(), false);
                    alertRepository.save(alert);
                    sendStockAlertEmail(deckle, gsm, totalWeight, threshold, unit);
                }
            }
        }
    }

    private void sendStockAlertEmail(int deckle, int gsm, long weight, int threshold, String unit) {
        List<Admin> admins = adminRepository.findAll();

        for (Admin admin : admins) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(admin.getEmail());
                message.setSubject("Low Reel Stock Alert - Unit " + unit);
                message.setText("Stock is low in Unit " + unit +
                        " for Deckle: " + deckle + ", GSM: " + gsm +
                        "\nCurrent Weight: " + weight + " kg\nThreshold: " + threshold + " kg");

                javaMailSender.send(message);
            } catch (Exception e) {
                e.printStackTrace(); // Consider logging
            }
        }
    }
}
