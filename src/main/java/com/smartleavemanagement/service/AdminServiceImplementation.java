package com.smartleavemanagement.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smartleavemanagement.DTOs.LoginResponse;
import com.smartleavemanagement.controller.UsersController;
import com.smartleavemanagement.model.Admins;
import com.smartleavemanagement.model.CountryCalendars;
import com.smartleavemanagement.model.RegistrationHistory;
import com.smartleavemanagement.model.RoleBasedLeaves;
import com.smartleavemanagement.model.Roles;
import com.smartleavemanagement.model.Users;
import com.smartleavemanagement.model.UsersLeaveBalance;
import com.smartleavemanagement.repository.AdminsRepository;
import com.smartleavemanagement.repository.CountryCalendarsRepository;
import com.smartleavemanagement.repository.RegistrationHistoryRepository;
import com.smartleavemanagement.repository.RoleBasedLeavesRepository;
import com.smartleavemanagement.repository.RolesRepository;
import com.smartleavemanagement.repository.UsersLeaveBalanceRepository;
import com.smartleavemanagement.repository.UsersRepository;
import com.smartleavemanagement.securityconfiguration.JwtUtil;


@Service
public class AdminServiceImplementation implements AdminService{

    private final UsersController usersController;

    private final RolesRepository rolesRepository;

	private final AdminsRepository adminsRepository;
	
	private final CountryCalendarsRepository countryCalendarsRepository;

	private final RoleBasedLeavesRepository roleBasedLeavesRepository;
	
	private final JwtUtil jwtUtil;
	
	private final RegistrationHistoryRepository registrationHistoryRepository;
	
	private final UsersRepository usersRepository;
	
	private final UsersLeaveBalanceRepository usersLeaveBalanceRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public AdminServiceImplementation(AdminsRepository adminsRepository,JwtUtil jwtUtil,
			RegistrationHistoryRepository registrationHistoryRepository, RolesRepository rolesRepository,
			CountryCalendarsRepository countryCalendarsRepository, UsersController usersController,
			RoleBasedLeavesRepository roleBasedLeavesRepository,UsersRepository usersRepository,
			UsersLeaveBalanceRepository usersLeaveBalanceRepository)
	{
		this.adminsRepository=adminsRepository;
		this.jwtUtil=jwtUtil;
		
		this.registrationHistoryRepository=registrationHistoryRepository;
		this.countryCalendarsRepository=countryCalendarsRepository;
		this.rolesRepository = rolesRepository;
		this.usersController = usersController;
		this.roleBasedLeavesRepository=roleBasedLeavesRepository;
		this.usersRepository=usersRepository;
		this.usersLeaveBalanceRepository=usersLeaveBalanceRepository;
	}
	
	@Override
	public ResponseEntity<String> registerAdmin(Admins admins) {
	   

	    if (adminsRepository.existsByUserName(admins.getUserName())) {
	        return ResponseEntity.badRequest().body("Username already exists");
	    }

	    if (adminsRepository.existsByEmail(admins.getEmail())) {
	        return ResponseEntity.badRequest().body("Email already exists");
	    }

	    admins.setPassword(passwordEncoder.encode(admins.getPassword()));
	    admins.setRole("ADMIN");
	    adminsRepository.save(admins);
	    
	    
	    RegistrationHistory registrationHistory = new RegistrationHistory();
		registrationHistory.setFirstName(admins.getFirstName());
		registrationHistory.setLastName(admins.getLastName());
		registrationHistory.setEmail(admins.getEmail());
		registrationHistory.setUserId(admins.getAdminId());
		registrationHistory.setRegisterDate(LocalDateTime.now());
		registrationHistory.setRole(admins.getRole());
		registrationHistoryRepository.save(registrationHistory);
		
		
		Roles newRole = new Roles();
		newRole.setRoleName("ADMIN");
		newRole.setDescription("All-access system administrator with full control");
		rolesRepository.save(newRole);
		
		
	    return ResponseEntity.ok("Admin registered successfully");
	}


