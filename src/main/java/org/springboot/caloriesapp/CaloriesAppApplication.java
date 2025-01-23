package org.springboot.caloriesapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CaloriesAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaloriesAppApplication.class, args);
	}
}
