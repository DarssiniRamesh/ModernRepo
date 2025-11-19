package com.example.postgresdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableJpaAuditing
public class PostgresDemoApplication {

	private static final Logger logger = LoggerFactory.getLogger(PostgresDemoApplication.class);

	@Value("${spring.datasource.url}")
	private String datasourceUrl;

	@PostConstruct
	public void logDatasourceUrl() {
		logger.info(">>> [ModernRepo] Resolved JDBC Datasource URL: '{}'", datasourceUrl);
	}

	public static void main(String[] args) {
		SpringApplication.run(PostgresDemoApplication.class, args);
	}
}
