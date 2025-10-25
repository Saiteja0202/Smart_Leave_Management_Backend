package com.smartleavemanagement.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smartleavemanagement.DTOs.LoginResponse;
import com.smartleavemanagement.model.Admins;
import com.smartleavemanagement.model.RegistrationHistory;
import com.smartleavemanagement.model.Roles;
import com.smartleavemanagement.repository.AdminsRepository;
import com.smartleavemanagement.repository.RegistrationHistoryRepository;
import com.smartleavemanagement.repository.RolesRepository;
import com.smartleavemanagement.securityconfiguration.JwtUtil;


@Service
public class AdminServiceImplementation implements AdminService{

    private final RolesRepository rolesRepository;

	private final AdminsRepository adminsRepository;
	


	
	private final JwtUtil jwtUtil;
	
	private final RegistrationHistoryRepository registrationHistoryRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public AdminServiceImplementation(AdminsRepository adminsRepository,JwtUtil jwtUtil,
			RegistrationHistoryRepository registrationHistoryRepository, RolesRepository rolesRepository)
	{
		this.adminsRepository=adminsRepository;
		this.jwtUtil=jwtUtil;
		
		this.registrationHistoryRepository=registrationHistoryRepository;
		
		this.rolesRepository = rolesRepository;
	}
	
	@Override
	public ResponseEntity<String> registerAdmin(Admins admins) {
	   

	    if (adminsRepository.existsByUserName(admins.getUserName())) {
	        return ResponseEntity.badRequest().body("Username already exists");
	    }

	    if (adminsRepository.existsByEmail(admins.getEmail())) {
	        return ResponseEntity.badRequest().body("Email already exists");
	    }

	    admins.setPassword(passwordEncoder.encode(admins.getPassword()));
	    admins.setRole("ADMIN");
	    adminsRepository.save(admins);
	    
	    
	    RegistrationHistory registrationHistory = new RegistrationHistory();
		registrationHistory.setFirstName(admins.getFirstName());
		registrationHistory.setLastName(admins.getLastName());
		registrationHistory.setEmail(admins.getEmail());
		registrationHistory.setUserId(admins.getAdminId());
		registrationHistory.setRegisterDate(LocalDateTime.now());
		registrationHistory.setRole(admins.getRole());
		registrationHistoryRepository.save(registrationHistory);
		
		
		Roles newRole = new Roles();
		newRole.setRoleName("ADMIN");
		newRole.setDescription("All-access system administrator with full control");
		rolesRepository.save(newRole);
		
		
	    return ResponseEntity.ok("Admin registered successfully");
	}


	@Override
	public ResponseEntity<?> login(String username, String password) {
	    Admins admin = adminsRepository.findByUserName(username).orElse(null);
	    if (admin == null) {
	        return ResponseEntity.status(404).body("User not found");
	    }

	    if (!passwordEncoder.matches(password, admin.getPassword())) {
	        return ResponseEntity.status(401).body("Invalid credentials");
	    }


	    String token = jwtUtil.generateToken(
	        admin.getUserName(),
	        (long) admin.getAdminId(),
	        admin.getRole()
	    );

	    LoginResponse response = new LoginResponse(
	        admin.getAdminId(),
	        admin.getRole(),
	        admin.getEmail(),
	        token
	    );

	    return ResponseEntity.ok(response);
	
	}
	
	
	@Override
	public ResponseEntity<String> addNewRole(String newRole,String description)
	{
		
		Roles newRoles = new Roles();
		
		newRoles.setRoleName(newRole);
		newRoles.setDescription(description);
		rolesRepository.save(newRoles);
		
		return ResponseEntity.ok("New Role Added Successfully");
	}


}
