package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.PaperPrice;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.PaperPriceRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaperPriceService {

    private final ReelRepository reelRepository;
    private final PaperPriceRepository paperPriceRepository;

    @Transactional
    public void syncPaperPriceCombinations() {

        List<Object[]> combinations = reelRepository.findDistinctPaperCombinations();

        // Load existing combinations
        Set<String> existingKeys = paperPriceRepository.findAll()
                .stream()
                .map(p -> normalize(p.getPaperTypeNormalized()) + "-" + p.getGsm() + "-" + p.getBf())
                .collect(Collectors.toSet());

        for (Object[] obj : combinations) {

            String paperType = (String) obj[0];
            String normalizedRaw = (String) obj[1];
            Integer gsm = (Integer) obj[2];
            Integer bf = (Integer) obj[3];

            if (normalizedRaw == null || gsm == null || bf == null) continue;

            // ✅ Normalize properly
            String normalized = normalize(normalizedRaw);

            String key = normalized + "-" + gsm + "-" + bf;

            // ✅ In-memory check
            if (!existingKeys.contains(key)) {

                // ✅ DB check (FINAL SAFETY)
                boolean existsInDb = paperPriceRepository
                        .findByPaperTypeNormalizedAndGsmAndBf(normalized, gsm, bf)
                        .isPresent();

                if (!existsInDb) {

                    PaperPrice paperPrice = new PaperPrice();
                    paperPrice.setPaperType(paperType);
                    paperPrice.setPaperTypeNormalized(normalized);
                    paperPrice.setGsm(gsm);
                    paperPrice.setBf(bf);
                    paperPrice.setPricePerKg(0.0);

                    paperPriceRepository.save(paperPrice);

                    // ✅ VERY IMPORTANT (fix for your bug)
                    existingKeys.add(key);
                }
            }
        }
    }

    // 🔥 Reusable normalization method
    private String normalize(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }
}