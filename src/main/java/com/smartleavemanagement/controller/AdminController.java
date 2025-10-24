package com.smartleavemanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartleavemanagement.DTOs.LoginDetails;
import com.smartleavemanagement.model.Admins;
import com.smartleavemanagement.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	
	private final AdminService adminService;
	
	public AdminController(AdminService adminService)
	{
		this.adminService=adminService;
	}
	

	 
	 @PostMapping("/registration")
	    public ResponseEntity<String> registerAdmin(@RequestBody Admins admins) {
	        return adminService.registerAdmin(admins);
	    }
	 
	 @PostMapping("/login")
	 public ResponseEntity<?> login(@RequestBody LoginDetails loginDetails) {
	     return adminService.login(loginDetails.getUserName(), loginDetails.getPassword());
	 }

	 
	

}
