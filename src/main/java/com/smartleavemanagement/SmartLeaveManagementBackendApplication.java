package com.smartleavemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;



@SpringBootApplication
@EnableScheduling
public class SmartLeaveManagementBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartLeaveManagementBackendApplication.class, args);
	}

}
