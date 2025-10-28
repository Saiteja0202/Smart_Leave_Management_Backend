package com.smartleavemanagement.service;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.smartleavemanagement.model.Admins;
import com.smartleavemanagement.model.CountryCalendars;
import com.smartleavemanagement.model.RoleBasedLeaves;

public interface AdminService {
	
	ResponseEntity<String> registerAdmin(Admins admins);
	ResponseEntity<?> login(String username, String password);
	ResponseEntity<String> addNewRole(int UserId,String newRole,String description,String token);
	ResponseEntity<String> addNewCountryCalendar(int userId,String countryName, int calendarYear, String holidayName,LocalDate hoildayDate, String token);
	ResponseEntity<String> addNewLeavePolicies(int userId, RoleBasedLeaves roleBasedLeaves, String token);
	ResponseEntity<String> promotionToUser(int userId,String roleName);
	ResponseEntity<?> getAllLeaveRequests(int adminId);
	ResponseEntity<String> approveLeaveRequestByAdmin(int adminId, int leaveId);
	ResponseEntity<String> rejectLeaveRequestByAdmin(int userId, int requesterId);
}
