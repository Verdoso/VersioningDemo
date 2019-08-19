package org.greeneyed.versioning.demo;

import org.greeneyed.summer.config.enablers.EnableSummer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSummer(log4j = false)
public class VersioningDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(VersioningDemoApplication.class, args);
	}

}
