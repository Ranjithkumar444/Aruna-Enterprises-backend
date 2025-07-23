package com.arunaenterprisesbackend.ArunaEnterprises.Controller;


import com.arunaenterprisesbackend.ArunaEnterprises.DTO.MachineConfigDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Machine;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.MachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class MachineController {

    @Autowired
    private MachineRepository machineRepository;

    @PostMapping("/machine/config")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> configMachine(@RequestBody MachineConfigDTO machineConfigDTO){
        try{
            Machine machine = new Machine();
            machine.setMachineName(machineConfigDTO.getMachineName());
            machine.setMachineCode(machineConfigDTO.getMachineCode());
            machine.setUnit(machineConfigDTO.getUnit());
            machine.setMaxDeckle(machine.getMaxDeckle());
            machine.setMinDeckle(machine.getMinDeckle());
            machine.setMaxCuttingLength(machine.getMaxCuttingLength());
            machine.setMinCuttingLength(machineConfigDTO.getMinCuttingLength());
            machine.setNoOfBoxPerHour(machine.getNoOfBoxPerHour());
            machine.setNoOfSheetsPerHour(machineConfigDTO.getNoOfSheetsPerHour());

            machineRepository.save(machine);
            return  ResponseEntity.ok("Machine Configured Successfully");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
