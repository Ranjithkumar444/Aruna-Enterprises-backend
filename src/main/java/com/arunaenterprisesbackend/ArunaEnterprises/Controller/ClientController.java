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

        // --- MAPPING FIELDS DIRECTLY FROM DTO ---
        client.setClient(courgatedClientDTO.getClient());

        String normalizedClient = courgatedClientDTO.getClient().toLowerCase().replaceAll("[^a-z0-9]", "");
        String normalizedProduct = courgatedClientDTO.getProduct().toLowerCase().replaceAll("[^a-z0-9]", "");
        client.setProductNormalizer(normalizedProduct);
        client.setClientNormalizer(normalizedClient);
        client.setConversionCost(courgatedClientDTO.getConversionCost());
        client.setFluteGsm(courgatedClientDTO.getFluteGsm());
        client.setLinerGsm(courgatedClientDTO.getLinerGsm());
        client.setMadeUpOf(courgatedClientDTO.getMadeUpOf());
        client.setFluteType(courgatedClientDTO.getFluteType());
        client.setPaperTypeBottom(courgatedClientDTO.getPaperTypeBottom());
        client.setPaperTypeTop(courgatedClientDTO.getPaperTypeTop());
        client.setPly(courgatedClientDTO.getPly());
        client.setTopGsm(courgatedClientDTO.getTopGsm());
        client.setProduct(courgatedClientDTO.getProduct());
        client.setSize(courgatedClientDTO.getSize());
        client.setPaperTypeFlute(courgatedClientDTO.getPaperTypeFlute());
        client.setBottomGsm(courgatedClientDTO.getLinerGsm()); // Assumed from original code

        client.setPaperTypeTopNorm(courgatedClientDTO.getPaperTypeTop().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setPaperTypeBottomNorm(courgatedClientDTO.getPaperTypeBottom().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setPaperTypeFluteNorm(courgatedClientDTO.getPaperTypeFlute().toLowerCase().replaceAll("[^a-z0-9]", ""));

        // *** CALCULATED FIELDS NOW MAPPED DIRECTLY FROM DTO INPUT ***
        client.setDeckle(courgatedClientDTO.getDeckle());
        client.setCuttingLength(courgatedClientDTO.getCuttingLength());
        client.setOneUps(courgatedClientDTO.getOneUps());
        client.setTwoUps(courgatedClientDTO.getTwoUps());
        client.setThreeUps(courgatedClientDTO.getThreeUps());
        client.setFourUps(courgatedClientDTO.getFourUps());
        client.setFiveUps(courgatedClientDTO.getFiveUps());
        client.setSixUps(courgatedClientDTO.getSixUps());
        client.setCuttingLengthOneUps(courgatedClientDTO.getCuttingLengthOneUps());
        client.setCuttingLengthTwoUps(courgatedClientDTO.getCuttingLengthTwoUps());
        client.setPiece(courgatedClientDTO.getPiece());
        // *************************************************************

        client.setProductType("corrugated");
        client.setDescription(courgatedClientDTO.getDescription());

        // 1. Save Clients
        clientRepository.save(client);

        // 2. Create SuggestedReel from the saved client
        Clients savedClient = client; // Use the client object that was just saved

        SuggestedReel reel = new SuggestedReel();
        reel.setClient(savedClient.getClient());
        reel.setClientNormalizer(savedClient.getClientNormalizer());
        reel.setProduct(savedClient.getProduct());
        reel.setProductNormalizer(savedClient.getProductNormalizer());
        reel.setSize(savedClient.getSize());
        reel.setPly(savedClient.getPly());
        reel.setDeckle(savedClient.getDeckle());
        reel.setCuttingLength(savedClient.getCuttingLength());
        reel.setBottomGsm(savedClient.getLinerGsm());
        reel.setTopGsm(savedClient.getTopGsm());
        reel.setLinerGsm(savedClient.getLinerGsm());
        reel.setConversionCost(savedClient.getConversionCost());
        reel.setFluteGsm(savedClient.getFluteGsm());
        reel.setMadeUpOf(savedClient.getMadeUpOf());
        reel.setPaperTypeTop(savedClient.getPaperTypeTop());
        reel.setPaperTypeBottom(savedClient.getPaperTypeBottom());
        reel.setOneUps(savedClient.getOneUps());
        reel.setTwoUps(savedClient.getTwoUps());
        reel.setFluteType(savedClient.getFluteType());
        reel.setThreeUps(savedClient.getThreeUps());
        reel.setPaperTypeFlute(savedClient.getPaperTypeFlute());
        reel.setFourUps(savedClient.getFourUps());
        reel.setFiveUps(savedClient.getFiveUps());
        reel.setSixUps(savedClient.getSixUps());
        reel.setDescription(savedClient.getDescription());

        reel.setCuttingLengthTwoUps(savedClient.getCuttingLengthTwoUps());
        reel.setCuttingLengthOneUps(savedClient.getCuttingLengthOneUps());
        reel.setPaperTypeFluteNorm(savedClient.getPaperTypeFluteNorm());
        reel.setPaperTypeTopNorm(savedClient.getPaperTypeTopNorm());
        reel.setPaperTypeBottomNorm(savedClient.getPaperTypeBottomNorm());

        // 3. Save SuggestedReel
        suggestedReelRepository.save(reel);

        return ResponseEntity.ok("Client and Suggested Reel created successfully.");
    }

    // --- PUNCHING CREATE (NO CALCULATIONS) ---
    @PostMapping("/client/punching/order/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> createPunchingClient(@RequestBody PunchingClientDTO punchingClientDTO) {
        Clients client = new Clients();

        // --- MAPPING FIELDS DIRECTLY FROM DTO ---
        String normalizedProduct = punchingClientDTO.getProduct().toLowerCase().replaceAll("[^a-z0-9]", "");
        client.setProductNormalizer(normalizedProduct);
        client.setClient(punchingClientDTO.getClient());
        client.setProduct(punchingClientDTO.getProduct());
        client.setSize(punchingClientDTO.getSize());
        client.setPly(punchingClientDTO.getPly());
        client.setConversionCost(punchingClientDTO.getConversionCost());
        client.setTopGsm(punchingClientDTO.getTopGsm());
        client.setLinerGsm(punchingClientDTO.getLinerGsm());
        client.setBottomGsm(punchingClientDTO.getLinerGsm()); // Assumed from original code
        client.setFluteGsm(punchingClientDTO.getFluteGsm());
        client.setFluteType(punchingClientDTO.getFluteType());
        client.setMadeUpOf(punchingClientDTO.getMadeUpOf());
        client.setPaperTypeTop(punchingClientDTO.getPaperTypeTop());
        client.setPaperTypeBottom(punchingClientDTO.getPaperTypeBottom());
        client.setPaperTypeFlute(punchingClientDTO.getPaperTypeFlute());
        client.setDescription(punchingClientDTO.getDescription());


        client.setPaperTypeTopNorm(punchingClientDTO.getPaperTypeTop().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setPaperTypeBottomNorm(punchingClientDTO.getPaperTypeBottom().toLowerCase().replaceAll("[^a-z0-9]", ""));
        client.setPaperTypeFluteNorm(punchingClientDTO.getPaperTypeFlute().toLowerCase().replaceAll("[^a-z0-9]", ""));

        // Normalization
        String normalizedClient = punchingClientDTO.getClient().toLowerCase().replaceAll("[^a-z0-9]", "");
        client.setClientNormalizer(normalizedClient);

        // Product type
        client.setProductType("punching");

        // *** CALCULATED FIELDS NOW MAPPED DIRECTLY FROM DTO INPUT ***
        // Note: The original punching endpoint did simple multiplications (x2, x3, etc.)
        // for ups based on the input deckle. I'm keeping those simple derivations
        // using the input Deckle/CuttingLength values for consistency, but if these
        // need to be supplied by the user, you must add them to PunchingClientDTO.

        double finalDeckle = punchingClientDTO.getDeckle();
        double cuttingLength = punchingClientDTO.getCuttingLength();

        client.setDeckle(finalDeckle);
        client.setCuttingLength(cuttingLength);

        if(finalDeckle > 65) { // Piece logic kept for consistency with original code
            client.setPiece("1 Ups Deckle");
        }else{
            client.setPiece("2 Ups Deckle");
        }

        client.setOneUps(finalDeckle);
        client.setTwoUps(finalDeckle * 2);
        client.setThreeUps(finalDeckle * 3);
        client.setFourUps(finalDeckle * 4);
        client.setFiveUps(finalDeckle * 5);
        client.setSixUps(finalDeckle * 6);

        client.setCuttingLengthOneUps(cuttingLength);
        client.setCuttingLengthTwoUps(cuttingLength * 2);
        // *************************************************************

        // 1. Save to DB
        clientRepository.save(client);

        // 2. Map to SuggestedReel
        Clients savedClient = client;

        SuggestedReel reel = new SuggestedReel();
        reel.setClient(savedClient.getClient());
        reel.setClientNormalizer(savedClient.getClientNormalizer());
        reel.setProductNormalizer(savedClient.getProductNormalizer());
        reel.setProduct(savedClient.getProduct());
        reel.setSize(savedClient.getSize());
        reel.setPly(savedClient.getPly());
        reel.setDeckle(savedClient.getDeckle());
        reel.setConversionCost(savedClient.getConversionCost());
        reel.setCuttingLength(savedClient.getCuttingLength());
        reel.setBottomGsm(savedClient.getLinerGsm());
        reel.setTopGsm(savedClient.getTopGsm());
        reel.setLinerGsm(savedClient.getLinerGsm());
        reel.setFluteGsm(savedClient.getFluteGsm());
        reel.setMadeUpOf(savedClient.getMadeUpOf());
        reel.setPaperTypeTop(savedClient.getPaperTypeTop());
        reel.setPaperTypeBottom(savedClient.getPaperTypeBottom());
        reel.setOneUps(savedClient.getOneUps());
        reel.setTwoUps(savedClient.getTwoUps());
        reel.setThreeUps(savedClient.getThreeUps());
        reel.setFluteType(savedClient.getFluteType());
        reel.setPaperTypeFlute(savedClient.getPaperTypeFlute());
        reel.setFourUps(savedClient.getFourUps());
        reel.setFiveUps(savedClient.getFiveUps());
        reel.setSixUps(savedClient.getSixUps());
        reel.setDescription(savedClient.getDescription());
        reel.setCuttingLengthTwoUps(savedClient.getCuttingLengthTwoUps());
        reel.setCuttingLengthOneUps(savedClient.getCuttingLengthOneUps());
        reel.setPaperTypeFluteNorm(savedClient.getPaperTypeFluteNorm());
        reel.setPaperTypeTopNorm(savedClient.getPaperTypeTopNorm());
        reel.setPaperTypeBottomNorm(savedClient.getPaperTypeBottomNorm());

        // 3. Save SuggestedReel
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

                suggestedReelRepository.save(existingReel);
            }

            return ResponseEntity.ok("Client and suggested reel updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating records: " + e.getMessage());
        }
    }
}
