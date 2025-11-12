package com.smartleavemanagement.service;

import com.smartleavemanagement.DTOs.HolidayCalendar;
import com.smartleavemanagement.DTOs.UserLeaveBalancedays;
import com.smartleavemanagement.model.Users;

import java.util.List;
import org.springframework.http.ResponseEntity;

public interface UsersService {

    ResponseEntity<String> registerUser(Users user);

    ResponseEntity<?> login(String username, String password);

    ResponseEntity<String> updateUserDetails(int userId, Users updatedUser, String token);

    ResponseEntity<String> generateOtp(String email, String context);

    ResponseEntity<String> verifyOtp(int otp, String context);

    ResponseEntity<String> updatePassword(int userId, String oldPassword, String newPassword, String token);

    ResponseEntity<List<HolidayCalendar>> getHolidays(int userId);

    ResponseEntity<List<UserLeaveBalancedays>> getUserLeaveBalance(int userId);
    
    ResponseEntity<String> deleteAccount(int userId, String token);

    ResponseEntity<String> updateNewPassword(int userId, String newPassword);
    
    ResponseEntity<List<String>> getAllCountriesForUsers();
    
    ResponseEntity<List<String>> getAllCities(String countryName);
}