	@Override
	public ResponseEntity<?> login(String username, String password) {
	    Admins admin = adminsRepository.findByUserName(username).orElse(null);
	    if (admin == null) {
	        return ResponseEntity.status(404).body("User not found");
	    }

	    if (!passwordEncoder.matches(password, admin.getPassword())) {
	        return ResponseEntity.status(401).body("Invalid credentials");
	    }


	    String token = jwtUtil.generateToken(
	        admin.getUserName(),
	        (long) admin.getAdminId(),
	        admin.getRole()
	    );

	    LoginResponse response = new LoginResponse(
	        admin.getAdminId(),
	        admin.getRole(),
	        admin.getEmail(),
	        token
	    );

	    return ResponseEntity.ok(response);
	
	}
	
	
	@Override
	public ResponseEntity<String> addNewRole(int userId,String newRole,String description, String token)
	{
		
		
		if (!jwtUtil.validateToken(token)) {
	        return ResponseEntity.status(401).body("Invalid or expired token");
	    }

	    Long tokenUserId = jwtUtil.extractUserId(token);
	    if (tokenUserId == null || tokenUserId != userId) {
	        return ResponseEntity.status(403).body("You are not authorized to add the roles!");
	    }

	    Admins admin = adminsRepository.findById(userId).orElse(null);
	    if (admin == null) {
	        return ResponseEntity.status(404).body("User not found");
	    }
		
		Roles newRoles = new Roles();
		
		newRoles.setRoleName(newRole);
		newRoles.setDescription(description);
	
		rolesRepository.save(newRoles);
		
		return ResponseEntity.ok("New Role Added Successfully, Now add role based leave policies next.");
	}

	
	public ResponseEntity<String> addNewCountryCalendar(int userId,String countryName, int calendarYear, String holidayName,LocalDate holidayDate, String token){
		
		if (!jwtUtil.validateToken(token)) {
	        return ResponseEntity.status(401).body("Invalid or expired token");
	    }

	    Long tokenUserId = jwtUtil.extractUserId(token);
	    if (tokenUserId == null || tokenUserId != userId) {
	        return ResponseEntity.status(403).body("You are not authorized to add the roles!");
	    }

	    Admins admin = adminsRepository.findById(userId).orElse(null);
	    if (admin == null) {
	        return ResponseEntity.status(404).body("User not found");
	    }
	    
	    CountryCalendars newCountryCalender = new CountryCalendars();
	    newCountryCalender.setCountryName(countryName);
	    newCountryCalender.setCalendarYear(calendarYear);
	    newCountryCalender.setHolidayDate(holidayDate);
	    newCountryCalender.setHolidayName(holidayName);
	    newCountryCalender.setHolidayDay(holidayDate.getDayOfWeek());
		
	    countryCalendarsRepository.save(newCountryCalender);
	    
		return ResponseEntity.ok("Added Country Calendar Successfully");
	}
	
	
	public ResponseEntity<String> addNewLeavePolicies(int userId, RoleBasedLeaves roleBasedLeaves, String token) {
	    if (!jwtUtil.validateToken(token)) {
	        return ResponseEntity.status(401).body("Invalid or expired token");
	    }

	    Long tokenUserId = jwtUtil.extractUserId(token);
	    if (tokenUserId == null || tokenUserId != userId) {
	        return ResponseEntity.status(403).body("You are not authorized to add the roles!");
	    }

	    Admins admin = adminsRepository.findById(userId).orElse(null);
	    if (admin == null) {
	        return ResponseEntity.status(404).body("User not found");
	    }

	    Roles role = rolesRepository.findByRoleNameIgnoreCase(roleBasedLeaves.getRole()).orElse(null);
	    if (role == null) {
	        return ResponseEntity.status(404).body("Role not found");
	    }

	    RoleBasedLeaves newRoleBasedLeaves = new RoleBasedLeaves();
	    newRoleBasedLeaves.setRole(role.getRoleName());
	    newRoleBasedLeaves.setCasualLeave(roleBasedLeaves.getCasualLeave());
	    newRoleBasedLeaves.setEarnedLeave(roleBasedLeaves.getEarnedLeave());
	    newRoleBasedLeaves.setLossOfPay(roleBasedLeaves.getLossOfPay());
	    newRoleBasedLeaves.setMaternityLeave(roleBasedLeaves.getMaternityLeave());
	    newRoleBasedLeaves.setPaternityLeave(roleBasedLeaves.getPaternityLeave());
	    newRoleBasedLeaves.setSickLeave(roleBasedLeaves.getSickLeave());

	    float totalLeaves = roleBasedLeaves.getSickLeave() + roleBasedLeaves.getCasualLeave() +
	                        roleBasedLeaves.getEarnedLeave() + roleBasedLeaves.getLossOfPay() +
	                        roleBasedLeaves.getMaternityLeave() + roleBasedLeaves.getPaternityLeave();

	    newRoleBasedLeaves.setTotalLeaves(totalLeaves);
	    roleBasedLeavesRepository.save(newRoleBasedLeaves);

	    
	    return ResponseEntity.ok("Successfully added role-based leave and updated user leave balances.");
	}
	
	
	public ResponseEntity<String> promotionToUser(int userId,String roleName)
	{
		RoleBasedLeaves roleBasedLeaves = roleBasedLeavesRepository.findByRole(roleName).orElse(null);
		
		Roles role = rolesRepository.findByRoleNameIgnoreCase(roleName).orElse(null);
	    if (role == null) {
	        return ResponseEntity.status(404).body("Role not found");
	    }
			
	    
	    
	    
	    	Users usersWithRole = usersRepository.findById(userId).orElse(null);

	    	Roles assignedRole = usersWithRole.getRole();
		    if (assignedRole != null || assignedRole.getRoleName() != null) {
		        assignedRole = rolesRepository.findByRoleNameIgnoreCase(roleName).orElse(null);
		        if (assignedRole == null) {
		            return ResponseEntity.status(500).body("Default role not found");
		        }
		    }
	    	
	    	
	    	UsersLeaveBalance balance = usersLeaveBalanceRepository.findByUser_UserId(userId);
	    	
	    	usersWithRole.setRole(assignedRole);
	    	usersWithRole.setUserRole(role.getRoleName());
	    	
	        balance.setUser(usersWithRole); 
	        balance.setRole(role.getRoleName());
	        balance.setCasualLeave(roleBasedLeaves.getCasualLeave());
	        balance.setEarnedLeave(roleBasedLeaves.getEarnedLeave());
	        balance.setLossOfPay(roleBasedLeaves.getLossOfPay());
	        balance.setMaternityLeave(roleBasedLeaves.getMaternityLeave());
	        balance.setPaternityLeave(roleBasedLeaves.getPaternityLeave());
	        balance.setSickLeave(roleBasedLeaves.getSickLeave());
	        float totalLeaves = roleBasedLeaves.getSickLeave() + roleBasedLeaves.getCasualLeave() +
                    roleBasedLeaves.getEarnedLeave() + roleBasedLeaves.getLossOfPay() +
                    roleBasedLeaves.getMaternityLeave() + roleBasedLeaves.getPaternityLeave();
	        
	        balance.setTotalLeaves(totalLeaves);

	        usersLeaveBalanceRepository.save(balance);
	        usersRepository.save(usersWithRole);
	        
	    
	    return ResponseEntity.ok("Successfully added leaves to user");

	}
	

}
