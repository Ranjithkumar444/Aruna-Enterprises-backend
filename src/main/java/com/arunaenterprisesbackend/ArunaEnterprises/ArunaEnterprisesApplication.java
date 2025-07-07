package com.arunaenterprisesbackend.ArunaEnterprises;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@EnableScheduling
@SpringBootApplication
public class ArunaEnterprisesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArunaEnterprisesApplication.class, args);
	}

	@Bean
	public ScheduledExecutorService scheduledExecutorService() {
		return Executors.newSingleThreadScheduledExecutor();
	}
}
