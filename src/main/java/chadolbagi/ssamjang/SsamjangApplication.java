package chadolbagi.ssamjang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "chadolbagi\\.ssamjang.*\\.Test.*"))
public class SsamjangApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsamjangApplication.class, args);
	}

}
