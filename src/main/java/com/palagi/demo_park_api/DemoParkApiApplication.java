package com.palagi.demo_park_api;

import com.palagi.demo_park_api.config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class  DemoParkApiApplication {

	public static void main(String[] args) {

		System.setProperty("DB_URL", EnvConfig.getString("DB_URL"));
		System.setProperty("DB_USERNAME", EnvConfig.getString("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", EnvConfig.getString("DB_PASSWORD"));

		SpringApplication.run(DemoParkApiApplication.class, args);
	}

}
