package com.smartleavemanagement.tests;
import com.smartleavemanagement.DTOs.UserLeaveBalancedays;
import com.smartleavemanagement.enums.OtpStatus;
import com.smartleavemanagement.model.CountryCalendars;
import com.smartleavemanagement.model.LeaveApplicationForm;
import com.smartleavemanagement.model.RoleBasedLeaves;
import com.smartleavemanagement.model.Roles;
import com.smartleavemanagement.model.Users;
import com.smartleavemanagement.model.UsersLeaveBalance;
import com.smartleavemanagement.repository.*;
import com.smartleavemanagement.securityconfiguration.JwtUtil;
import com.smartleavemanagement.service.UsersServiceImplementation;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Consolidated tests for UsersServiceImplementation.
 * Organized with @Nested to keep related tests together.
 */
@ExtendWith(MockitoExtension.class)
class UsersServiceImplementationTest {


    @Mock UsersRepository usersRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtUtil jwtUtil;
    @Mock RolesRepository rolesRepository;
    @Mock JavaMailSender mailSender;
    @Mock RegistrationHistoryRepository registrationHistoryRepository;
    @Mock CountryCalendarsRepository countryCalendarsRepository;
    @Mock UsersLeaveBalanceRepository usersLeaveBalanceRepository;
    @Mock RoleBasedLeavesRepository roleBasedLeavesRepository;
    @Mock LeaveApplicationFormRepository leaveApplicationFormRepository;

    @InjectMocks
    UsersServiceImplementation service;
    

    @Nested
    class AuthAndOtpTests {

        @Test
        void login_userNotFound_returns404() {
            when(usersRepository.findByUserName("alice")).thenReturn(Optional.empty());

            ResponseEntity<?> res = service.login("alice", "pwd");

            assertEquals(404, res.getStatusCodeValue());
            assertEquals("User not found", res.getBody());
            verifyNoInteractions(passwordEncoder, jwtUtil);
        }

        

        @Test
        void login_success_returnsToken() {
            Users u = user(101, "alice", "ENC", "alice@example.com", role("TEAM_MEMBER"));
            when(usersRepository.findByUserName("alice")).thenReturn(Optional.of(u));
            when(passwordEncoder.matches("good", "ENC")).thenReturn(true);
            when(jwtUtil.generateToken("alice", 101L, "TEAM_MEMBER")).thenReturn("jwt-123");

            ResponseEntity<?> res = service.login("alice", "good");

            assertEquals(200, res.getStatusCodeValue());
            assertTrue(res.getBody() instanceof com.smartleavemanagement.DTOs.LoginResponse);
            var lr = (com.smartleavemanagement.DTOs.LoginResponse) res.getBody();
            assertEquals(101, lr.getUserId());
            assertEquals("TEAM_MEMBER", lr.getRole());
            assertEquals("alice@example.com", lr.getEmail());
            assertEquals("jwt-123", lr.getToken());
        }



        @Test
        void generateOtp_success_setsOtpPending_sendsMail_200() {
            Users u = user(10, "u", "enc", "u@x.com", role("TEAM_MEMBER"));
            u.setFirstName("U");
            u.setLastName("Ser");
            when(usersRepository.findByEmail("u@x.com")).thenReturn(Optional.of(u));

            ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
            ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

            ResponseEntity<String> res = service.generateOtp("u@x.com", "password");

            assertEquals(200, res.getStatusCodeValue());
            assertEquals("OTP generated and sent to email", res.getBody());

            verify(usersRepository).save(userCaptor.capture());
            Users saved = userCaptor.getValue();
            assertEquals(OtpStatus.PENDING, saved.getOtpStatus());
            assertTrue(saved.getOtp() >= 1000 && saved.getOtp() <= 9999);

            verify(mailSender).send(mailCaptor.capture());
            SimpleMailMessage msg = mailCaptor.getValue();
            assertArrayEquals(new String[]{"u@x.com"}, msg.getTo());
            assertEquals("Your OTP for password recovery", msg.getSubject());
            assertTrue(msg.getText().contains(String.valueOf(saved.getOtp())));
            assertTrue(msg.getText().contains("Dear U Ser"));
        }



