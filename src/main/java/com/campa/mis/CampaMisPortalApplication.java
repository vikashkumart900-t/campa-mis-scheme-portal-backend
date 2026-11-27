package com.campa.mis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CampaMisPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampaMisPortalApplication.class, args);
	}

}
