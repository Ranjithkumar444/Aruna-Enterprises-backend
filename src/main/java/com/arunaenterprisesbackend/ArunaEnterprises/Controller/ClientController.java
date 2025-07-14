package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Clients;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.SuggestedReel;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ClientRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.SuggestedReelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SuggestedReelRepository suggestedReelRepository;


    @PostMapping("/client/order/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> clientCreate(@RequestBody Clients clients) {
        Clients client = new Clients();

        client.setClient(clients.getClient());

        String normalizedClient = clients.getClient().toLowerCase().replaceAll("[^a-z0-9]", "");
        client.setClientNormalizer(normalizedClient);
        client.setFluteGsm(clients.getFluteGsm());
        client.setLinerGsm(clients.getLinerGsm());
        client.setMadeUpOf(clients.getMadeUpOf());
        client.setPaperTypeBottom(clients.getPaperTypeBottom());
        client.setPaperTypeTop(clients.getPaperTypeTop());
        client.setPly(clients.getPly());
        client.setTopGsm(clients.getTopGsm());
        client.setProduct(clients.getProduct());
        client.setSize(clients.getSize());
        client.setPaperTypeFlute(clients.getPaperTypeFlute());
        client.setBottomGsm(clients.getLinerGsm());

        String size = clients.getSize();
        String[] dimensions = size.split("X");

        int[] arr = new int[dimensions.length];
        for (int i = 0; i < dimensions.length; i++) {
            arr[i] = Integer.parseInt(dimensions[i]);
        }

        String productType = clients.getProductType().toLowerCase().replaceAll("[^a-z0-9]", "");
        client.setProductType(productType);  // Always set this

        if (productType.equals("punching") || productType.equals("Punching")) {
            // For punching: take values from client input
            client.setCuttingLength(clients.getCuttingLength());
            client.setDeckle(clients.getDeckle());
            client.setOneUps(clients.getDeckle());
            client.setTwoUps(clients.getDeckle() * 2);
            client.setThreeUps(clients.getDeckle() * 3);
            client.setFourUps(clients.getDeckle() * 4);
        } else {
            // For corrugated: calculate values
            int l = arr[0];
            int w = arr[1];
            int h = arr.length > 2 ? arr[2] : 0; // safely fallback

            String plyStr = clients.getPly();
            int plyNo = 0;

            if (plyStr != null && plyStr.contains("-")) {
                String[] parts = plyStr.split("-");
                try {
                    plyNo = Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format in ply: " + plyStr);
                }
            }

            int dec = (plyNo == 9 || plyNo == 11) ? (w + h + 30) : (w + h + 20);
            double finalDeckle = dec / 10.0;

            client.setDeckle(finalDeckle);
            client.setOneUps(finalDeckle);
            client.setTwoUps(finalDeckle * 2);
            client.setThreeUps(finalDeckle * 3);
            client.setFourUps(finalDeckle * 4);

            // Use the calculated deckle here
            double cuttingLength;
            if (finalDeckle > 65.0) {
                cuttingLength = (l + w + 50) / 10.0;
            } else {
                cuttingLength = (2 * l + 2 * w + 50) / 10.0;
            }

            client.setCuttingLength(cuttingLength);
        }

        client.setDescription(clients.getDescription());

        client.setSellingPricePerBox(clients.getSellingPricePerBox());

        client.setProductionCostPerBox(clients.getProductionCostPerBox());

        // Save client
        clientRepository.save(client);

        // Create SuggestedReel from same values
        SuggestedReel reel = new SuggestedReel();
        reel.setClient(client.getClient());
        reel.setClientNormalizer(client.getClientNormalizer());
        reel.setProduct(client.getProduct());
        reel.setSize(client.getSize());
        reel.setPly(client.getPly());
        reel.setDeckle(client.getDeckle());
        reel.setCuttingLength(client.getCuttingLength());
        reel.setBottomGsm(client.getLinerGsm());
        reel.setTopGsm(client.getTopGsm());
        reel.setLinerGsm(client.getLinerGsm());
        reel.setFluteGsm(client.getFluteGsm());
        reel.setMadeUpOf(client.getMadeUpOf());
        reel.setPaperTypeTop(client.getPaperTypeTop());
        reel.setPaperTypeBottom(client.getPaperTypeBottom());
        reel.setOneUps(client.getOneUps());
        reel.setTwoUps(client.getTwoUps());
        reel.setThreeUps(client.getThreeUps());
        reel.setPaperTypeFlute(client.getPaperTypeFlute());
        reel.setFourUps(client.getFourUps());
        reel.setDescription(client.getDescription());
        reel.setProductionCostPerBox(client.getProductionCostPerBox());
        reel.setSellingPricePerBox(clients.getSellingPricePerBox());

        suggestedReelRepository.save(reel);

        return ResponseEntity.ok("Client and Suggested Reel created successfully.");
    }

    @GetMapping("/getAllClients")
    public ResponseEntity<List<Clients>> getAllClients(){
        List<Clients> client = clientRepository.findAll();

        return ResponseEntity.ok(client);
    }

}