        @Test
        void verifyOtp_notPending_setsExpired_400() {
            Users u = user(7, "bob", "e", "b@x.com", role("TEAM_MEMBER"));
            u.setOtp(1234);
            u.setOtpStatus(OtpStatus.GENERATE);
            when(usersRepository.findByOtp(1234)).thenReturn(Optional.of(u));

            ResponseEntity<String> res = service.verifyOtp(1234, "password");

            assertEquals(400, res.getStatusCodeValue());
            assertEquals("OTP expired or already used", res.getBody());
            assertEquals(OtpStatus.EXPIRED, u.getOtpStatus());
            verify(usersRepository).save(u);
        }



        @Test
        void updatePassword_success_200() {
            Users u = user(101, "alice", "ENC_OLD", "a@x.com", role("TEAM_MEMBER"));
            u.setOtpStatus(OtpStatus.VERIFIED);
            when(jwtUtil.validateToken("t")).thenReturn(true);
            when(jwtUtil.extractUserId("t")).thenReturn(101L);
            when(usersRepository.findById(101)).thenReturn(Optional.of(u));
            when(passwordEncoder.matches("old", "ENC_OLD")).thenReturn(true);
            when(passwordEncoder.encode("new")).thenReturn("ENC_NEW");

            var res = service.updatePassword(101, "old", "new", "t");

            assertEquals(200, res.getStatusCodeValue());
            assertEquals("Password updated successfully", res.getBody());
            assertEquals("ENC_NEW", u.getPassword());
            assertEquals(OtpStatus.GENERATE, u.getOtpStatus());
            verify(usersRepository).save(u);
        }
    }
    

 private Users minimalUser() {
     Users users = new Users();

     users.setFirstName("Sai Kishore");
     users.setLastName("Manthri");

     users.setEmail("user1@example.com");     
     users.setPhoneNumber("9876543210");         
     users.setAddress("Kolkata, WB");            

     users.setGender(com.smartleavemanagement.enums.Gender.MALE);

     users.setUserName("user1");                
     users.setPassword("pwd");                  

     users.setCountryName("India");
     users.setCityName("Kolkata");

     return users;
 }

    @Nested
    class RegistrationAndLifecycleTests {

        @Test
        void deleteAccount_happyPath_deletesAll_200() {
            when(jwtUtil.validateToken("t")).thenReturn(true);
            when(jwtUtil.extractUserId("t")).thenReturn(10L);

            Users user = user(10, "u", "p", "e@x.com", role("TEAM_MEMBER"));
            UsersLeaveBalance bal = new UsersLeaveBalance();
            bal.setUser(user);
            bal.setRole("TEAM_MEMBER");

            LeaveApplicationForm f1 = new LeaveApplicationForm();
            LeaveApplicationForm f2 = new LeaveApplicationForm();
            List<LeaveApplicationForm> forms = List.of(f1, f2);

            when(usersRepository.findById(10)).thenReturn(Optional.of(user));
            when(usersLeaveBalanceRepository.findByUser_UserId(10)).thenReturn(bal);
            when(leaveApplicationFormRepository.findByUserId(10)).thenReturn(forms);

            var res = service.deleteAccount(10, "t");

            assertEquals(200, res.getStatusCodeValue());
            assertEquals("Successfully Deleted Account", res.getBody());
            verify(leaveApplicationFormRepository, times(1)).delete(f1);
            verify(leaveApplicationFormRepository, times(1)).delete(f2);
            verify(usersLeaveBalanceRepository).delete(bal);
            verify(usersRepository).delete(user);
        }
    }


    private Users user(int id, String username, String encPwd, String email, Roles role) {
        Users u = new Users();
        u.setUserId(id);
        u.setUserName(username);
        u.setPassword(encPwd);
        u.setEmail(email);
        u.setRole(role);
        u.setUserRole(role != null ? role.getRoleName() : null);
        u.setFirstName("First");
        u.setLastName("Last");
        u.setCountryName("India");
        u.setCityName("Kolkata");
        return u;
    }

    private Roles role(String name) {
        Roles r = new Roles();
        r.setRoleName(name);
        return r;
    }

    private CountryCalendars calendar(String country, String city, String name, LocalDate date, int year, DayOfWeek day) {
        CountryCalendars c = new CountryCalendars();
        c.setCountryName(country);
        c.setCityName(city);
        c.setHolidayName(name);
        c.setHolidayDate(date);
        c.setCalendarYear(year);
        c.setHolidayDay(day);
        return c;
    }
}