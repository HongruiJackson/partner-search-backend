package com.jackson.partnersearchbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.jackson.partnersearchbackend.mapper")
@EnableScheduling
@ServletComponentScan
public class PartnerSearchBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PartnerSearchBackendApplication.class, args);
	}

}
