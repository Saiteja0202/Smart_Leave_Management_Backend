package com.smartleavemanagement.service;


import com.smartleavemanagement.model.Users;
import org.springframework.http.ResponseEntity;

public interface UsersService {
	
    ResponseEntity<String> registerUser(Users user);
    ResponseEntity<?> login(String username, String password);
    ResponseEntity<String> updateUserDetails(int userId, Users updatedUser, String token);
    ResponseEntity<String> generateOtp(String email, String context);
    ResponseEntity<String> verifyOtp(int otp, String context);
    ResponseEntity<String> updatePassword(int userId, String oldPassword, String newPassword, String token);

}
