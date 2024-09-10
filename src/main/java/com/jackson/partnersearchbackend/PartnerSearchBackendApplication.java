package com.jackson.partnersearchbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jackson.partnersearchbackend.mapper")
public class PartnerSearchBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PartnerSearchBackendApplication.class, args);
	}

}
