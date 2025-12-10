package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.UpdateRequest;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.A;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.InventoryHistory;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class InventoryService {

    @Autowired
    private ARepository aRepository;
    @Autowired
    private BRepository bRepository;
    @Autowired
    private CRepository cRepository;
    @Autowired
    private DRepository dRepository;

    @Autowired
    private InventoryHistoryRepository historyRepo;

    // --- HISTORY METHOD GOES HERE ---
    public void saveHistory(String unit, String product, int oldCount, int newCount) {
        InventoryHistory h = new InventoryHistory();
        h.setUnit(unit);
        h.setProduct(product);
        h.setOldCount(oldCount);
        h.setNewCount(newCount);
        h.setDelta(newCount - oldCount);
        h.setUpdatedAt(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
        historyRepo.save(h);
    }

    // Example update method for Unit A
    @Transactional
    public A updateA(UpdateRequest req) {
        A item = aRepository.findByProduct(req.getProduct());

        if (item == null) {
            throw new RuntimeException("Product not found in unit A: " + req.getProduct());
        }

        int oldCount = item.getCount();
        item.setCount(req.getCount());

        A saved = aRepository.save(item);

        saveHistory("A", req.getProduct(), oldCount, req.getCount());

        return saved;
    }

    // similar methods for B, C, D...
}
