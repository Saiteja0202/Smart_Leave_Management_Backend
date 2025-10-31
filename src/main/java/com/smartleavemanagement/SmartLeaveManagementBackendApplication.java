package com.smartleavemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.client.HttpClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;



@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.webservices.client.WebServiceTemplateAutoConfiguration.class,
        HttpClientAutoConfiguration.class,
        RestClientAutoConfiguration.class
})
@EnableScheduling
public class SmartLeaveManagementBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartLeaveManagementBackendApplication.class, args);
	}

}
