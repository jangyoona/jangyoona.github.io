package com.groupware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.FormatterRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Date;

@SpringBootApplication
@EnableScheduling
public class GroupwareApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(GroupwareApplication.class, args);
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addFormatterForFieldType(Date.class, new org.springframework.format.datetime.DateFormatter("yyyy-MM-dd"));
	}

}
