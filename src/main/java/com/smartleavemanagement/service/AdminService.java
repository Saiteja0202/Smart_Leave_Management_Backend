package com.smartleavemanagement.service;

import org.springframework.http.ResponseEntity;

import com.smartleavemanagement.model.Users;

public interface AdminService {
	
	ResponseEntity<String> registerAdmin(Users user);
	ResponseEntity<?> login(String username, String password);

}
