package com.smartleavemanagement.tests;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;

import com.smartleavemanagement.model.*;
import com.smartleavemanagement.repository.*;
import com.smartleavemanagement.securityconfiguration.JwtUtil;
import com.smartleavemanagement.service.LeaveApplicationServiceImplementation;
import com.smartleavemanagement.enums.LeaveStatus;

@ExtendWith(MockitoExtension.class)
public class LeaveApplicationServiceImplementationTest {

    @Mock private UsersRepository usersRepository;
    @Mock private CountryCalendarsRepository countryCalendarsRepository;
    @Mock private UsersLeaveBalanceRepository usersLeaveBalanceRepository;
    @Mock private LeaveApplicationFormRepository leaveApplicationFormRepository;
    @Mock private RoleBasedLeavesRepository roleBasedLeavesRepository;
    @Mock private JavaMailSender mailSender;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private LeaveApplicationServiceImplementation leaveService;

    private Users mockUser;
    private String validToken = "valid.jwt.token";

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId(1);
        mockUser.setCountryName("India");
        mockUser.setUserRole("TEAM_MEMBER");
        mockUser.setFirstName("John");
    }

    @Test
    void testCalculateDuration_Unauthorized() {
        when(jwtUtil.validateToken(validToken)).thenReturn(false);

        ResponseEntity<?> response = leaveService.calculateDuration(1, LocalDate.now(), LocalDate.now().plusDays(1), validToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid or expired token", response.getBody());
    }

    @Test
    void testCalculateDuration_Success() {
        LocalDate startDate = LocalDate.of(2026, 3, 2); // A Monday
        LocalDate endDate = LocalDate.of(2026, 3, 3);   // A Tuesday
        
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUserId(validToken)).thenReturn(1L);
        when(usersRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(countryCalendarsRepository.findAllByCityName("India")).thenReturn(new ArrayList<>());
        when(leaveApplicationFormRepository.findByUserId(1)).thenReturn(new ArrayList<>());

        ResponseEntity<?> response = leaveService.calculateDuration(1, startDate, endDate, validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2.0f, (float) response.getBody());
    }

    @Test
    void testApplyLeave_InsufficientBalance() {
        LeaveApplicationForm form = new LeaveApplicationForm();
        form.setLeaveType("SICK");
        form.setStartDate(LocalDate.now().plusDays(5)); // Ensure it's not a weekend for logic
        form.setEndDate(LocalDate.now().plusDays(5));

        UsersLeaveBalance balance = new UsersLeaveBalance();
        balance.setSickLeave(0.0f); // Zero balance

        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUserId(validToken)).thenReturn(1L);
        when(usersRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(usersLeaveBalanceRepository.findByUser_UserId(1)).thenReturn(balance);

        ResponseEntity<String> response = leaveService.applyLeave(1, form, validToken);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Insufficient leave balance"));
    }

    @Test
    void testCancelLeave_Success() {
        int leaveId = 101;
        LeaveApplicationForm existingLeave = new LeaveApplicationForm();
        existingLeave.setLeaveId(leaveId);
        existingLeave.setLeaveStatus(LeaveStatus.PENDING);

        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUserId(validToken)).thenReturn(1L);
        when(leaveApplicationFormRepository.findByLeaveId(leaveId)).thenReturn(Optional.of(existingLeave));

        ResponseEntity<String> response = leaveService.cancelLeave(1, leaveId, validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Canceled Leave", response.getBody());
        assertEquals(LeaveStatus.CANCELED, existingLeave.getLeaveStatus());
        verify(leaveApplicationFormRepository, times(1)).save(existingLeave);
    }
}