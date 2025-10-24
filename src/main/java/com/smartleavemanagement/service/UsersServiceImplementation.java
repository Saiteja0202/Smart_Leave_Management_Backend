package com.smartleavemanagement.service;


import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
public class UsersServiceImplementation implements UsersService {

	private final UsersRepository usersRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	private final RolesRepository rolesRepository;

	private final JavaMailSender mailSender;

    public UsersServiceImplementation(
        UsersRepository usersRepository,
        PasswordEncoder passwordEncoder,
        JwtUtil jwtUtil,
        RolesRepository rolesRepository,
        JavaMailSender mailSender
    ) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.rolesRepository = rolesRepository;
        this.mailSender = mailSender;
    }

	@Override
	public ResponseEntity<String> registerUser(Users user) {
		if (user.getRole() != null && user.getRole().getRoleName() != null
				&& user.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {

			return ResponseEntity.badRequest().body("Admin must register via /admin/registration");
		}

		if (usersRepository.existsByUserName(user.getUserName())) {
			return ResponseEntity.badRequest().body("Username already exists");
		}

		if (usersRepository.existsByEmail(user.getEmail())) {
			return ResponseEntity.badRequest().body("Email already exists");
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setOtp(0);
		user.setOtpStatus(OtpStatus.GENERATE);

		if (user.getRole() == null) {
			Roles defaultRole = rolesRepository.findById(1).orElse(null);
			if (defaultRole == null) {
				return ResponseEntity.status(500).body("Default role not found");
			}
			user.setRole(defaultRole);
		}

		usersRepository.save(user);
		return ResponseEntity.ok("User registered successfully");
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

		String token = jwtUtil.generateToken(user.getUserName(), (long) user.getUserId(), user.getRole().getRoleName());

		LoginResponse response = new LoginResponse(user.getUserId(), user.getRole().getRoleName(), user.getEmail(),
				token);

		return ResponseEntity.ok(response);

	}

	@Override
	public ResponseEntity<String> updateUserDetails(int userId, Users updatedUser, String token) {
		if (!jwtUtil.validateToken(token)) {
			return ResponseEntity.status(401).body("Invalid or expired token");
		}

		String usernameFromToken = jwtUtil.extractUsername(token);
		Users existingUser = usersRepository.findById(userId).orElse(null);

		if (existingUser == null) {
			return ResponseEntity.status(404).body("User not found");
		}

		if (!existingUser.getUserName().equals(usernameFromToken)) {
			return ResponseEntity.status(403).body("You are not authorized to update this user's details");
		}

		existingUser.setFullName(updatedUser.getFullName());
		existingUser.setEmail(updatedUser.getEmail());
		existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
		existingUser.setAddress(updatedUser.getAddress());
		existingUser.setGender(updatedUser.getGender());

		usersRepository.save(existingUser);
		return ResponseEntity.ok("User details updated successfully");
	}
	
	@Override
	public ResponseEntity<String> generateOtp(String email, String context) {
	    Optional<Users> optionalUser = usersRepository.findByEmail(email);
	    if (optionalUser.isEmpty()) {
	        return ResponseEntity.status(404).body("User not found");
	    }

	    Users user = optionalUser.get();
	    int otp = (int)(Math.random() * 9000) + 1000;

	    user.setOtp(otp);
	    user.setOtpStatus(OtpStatus.PENDING);
	    usersRepository.save(user);

	    SimpleMailMessage message = new SimpleMailMessage();
	    message.setTo(user.getEmail());
	    message.setSubject("Your OTP for " + context + " recovery");
	    message.setText("Dear " + user.getFullName() + ",\n\nYour OTP is: " + otp + "\n\nRegards,\nSmart Leave Management Team");

	    mailSender.send(message);

	    return ResponseEntity.ok("OTP generated and sent to email");
	}
	@Override
	public ResponseEntity<String> verifyOtp(int otp, String context) {
	    Users user = usersRepository.findByOtp(otp).orElse(null);
	    if (user == null) {
	        return ResponseEntity.badRequest().body("Wrong OTP");
	    }

	    if (user.getOtpStatus() != OtpStatus.PENDING) {
	        user.setOtpStatus(OtpStatus.EXPIRED);
	        usersRepository.save(user);
	        return ResponseEntity.badRequest().body("OTP expired or already used");
	    }

	    user.setOtpStatus(OtpStatus.VERIFIED);
	    usersRepository.save(user);

	    if (context.equalsIgnoreCase("username")) {
	        return ResponseEntity.ok("Username: " + user.getUserName());
	    } else {
	        return ResponseEntity.ok("Email verified successfully");
	    }
	}

	@Override
	public ResponseEntity<String> updatePassword(int userId, String oldPassword, String newPassword, String token) {
	    if (!jwtUtil.validateToken(token)) {
	        return ResponseEntity.status(401).body("Invalid or expired token");
	    }

	    Long tokenUserId = jwtUtil.extractUserId(token);
	    if (tokenUserId == null || tokenUserId != userId) {
	        return ResponseEntity.status(403).body("You are not authorized to update this user's password");
	    }

	    Users user = usersRepository.findById(userId).orElse(null);
	    if (user == null) {
	        return ResponseEntity.status(404).body("User not found");
	    }

	    if (user.getOtpStatus() != OtpStatus.VERIFIED) {
	        return ResponseEntity.badRequest().body("OTP verification required before updating password");
	    }

	    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
	        return ResponseEntity.badRequest().body("Old password does not match");
	    }

	    user.setPassword(passwordEncoder.encode(newPassword));
	    user.setOtpStatus(OtpStatus.GENERATE);
	    usersRepository.save(user);

	    return ResponseEntity.ok("Password updated successfully");
	}


}
