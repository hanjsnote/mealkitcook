package com.mealkitcook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class MealkitcookApplication {

	public static void main(String[] args) {
		SpringApplication.run(MealkitcookApplication.class, args);
	}

}
