package com.cloud.initialserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableAutoConfiguration
@EnableJpaRepositories("com.cloud.initialserver")
public class InitialserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(InitialserverApplication.class, args);
	}
}
