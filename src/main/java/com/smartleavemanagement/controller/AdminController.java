package com.smartleavemanagement.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartleavemanagement.DTOs.LoginDetails;
import com.smartleavemanagement.model.Admins;
import com.smartleavemanagement.model.CountryCalendars;
import com.smartleavemanagement.model.RoleBasedLeaves;
import com.smartleavemanagement.model.Roles;
import com.smartleavemanagement.model.Users;
import com.smartleavemanagement.repository.AdminsRepository;
import com.smartleavemanagement.repository.UsersRepository;
import com.smartleavemanagement.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final UsersRepository usersRepository;
    private final AdminsRepository adminsRepository;

    public AdminController(AdminService adminService, UsersRepository usersRepository, AdminsRepository adminsRepository) {
        this.adminService = adminService;
        this.usersRepository = usersRepository;
        this.adminsRepository = adminsRepository;
    }

    @PostMapping("/registration")
    public ResponseEntity<String> registerAdmin(@RequestBody Admins admins) {
        return adminService.registerAdmin(admins);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDetails loginDetails) {
        return adminService.login(loginDetails.getUserName(), loginDetails.getPassword());
    }

    @PostMapping("/add-newrole/{adminId}")
    public ResponseEntity<String> addNewRole(@PathVariable int adminId,
                                             @RequestBody Roles roles,
                                             @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return adminService.addNewRole(adminId, roles.getRoleName(), roles.getDescription(), token);
    }

    @PostMapping("/add-new-country-calendar/{adminId}")
    public ResponseEntity<String> addNewCountryCalendar(@PathVariable int adminId,
                                                        @RequestBody CountryCalendars countryCalendars,
                                                        @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return adminService.addNewCountryCalendar(
                adminId,
                countryCalendars.getCountryName(),
                countryCalendars.getCalendarYear(),
                countryCalendars.getHolidayName(),
                countryCalendars.getHolidayDate(),
                token
        );
    }

    @PostMapping("/add-new-leave-policies/{adminId}")
    public ResponseEntity<String> addNewLeavePolicies(@PathVariable int adminId,
                                                      @RequestBody RoleBasedLeaves roleBasedLeaves,
                                                      @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return adminService.addNewLeavePolicies(adminId, roleBasedLeaves, token);
    }

    @PutMapping("/promote/{adminId}/{userId}/{roleName}")
    public ResponseEntity<String> promotionToUser(@PathVariable int adminId,
                                                  @PathVariable int userId,
                                                  @PathVariable String roleName,
                                                  @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return adminService.promotionToUser(adminId, userId, roleName, token);
    }

    @PostMapping("/approve/{adminId}/{leaveId}")
    public ResponseEntity<String> approveLeave(@PathVariable int adminId,
                                               @PathVariable int leaveId,
                                               @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return adminService.approveLeaveRequestByAdmin(adminId, leaveId, token);
    }

    @PostMapping("/reject/{adminId}/{leaveId}")
    public ResponseEntity<String> rejectLeave(@PathVariable int adminId,
                                              @PathVariable int leaveId,
                                              @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return adminService.rejectLeaveRequestByAdmin(adminId, leaveId, token);
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> requests = usersRepository.findAll();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/get-admin-details/{adminId}")
    public Optional<Admins> getAdminDetails(@PathVariable int adminId) {
        return adminsRepository.findById(adminId);
    }

    @GetMapping("/get-all-leave-requests/{adminId}")
    public ResponseEntity<?> getAllLeaveRequests(@PathVariable int adminId) {
        return adminService.getAllLeaveRequests(adminId);
    }

    @GetMapping("/get-all-roles/{adminId}")
    public ResponseEntity<List<Roles>> getAllRoles(@PathVariable int adminId) {
        return adminService.getAllRoles(adminId);
    }

    @GetMapping("/get-all-roles-based-leaves-policies/{adminId}")
    public ResponseEntity<List<RoleBasedLeaves>> getAllRoleBasedLeavePolicies(@PathVariable int adminId) {
        return adminService.getAllRoleBasedLeavePolicies(adminId);
    }

    @GetMapping("/get-all-holidays/{adminId}")
    public ResponseEntity<List<CountryCalendars>> getAllHolidays(@PathVariable int adminId) {
        return adminService.getAllHolidays(adminId);
    }
}
