package com.smartleavemanagement.service;

import org.springframework.http.ResponseEntity;
import com.smartleavemanagement.model.Admins;

public interface AdminService {
	
	ResponseEntity<String> registerAdmin(Admins admins);
	ResponseEntity<?> login(String username, String password);
	ResponseEntity<String> addNewRole(String newRole,String description);

}
