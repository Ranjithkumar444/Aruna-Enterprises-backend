package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.CourgatedClientDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.PunchingClientDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Clients;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Machine;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.SuggestedReel;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ClientRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.MachineRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.SuggestedReelRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SuggestedReelRepository suggestedReelRepository;

    @Autowired
    private MachineRepository machineRepository;

    @PostMapping("/client/corrugated/order/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> CourgatedClientCreate(@RequestBody CourgatedClientDTO courgatedClientDTO){
        Clients client = new Clients();

        Machine machine = machineRepository.findByMachineName("Courgation");

        if(machine == null){
            machine = machineRepository.findByMachineName("courgation");
        }

        client.setClient(courgatedClientDTO.getClient());

        String normalizedClient = courgatedClientDTO.getClient().toLowerCase().replaceAll("[^a-z0-9]", "");
        String normalizedProduct = courgatedClientDTO.getProduct().toLowerCase().replaceAll("[^a-z0-9]", "");
        client.setProductNormalizer(normalizedProduct);
        client.setClientNormalizer(normalizedClient);
        client.setFluteGsm(courgatedClientDTO.getFluteGsm());
        client.setLinerGsm(courgatedClientDTO.getLinerGsm());
        client.setMadeUpOf(courgatedClientDTO.getMadeUpOf());
        client.setPaperTypeBottom(courgatedClientDTO.getPaperTypeBottom());
        client.setPaperTypeTop(courgatedClientDTO.getPaperTypeTop());
        client.setPly(courgatedClientDTO.getPly());
        client.setTopGsm(courgatedClientDTO.getTopGsm());
        client.setProduct(courgatedClientDTO.getProduct());
        client.setSize(courgatedClientDTO.getSize());
        client.setPaperTypeFlute(courgatedClientDTO.getPaperTypeFlute());
        client.setBottomGsm(courgatedClientDTO.getLinerGsm());
        client.setProductionCostPerBox(courgatedClientDTO.getProductionCostPerBox());
        client.setSellingPricePerBox(courgatedClientDTO.getSellingPricePerBox());

        client.setPaperTypeTopNorm(courgatedClientDTO.getPaperTypeTop().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setPaperTypeBottomNorm(courgatedClientDTO.getPaperTypeBottom().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setPaperTypeFluteNorm(courgatedClientDTO.getPaperTypeFlute().toLowerCase().replaceAll("[^a-z0-9]", ""));

        String size = courgatedClientDTO.getSize();
        String[] dimensions = size.split("X");

        int[] arr = new int[dimensions.length];
        for (int i = 0; i < dimensions.length; i++) {
            arr[i] = Integer.parseInt(dimensions[i]);
        }

        client.setProductType("corrugated");

        int l = arr[0];
        int w = arr[1];
        int h = arr.length > 2 ? arr[2] : 0;

        String plyStr = courgatedClientDTO.getPly();
        int plyNo = 0;

        if (plyStr != null && plyStr.contains("-")) {
            String[] parts = plyStr.split("-");
            try {
                plyNo = Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format in ply: " + plyStr);
            }
        }

        int dec = (plyNo == 9 || plyNo == 11 || plyNo == 7) ? (w + h + 30) : (w + h + 20);
        double finalDeckle;

        if(plyNo == 7 || plyNo == 9){
            finalDeckle = Math.round((dec)/10.0);
        }else{
            finalDeckle = Math.floor((dec)/10.0);
        }

        client.setDeckle(finalDeckle);
        client.setOneUps(finalDeckle);
        client.setTwoUps(finalDeckle * 2);
        client.setThreeUps(finalDeckle * 3);
        client.setFourUps(finalDeckle * 4);
        client.setFiveUps(finalDeckle*5);
        client.setSixUps(finalDeckle*6);

        double cuttingLength;
        double baseCuttingLength = (2  * ( l +  w ) + 50 ) / 10.0;
        if(baseCuttingLength <= machine.getMinCuttingLength()){
            client.setPiece("Two Ups Cutting Length");
            cuttingLength = baseCuttingLength * 2;
        }
        else if(baseCuttingLength >= machine.getMaxCuttingLength()){
            cuttingLength = (l + w + 50) / 10.0;
            client.setPiece("Two Piece Cutting Length");
        }
        else {
            cuttingLength = baseCuttingLength;
            client.setPiece("One Ups Cutting Length");
        }
        client.setCuttingLength(cuttingLength);
        client.setCuttingLengthOneUps(cuttingLength);
        client.setCuttingLengthTwoUps(cuttingLength*2);

        client.setDescription(courgatedClientDTO.getDescription());
        clientRepository.save(client);

        Optional<Clients> clientsopt = clientRepository.findByClientNormalizerAndSize(normalizedClient, courgatedClientDTO.getSize());
        Clients clients = clientsopt.get();

        SuggestedReel reel = new SuggestedReel();
        reel.setClient(clients.getClient());
        reel.setClientNormalizer(clients.getClientNormalizer());
        reel.setProduct(clients.getProduct());
        reel.setProductNormalizer(normalizedProduct);
        reel.setSize(clients.getSize());
        reel.setPly(clients.getPly());
        reel.setDeckle(clients.getDeckle());
        reel.setCuttingLength(clients.getCuttingLength());
        reel.setBottomGsm(clients.getLinerGsm());
        reel.setTopGsm(clients.getTopGsm());
        reel.setLinerGsm(clients.getLinerGsm());
        reel.setFluteGsm(clients.getFluteGsm());
        reel.setMadeUpOf(clients.getMadeUpOf());
        reel.setPaperTypeTop(clients.getPaperTypeTop());
        reel.setPaperTypeBottom(clients.getPaperTypeBottom());
        reel.setOneUps(clients.getOneUps());
        reel.setTwoUps(clients.getTwoUps());
        reel.setThreeUps(clients.getThreeUps());
        reel.setPaperTypeFlute(clients.getPaperTypeFlute());
        reel.setFourUps(clients.getFourUps());
        reel.setFiveUps(clients.getFiveUps());
        reel.setSixUps(clients.getSixUps());
        reel.setDescription(clients.getDescription());
        reel.setProductionCostPerBox(clients.getProductionCostPerBox());
        reel.setSellingPricePerBox(clients.getSellingPricePerBox());
        reel.setCuttingLengthTwoUps(clients
                .getCuttingLength()*2);
        reel.setCuttingLengthOneUps(clients.getCuttingLength());

        reel.setPaperTypeFluteNorm(clients.getPaperTypeFluteNorm());
        reel.setPaperTypeTopNorm(clients.getPaperTypeTopNorm());
        reel.setPaperTypeBottomNorm(clients.getPaperTypeBottomNorm());

        suggestedReelRepository.save(reel);

        return ResponseEntity.ok("Client and Suggested Reel created successfully.");
    }

    @PostMapping("/client/punching/order/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> createPunchingClient(@RequestBody PunchingClientDTO punchingClientDTO) {
        Clients client = new Clients();

        String normalizedProduct = punchingClientDTO.getProduct().toLowerCase().replaceAll("[^a-z0-9]", "");

        // Set base fields from DTO
        client.setClient(punchingClientDTO.getClient());
        client.setProduct(punchingClientDTO.getProduct());
        client.setProductNormalizer(normalizedProduct);
        client.setSize(punchingClientDTO.getSize());
        client.setPly(punchingClientDTO.getPly());
        client.setTopGsm(punchingClientDTO.getTopGsm());
        client.setLinerGsm(punchingClientDTO.getLinerGsm());
        client.setBottomGsm(punchingClientDTO.getLinerGsm());
        client.setFluteGsm(punchingClientDTO.getFluteGsm());
        client.setMadeUpOf(punchingClientDTO.getMadeUpOf());
        client.setPaperTypeTop(punchingClientDTO.getPaperTypeTop());
        client.setPaperTypeBottom(punchingClientDTO.getPaperTypeBottom());
        client.setPaperTypeFlute(punchingClientDTO.getPaperTypeFlute());
        client.setDescription(punchingClientDTO.getDescription());
        client.setProductionCostPerBox(punchingClientDTO.getProductionCostPerBox());
        client.setSellingPricePerBox(punchingClientDTO.getSellingPricePerBox());

        client.setPaperTypeTopNorm(punchingClientDTO.getPaperTypeTop().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setPaperTypeBottomNorm(punchingClientDTO.getPaperTypeBottom().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setPaperTypeFluteNorm(punchingClientDTO.getPaperTypeFlute().toLowerCase().replaceAll("[^a-z0-9]", ""));

        // Normalize client name
        String normalizedClient = punchingClientDTO.getClient().toLowerCase().replaceAll("[^a-z0-9]", "");
        client.setClientNormalizer(normalizedClient);

        // Set product type
        client.setProductType("punching");

        // Parse size string
        String[] dimensions = punchingClientDTO.getSize().split("X");
        int[] arr = new int[dimensions.length];
        for (int i = 0; i < dimensions.length; i++) {
            arr[i] = Integer.parseInt(dimensions[i].trim());
        }

        int l = arr[0];
        int w = arr[1];
        int h = arr.length > 2 ? arr[2] : 0;

        // Ply and Deckle Calculation
        String plyStr = punchingClientDTO.getPly();
        int plyNo = 0;
        if (plyStr != null && plyStr.contains("-")) {
            try {
                plyNo = Integer.parseInt(plyStr.split("-")[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid ply format: " + plyStr);
            }
        }


        double finalDeckle = punchingClientDTO.getDeckle();

        double cuttingLength = punchingClientDTO.getCuttingLength();

        client.setDeckle(finalDeckle);
        client.setCuttingLength(cuttingLength);

        if(finalDeckle > 65) {
            client.setPiece("2 Ups Deckle");
        }else{
            client.setPiece("1 Ups Deckle");
        }

        // Set UPS values
        client.setOneUps(finalDeckle);
        client.setTwoUps(finalDeckle * 2);
        client.setThreeUps(finalDeckle * 3);
        client.setFourUps(finalDeckle * 4);
        client.setFiveUps(finalDeckle * 5);
        client.setSixUps(finalDeckle * 6);

        client.setCuttingLengthOneUps(cuttingLength);
        client.setCuttingLengthTwoUps(cuttingLength * 2);

        // Save to DB
        clientRepository.save(client);

        Optional<Clients> clientsopt = clientRepository.findByClientNormalizerAndSize(normalizedClient, punchingClientDTO.getSize());
        Clients clients = clientsopt.get();

        SuggestedReel reel = new SuggestedReel();
        reel.setClient(clients.getClient());
        reel.setClientNormalizer(clients.getClientNormalizer());
        reel.setProduct(clients.getProduct());
        reel.setProductNormalizer(clients.getProductNormalizer());
        reel.setSize(clients.getSize());
        reel.setPly(clients.getPly());
        reel.setDeckle(clients.getDeckle());
        reel.setCuttingLength(clients.getCuttingLength());
        reel.setBottomGsm(clients.getLinerGsm());
        reel.setTopGsm(clients.getTopGsm());
        reel.setLinerGsm(clients.getLinerGsm());
        reel.setFluteGsm(clients.getFluteGsm());
        reel.setMadeUpOf(clients.getMadeUpOf());
        reel.setPaperTypeTop(clients.getPaperTypeTop());
        reel.setPaperTypeBottom(clients.getPaperTypeBottom());
        reel.setOneUps(clients.getOneUps());
        reel.setTwoUps(clients.getTwoUps());
        reel.setThreeUps(clients.getThreeUps());
        reel.setPaperTypeFlute(clients.getPaperTypeFlute());
        reel.setFourUps(clients.getFourUps());
        reel.setFiveUps(clients.getFiveUps());
        reel.setSixUps(clients.getSixUps());
        reel.setDescription(clients.getDescription());
        reel.setProductionCostPerBox(clients.getProductionCostPerBox());
        reel.setSellingPricePerBox(clients.getSellingPricePerBox());
        reel.setCuttingLengthTwoUps(clients
                .getCuttingLength()*2);
        reel.setCuttingLengthOneUps(clients.getCuttingLength());

        reel.setPaperTypeFluteNorm(clients.getPaperTypeFluteNorm());
        reel.setPaperTypeTopNorm(clients.getPaperTypeTopNorm());
        reel.setPaperTypeBottomNorm(clients.getPaperTypeBottomNorm());

        suggestedReelRepository.save(reel);

        return ResponseEntity.ok("Punching client order saved successfully.");
    }

    @PutMapping("/client/corrugated/order/update/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> editCorrugatedClient(@PathVariable Long id, @RequestBody CourgatedClientDTO dto) {
        Optional<Clients> optionalClient = clientRepository.findById(id);
        if (optionalClient.isEmpty()) return ResponseEntity.notFound().build();

        Clients client = optionalClient.get();

        client.setClient(dto.getClient());
        client.setProduct(dto.getProduct());
        client.setSize(dto.getSize());
        client.setPly(dto.getPly());
        client.setTopGsm(dto.getTopGsm());
        client.setLinerGsm(dto.getLinerGsm());
        client.setBottomGsm(dto.getLinerGsm());
        client.setFluteGsm(dto.getFluteGsm());
        client.setMadeUpOf(dto.getMadeUpOf());
        client.setPaperTypeTop(dto.getPaperTypeTop());
        client.setPaperTypeBottom(dto.getPaperTypeBottom());
        client.setPaperTypeFlute(dto.getPaperTypeFlute());
        client.setDescription(dto.getDescription());
        client.setProductionCostPerBox(dto.getProductionCostPerBox());
        client.setSellingPricePerBox(dto.getSellingPricePerBox());

        client.setPaperTypeTopNorm(dto.getPaperTypeTop().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setPaperTypeBottomNorm(dto.getPaperTypeBottom().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setPaperTypeFluteNorm(dto.getPaperTypeFlute().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setClientNormalizer(dto.getClient().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setProductNormalizer(dto.getProduct().toLowerCase().replaceAll("[^a-z0-9]", ""));

        clientRepository.save(client);
        return ResponseEntity.ok("Corrugated client updated successfully.");
    }


    @PutMapping("/client/punching/order/update/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> editPunchingClient(@PathVariable Long id, @RequestBody PunchingClientDTO dto) {
        Optional<Clients> optionalClient = clientRepository.findById(id);
        if (optionalClient.isEmpty()) return ResponseEntity.notFound().build();

        Clients client = optionalClient.get();

        client.setClient(dto.getClient());
        client.setProduct(dto.getProduct());
        client.setSize(dto.getSize());
        client.setPly(dto.getPly());
        client.setTopGsm(dto.getTopGsm());
        client.setLinerGsm(dto.getLinerGsm());
        client.setBottomGsm(dto.getLinerGsm());
        client.setFluteGsm(dto.getFluteGsm());
        client.setMadeUpOf(dto.getMadeUpOf());
        client.setPaperTypeTop(dto.getPaperTypeTop());
        client.setPaperTypeBottom(dto.getPaperTypeBottom());
        client.setPaperTypeFlute(dto.getPaperTypeFlute());
        client.setDescription(dto.getDescription());
        client.setProductionCostPerBox(dto.getProductionCostPerBox());
        client.setSellingPricePerBox(dto.getSellingPricePerBox());

        client.setPaperTypeTopNorm(dto.getPaperTypeTop().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setPaperTypeBottomNorm(dto.getPaperTypeBottom().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setPaperTypeFluteNorm(dto.getPaperTypeFlute().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setClientNormalizer(dto.getClient().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setProductNormalizer(dto.getProduct().toLowerCase().replaceAll("[^a-z0-9]", ""));

        client.setDeckle(dto.getDeckle());
        client.setCuttingLength(dto.getCuttingLength());

        clientRepository.save(client);
        return ResponseEntity.ok("Punching client updated successfully.");
    }




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
            client.setFiveUps(clients.getDeckle()*5);
            client.setSixUps(clients.getDeckle()*6);
        } else {
            // For corrugated: calculate values
            int l = arr[0];
            int w = arr[1];
            int h = arr.length > 2 ? arr[2] : 0;

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
            client.setFiveUps(finalDeckle*5);
            client.setSixUps(finalDeckle*6);

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


    @PutMapping("/updateClientAndReel/{id}")
    public ResponseEntity<String> updateClientAndReel(
            @PathVariable long id,
            @RequestBody Clients updatedClient,
            @RequestHeader("Authorization") String token) {
        try {
            // Update Clients table
            Clients existingClient = clientRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            String originalNormalizer = existingClient.getClientNormalizer();
            String originalSize = existingClient.getSize();


            // Update all fields except ID
            BeanUtils.copyProperties(updatedClient, existingClient, "id");
            clientRepository.save(existingClient);

            // Update SuggestedReel table
            Optional<SuggestedReel> existingReelOpt =
                    suggestedReelRepository.findByClientNormalizerAndSize(originalNormalizer, originalSize);

            if (existingReelOpt.isPresent()) {
                SuggestedReel existingReel = existingReelOpt.get();

                // Update all relevant fields
                existingReel.setClient(updatedClient.getClient());
                existingReel.setClientNormalizer(updatedClient.getClientNormalizer());
                existingReel.setProduct(updatedClient.getProduct());
                existingReel.setSize(updatedClient.getSize());
                existingReel.setPly(updatedClient.getPly());
                existingReel.setDeckle(updatedClient.getDeckle());
                existingReel.setCuttingLength(updatedClient.getCuttingLength());
                existingReel.setTopGsm(updatedClient.getTopGsm());
                existingReel.setLinerGsm(updatedClient.getLinerGsm());
                existingReel.setBottomGsm(updatedClient.getBottomGsm());
                existingReel.setFluteGsm(updatedClient.getFluteGsm());
                existingReel.setMadeUpOf(updatedClient.getMadeUpOf());
                existingReel.setPaperTypeTop(updatedClient.getPaperTypeTop());
                existingReel.setPaperTypeBottom(updatedClient.getPaperTypeBottom());
                existingReel.setPaperTypeFlute(updatedClient.getPaperTypeFlute());
                existingReel.setOneUps(updatedClient.getOneUps());
                existingReel.setTwoUps(updatedClient.getTwoUps());
                existingReel.setThreeUps(updatedClient.getThreeUps());
                existingReel.setFourUps(updatedClient.getFourUps());
                existingReel.setDescription(updatedClient.getDescription());
                existingReel.setSellingPricePerBox(updatedClient.getSellingPricePerBox());
                existingReel.setProductionCostPerBox(updatedClient.getProductionCostPerBox());

                suggestedReelRepository.save(existingReel);
            }

            return ResponseEntity.ok("Client and suggested reel updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating records: " + e.getMessage());
        }
    }
}
