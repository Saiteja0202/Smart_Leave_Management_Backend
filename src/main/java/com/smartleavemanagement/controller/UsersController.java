package com.smartleavemanagement.controller;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartleavemanagement.DTOs.HolidayCalendar;
import com.smartleavemanagement.DTOs.LoginDetails;
import com.smartleavemanagement.DTOs.UserLeaveBalancedays;
import com.smartleavemanagement.model.Users;
import com.smartleavemanagement.model.UsersLeaveBalance;
import com.smartleavemanagement.repository.UsersRepository;
import com.smartleavemanagement.service.UsersService;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;
    
    private final UsersRepository usersRepository;

    
    public UsersController(UsersService usersService,UsersRepository usersRepository) {
        this.usersService = usersService;
        this.usersRepository=usersRepository;
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
    
    
    @GetMapping("/get-user-details/{userId}")
    public Optional<Users> getUserDetails(@PathVariable int userId)
    {
    	return usersRepository.findById(userId);
    }

    @GetMapping("/get-holidays/{userId}")
    public ResponseEntity<List<HolidayCalendar>> getHolidays(@PathVariable int userId)
    {
    	return usersService.getHolidays(userId);
    }
    
    @GetMapping("/get-leave-balance/{userId}")
    public ResponseEntity<List<UserLeaveBalancedays>> getUserLeaveBalance(@PathVariable int userId)
    {
    	return usersService.getUserLeaveBalance(userId);
    }
    
}
