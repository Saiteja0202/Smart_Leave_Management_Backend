package com.smartleavemanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.smartleavemanagement.model.*;

public interface AdminService {

    ResponseEntity<String> registerAdmin(Admins admins);
    ResponseEntity<?> login(String username, String password);

    ResponseEntity<String> addNewRole(int userId, String newRole, String description, String token);
    ResponseEntity<String> addNewCountryCalendar(int userId, String countryName, int calendarYear, String holidayName, LocalDate holidayDate,String cityName, String token);
    ResponseEntity<String> addNewLeavePolicies(int userId, RoleBasedLeaves roleBasedLeaves, String token);
    ResponseEntity<String> promotionToUser(int adminId, int userId, String roleName, String token);
    ResponseEntity<?> getAllLeaveRequests(int adminId);
    ResponseEntity<String> approveLeaveRequestByAdmin(int adminId, int leaveId, String token);
    ResponseEntity<String> rejectLeaveRequestByAdmin(int adminId, int leaveId, String token);
    ResponseEntity<List<Roles>> getAllRoles(int adminId);
    ResponseEntity<List<RoleBasedLeaves>> getAllRoleBasedLeavePolicies(int adminId);
    ResponseEntity<List<CountryCalendars>> getAllHolidays(int adminId);
    ResponseEntity<String> deleteUser(int adminId, int userId, String token);

    ResponseEntity<String> updateDetails(int adminId, Admins admin, String token);
    ResponseEntity<?> getAllLeaveBalance(int adminId,String token);
    ResponseEntity<?> uploadCalendar(int adminId, MultipartFile file, String token);
    ResponseEntity<String> updateCalendar(int adminId, List<Map<String, Object>> holidays, String token);
    
    ResponseEntity<?> updateSingleHoliday(int adminId,int holidayId,CountryCalendars countryCalendars,String token);
}
