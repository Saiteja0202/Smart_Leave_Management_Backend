package com.smartleavemanagement.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import com.smartleavemanagement.model.*;

public interface AdminService {

    ResponseEntity<String> registerAdmin(Admins admins);
    ResponseEntity<?> login(String username, String password);

    ResponseEntity<String> addNewRole(int userId, String newRole, String description, String token);
    ResponseEntity<String> addNewCountryCalendar(int userId, String countryName, int calendarYear, String holidayName, LocalDate holidayDate, String token);
    ResponseEntity<String> addNewLeavePolicies(int userId, RoleBasedLeaves roleBasedLeaves, String token);
    ResponseEntity<String> promotionToUser(int adminId, int userId, String roleName, String token);
    ResponseEntity<?> getAllLeaveRequests(int adminId);
    ResponseEntity<String> approveLeaveRequestByAdmin(int adminId, int leaveId, String token);
    ResponseEntity<String> rejectLeaveRequestByAdmin(int adminId, int leaveId, String token);
    ResponseEntity<List<Roles>> getAllRoles(int adminId);
    ResponseEntity<List<RoleBasedLeaves>> getAllRoleBasedLeavePolicies(int adminId);
    ResponseEntity<List<CountryCalendars>> getAllHolidays(int adminId);
}
