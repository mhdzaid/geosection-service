package com.natlex.geosectionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class NatlexGeoSectionManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NatlexGeoSectionManagementServiceApplication.class, args);
	}

}
