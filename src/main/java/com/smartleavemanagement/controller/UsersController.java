package com.smartleavemanagement.controller;


import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartleavemanagement.DTOs.LoginDetails;
import com.smartleavemanagement.model.Users;
import com.smartleavemanagement.service.UsersService;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/registration")
    public ResponseEntity<String> registerUser(@RequestBody Users user) {
        return usersService.registerUser(user);
    }


    @PostMapping("/login")
	 public ResponseEntity<?> login(@RequestBody LoginDetails loginDetails) {
	     return usersService.login(loginDetails.getUserName(), loginDetails.getPassword());
	 }

    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateUserDetails(
            @PathVariable int userId,
            @RequestBody Users updatedUser,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return usersService.updateUserDetails(userId, updatedUser, token);
    }
    @PostMapping("/forgot-password/generate-otp")
    public ResponseEntity<String> generateOtpForPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        return usersService.generateOtp(email, "password");
    }

    @PostMapping("/forgot-username/generate-otp")
    public ResponseEntity<String> generateOtpForUsername(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        return usersService.generateOtp(email, "username");
    }
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<String> verifyOtpForPassword(@RequestBody Map<String, String> payload) {
        int otp = Integer.parseInt(payload.get("otp"));
        return usersService.verifyOtp(otp, "password");
    }

    @PostMapping("/forgot-username/verify-otp")
    public ResponseEntity<?> verifyOtpForUsername(@RequestBody Map<String, String> payload) {
        int otp = Integer.parseInt(payload.get("otp"));
        return usersService.verifyOtp(otp, "username");
    }

    @PutMapping("/update-password/{userId}")
    public ResponseEntity<String> updatePassword(
            @PathVariable int userId,
            @RequestBody Map<String, String> payload,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        String oldPassword = payload.get("oldPassword");
        String newPassword = payload.get("newPassword");

        return usersService.updatePassword(userId, oldPassword, newPassword, token);
    }


}
