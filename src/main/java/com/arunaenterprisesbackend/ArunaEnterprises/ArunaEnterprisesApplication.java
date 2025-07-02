package com.arunaenterprisesbackend.ArunaEnterprises;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ArunaEnterprisesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArunaEnterprisesApplication.class, args);
	}

}
