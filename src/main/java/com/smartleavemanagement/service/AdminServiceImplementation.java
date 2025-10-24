package com.smartleavemanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smartleavemanagement.DTOs.LoginResponse;
import com.smartleavemanagement.enums.OtpStatus;
import com.smartleavemanagement.model.Roles;
import com.smartleavemanagement.model.Users;
import com.smartleavemanagement.repository.RolesRepository;
import com.smartleavemanagement.repository.UsersRepository;
import com.smartleavemanagement.securityconfiguration.JwtUtil;




@Service
public class AdminServiceImplementation implements AdminService{

	private final UsersRepository usersRepository;
	
	private final RolesRepository rolesRepository;

	
	private final JwtUtil jwtUtil;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public AdminServiceImplementation(UsersRepository usersRepository,JwtUtil jwtUtil,RolesRepository rolesRepository)
	{
		this.usersRepository=usersRepository;
		this.jwtUtil=jwtUtil;
		this.rolesRepository=rolesRepository;
	}
	
	@Override
	public ResponseEntity<String> registerAdmin(Users user) {
	    Roles adminRole = rolesRepository.findByRoleNameIgnoreCase("ADMIN").orElse(null);
	    if (adminRole == null) {
	        return ResponseEntity.status(500).body("ADMIN role not found in database");
	    }

	    if (usersRepository.existsByUserName(user.getUserName())) {
	        return ResponseEntity.badRequest().body("Username already exists");
	    }

	    if (usersRepository.existsByEmail(user.getEmail())) {
	        return ResponseEntity.badRequest().body("Email already exists");
	    }

	    user.setPassword(passwordEncoder.encode(user.getPassword()));
	    user.setOtpStatus(OtpStatus.GENERATE);
	    user.setRole(adminRole);
	    usersRepository.save(user);
	    return ResponseEntity.ok("Admin registered successfully");
	}


	@Override
	public ResponseEntity<?> login(String username, String password) {
	    Users user = usersRepository.findByUserName(username).orElse(null);
	    if (user == null) {
	        return ResponseEntity.status(404).body("User not found");
	    }

	    if (!passwordEncoder.matches(password, user.getPassword())) {
	        return ResponseEntity.status(401).body("Invalid credentials");
	    }


	    String token = jwtUtil.generateToken(
	        user.getUserName(),
	        (long) user.getUserId(),
	        user.getRole().getRoleName()
	    );

	    LoginResponse response = new LoginResponse(
	        user.getUserId(),
	        user.getRole().getRoleName(),
	        user.getEmail(),
	        token
	    );

	    return ResponseEntity.ok(response);
	
	}


}
