package com.smartleavemanagement.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import com.smartleavemanagement.DTOs.LoginDetails;
import com.smartleavemanagement.model.Admins;
import com.smartleavemanagement.model.CountryCalendars;
import com.smartleavemanagement.model.RoleBasedLeaves;
import com.smartleavemanagement.model.Roles;
import com.smartleavemanagement.model.Users;
import com.smartleavemanagement.repository.AdminsRepository;
import com.smartleavemanagement.repository.UsersRepository;
import com.smartleavemanagement.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	
	private final AdminService adminService;
	
	private final UsersRepository usersRepository;
	
	private final AdminsRepository adminsRepository;
	
	public AdminController(AdminService adminService, UsersRepository usersRepository,AdminsRepository adminsRepository)
	{
		this.adminService=adminService;
		this.usersRepository=usersRepository;
		this.adminsRepository=adminsRepository;
	}
	

	 
	 @PostMapping("/registration")
	    public ResponseEntity<String> registerAdmin(@RequestBody Admins admins) {
	        return adminService.registerAdmin(admins);
	    }
	 
	 @PostMapping("/login")
	 public ResponseEntity<?> login(@RequestBody LoginDetails loginDetails) {
	     return adminService.login(loginDetails.getUserName(), loginDetails.getPassword());
	 }

	 
	 @PostMapping("/add-newrole/{userId}")
	 public ResponseEntity<String> addNewRole(@PathVariable int userId,@RequestBody Roles roles,
			 @RequestHeader("Authorization") String authHeader)
	 {
	     String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
		 return adminService.addNewRole(userId,roles.getRoleName(),roles.getDescription(), token);
	 }
	 
	 @PostMapping("/add-new-country-calenadr/{userId}")
	 public ResponseEntity<String> addNewCountryCalendar(@PathVariable int userId,@RequestBody CountryCalendars countryCalendars,
			@RequestHeader("Authorization") String authHeader)
	 {
		 
		 String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
		 return adminService.addNewCountryCalendar(userId,countryCalendars.getCountryName(),countryCalendars.getCalendarYear(),
				 countryCalendars.getHolidayName(),countryCalendars.getHolidayDate(),token);
	 }
	 
	 
	 @GetMapping("/get-all-users")
	 public ResponseEntity<List<Users>> getAllUsers()
	 {
		 List<Users> requests = usersRepository.findAll(); 
			return ResponseEntity.ok(requests);
	 }
	 
	 
	 @GetMapping("/get-admin-details/{adminId}")
	 public Optional<Admins> getAdminDetails(@PathVariable int adminId) {
	     return adminsRepository.findById(adminId);
	 }
	 
	 
	 @PostMapping("/add-new-leave-policies/{userId}")
	 public ResponseEntity<String> addNewLeavePolicies(@PathVariable int userId,@RequestBody RoleBasedLeaves roleBasedLeaves,
			 @RequestHeader("Authorization") String authHeader)
	 {
		 String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
		 return adminService.addNewLeavePolicies(userId, roleBasedLeaves, token);
	 }

	 
	 @PutMapping("/promote/{userId}/{roleName}")
	 public ResponseEntity<String> promotionToUser(@PathVariable int userId, @PathVariable String roleName)
	 {
		 return adminService.promotionToUser(userId, roleName);
	 }
	

}
