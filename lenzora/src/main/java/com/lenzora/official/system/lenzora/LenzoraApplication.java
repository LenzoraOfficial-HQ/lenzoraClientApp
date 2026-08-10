package com.lenzora.official.system.lenzora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})

public class LenzoraApplication {

	public static void main(String[] args) {
		SpringApplication.run(LenzoraApplication.class, args);
	}

}
