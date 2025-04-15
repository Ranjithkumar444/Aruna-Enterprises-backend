package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {

    @GetMapping("/greet")
    public String HelloController(){
        return "HEllo World";
    }
}
