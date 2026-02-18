package com.smartleavemanagement.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.smartleavemanagement.enums.*;
import com.smartleavemanagement.model.*;
import com.smartleavemanagement.repository.*;
import com.smartleavemanagement.securityconfiguration.JwtUtil;
import com.smartleavemanagement.service.AdminServiceImplementation;
import com.smartleavemanagement.DTOs.LoginResponse;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplementationTest {

    @InjectMocks
    private AdminServiceImplementation adminService;

    @Mock private AdminsRepository adminsRepository;
    @Mock private RolesRepository rolesRepository;
    @Mock private RegistrationHistoryRepository registrationHistoryRepository;
    @Mock private CountryCalendarsRepository countryCalendarsRepository;
    @Mock private RoleBasedLeavesRepository roleBasedLeavesRepository;
    @Mock private UsersRepository usersRepository;
    @Mock private UsersLeaveBalanceRepository usersLeaveBalanceRepository;
    @Mock private LeaveApplicationFormRepository leaveApplicationFormRepository;
    @Mock private JwtUtil jwtUtil;
    @Mock private PasswordEncoder passwordEncoder;

    private Admins admin;

    @BeforeEach
    void setup() {
        admin = new Admins();
        admin.setUserName("Admin2@123");
        admin.setEmail("admin2@test.com");
        admin.setPassword("Admin2@123");
        admin.setAddress("Kolkata");
        admin.setFirstName("Admin");
        admin.setLastName("Two");
        admin.setGender(Gender.FEMALE);
        admin.setPhoneNumber("+91 9876543210");
     
    }


    @Test
    void registerAdmin_success() {
   
        ResponseEntity<String> response = adminService.registerAdmin(admin);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Admin registered successfully", response.getBody());
    }


    @Test 
    void registerAdmin_usernameExists() 
    { 
    	when(adminsRepository.existsByUserName(admin.getUserName())).thenReturn(true);
    	ResponseEntity<String> response = adminService.registerAdmin(admin);
    	assertEquals(400, response.getStatusCodeValue()); 
    	assertEquals("Username already exists", response.getBody()); 
    }

    @Test
    void login_success() {
        Admins existingAdmin = new Admins();
        existingAdmin.setAdminId(1);
        existingAdmin.setUserName("Admin1@123");
        existingAdmin.setPassword("encodedPassword");
        existingAdmin.setRole("ADMIN");
        existingAdmin.setEmail("admin1@test.com");

        when(adminsRepository.findByUserName("Admin1@123")).thenReturn(Optional.of(existingAdmin));
        when(passwordEncoder.matches("Admin1@123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyLong(), anyString())).thenReturn("jwt-token");

        ResponseEntity<?> response = adminService.login("Admin1@123", "Admin1@123");

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof LoginResponse);
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertEquals("jwt-token", loginResponse.getToken());
    }




    @Test
    void addNewRole_success() {
        Admins existingAdmin = new Admins();
        existingAdmin.setAdminId(1);
        existingAdmin.setRole("ADMIN");

        when(jwtUtil.validateToken("token")).thenReturn(true);
        when(jwtUtil.extractUserId("token")).thenReturn(1L);
        when(adminsRepository.findById(1)).thenReturn(Optional.of(existingAdmin));

        ResponseEntity<String> response = adminService.addNewRole(1, "MANAGER", "Manager Role", "token");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("New Role Added Successfully, Now add role based leave policies next.", response.getBody());
    }


    @Test
    void addNewRole_invalidToken() {
        when(jwtUtil.validateToken("token")).thenReturn(false);
        ResponseEntity<String> response = adminService.addNewRole(1, "MANAGER", "Role", "token");
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid or expired token", response.getBody());
    }

    @Test
    void approveLeave_success() {
        LeaveApplicationForm leave = new LeaveApplicationForm();
        leave.setLeaveId(10);
        leave.setUserId(5);
        leave.setLeaveType("SICK");
        leave.setDuration(1);
        leave.setLeaveStatus(LeaveStatus.PENDING);

        UsersLeaveBalance balance = new UsersLeaveBalance();
        balance.setSickLeave(5);
        balance.setTotalLeaves(10);

        when(jwtUtil.validateToken("token")).thenReturn(true);
        when(jwtUtil.extractUserId("token")).thenReturn(1L);
        when(leaveApplicationFormRepository.findById(10)).thenReturn(Optional.of(leave));
        when(usersLeaveBalanceRepository.findByUser_UserId(5)).thenReturn(balance);

        ResponseEntity<String> response = adminService.approveLeaveRequestByAdmin(1, 10, "token");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Leave request approved successfully.", response.getBody());
        verify(usersLeaveBalanceRepository).save(balance);
        verify(leaveApplicationFormRepository).save(leave);
    }

    @Test
    void deleteUser_success() {
        Users user = new Users();
        user.setUserId(5);
        user.setFirstName("John");
        user.setLastName("Doe");

        UsersLeaveBalance balance = new UsersLeaveBalance();
        List<LeaveApplicationForm> leaves = new ArrayList<>();

        when(jwtUtil.validateToken("token")).thenReturn(true);
        when(jwtUtil.extractUserId("token")).thenReturn(1L);
        when(usersRepository.findById(5)).thenReturn(Optional.of(user));
        when(usersLeaveBalanceRepository.findByUser_UserId(5)).thenReturn(balance);
        when(leaveApplicationFormRepository.findByUserId(5)).thenReturn(leaves);

        ResponseEntity<String> response = adminService.deleteUser(1, 5, "token");

        assertEquals(200, response.getStatusCodeValue());
        verify(usersRepository).delete(user);
        verify(usersLeaveBalanceRepository).delete(balance);
    }
}
