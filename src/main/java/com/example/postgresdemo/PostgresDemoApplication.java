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
		// Try to log explicit SSL property value from environment to assist with debugging
		String sslmode = System.getenv("SPRING_DATASOURCE_SSLMODE");
		if (sslmode == null) sslmode = "(not set)";
		logger.info(">>> [ModernRepo] SPRING_DATASOURCE_SSLMODE environment variable: '{}'", sslmode);
		// Defensive: HikariCP's internal property for SSL, if set
		String hikariSsl = System.getenv("SPRING_DATASOURCE_HIKARI_DATA_SOURCE_PROPERTIES_SSL");
		if (hikariSsl == null) hikariSsl = "(not set)";
		logger.info(">>> [ModernRepo] HikariCP spring.datasource.hikari.data-source-properties.ssl: '{}'", hikariSsl);
	}

	public static void main(String[] args) {
		SpringApplication.run(PostgresDemoApplication.class, args);
	}
}
