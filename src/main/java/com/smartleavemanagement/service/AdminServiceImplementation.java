package com.smartleavemanagement.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.poi.ss.usermodel.Cell;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smartleavemanagement.DTOs.LeaveRequests;
import com.smartleavemanagement.DTOs.LoginResponse;
import com.smartleavemanagement.enums.LeaveStatus;
import com.smartleavemanagement.model.Admins;
import com.smartleavemanagement.model.CountryCalendars;
import com.smartleavemanagement.model.LeaveApplicationForm;
import com.smartleavemanagement.model.RegistrationHistory;
import com.smartleavemanagement.model.RoleBasedLeaves;
import com.smartleavemanagement.model.Roles;
import com.smartleavemanagement.model.Users;
import com.smartleavemanagement.model.UsersLeaveBalance;
import com.smartleavemanagement.repository.AdminsRepository;
import com.smartleavemanagement.repository.CountryCalendarsRepository;
import com.smartleavemanagement.repository.LeaveApplicationFormRepository;
import com.smartleavemanagement.repository.RegistrationHistoryRepository;
import com.smartleavemanagement.repository.RoleBasedLeavesRepository;
import com.smartleavemanagement.repository.RolesRepository;
import com.smartleavemanagement.repository.UsersLeaveBalanceRepository;
import com.smartleavemanagement.repository.UsersRepository;
import com.smartleavemanagement.securityconfiguration.JwtUtil;


import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;



@Service
public class AdminServiceImplementation implements AdminService{

    

    @Value("${file.url}")
    private String FILE_URL;
    
    private final RolesRepository rolesRepository;

	private final AdminsRepository adminsRepository;
	
	private final CountryCalendarsRepository countryCalendarsRepository;

	private final RoleBasedLeavesRepository roleBasedLeavesRepository;
	
	private final JwtUtil jwtUtil;
	
	private final RegistrationHistoryRepository registrationHistoryRepository;
	
	private final UsersRepository usersRepository;
	
	private final UsersLeaveBalanceRepository usersLeaveBalanceRepository;
	
	private final LeaveApplicationFormRepository leaveApplicationFormRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public AdminServiceImplementation(AdminsRepository adminsRepository,JwtUtil jwtUtil,
			RegistrationHistoryRepository registrationHistoryRepository, RolesRepository rolesRepository,
			CountryCalendarsRepository countryCalendarsRepository,
			RoleBasedLeavesRepository roleBasedLeavesRepository,UsersRepository usersRepository,
			UsersLeaveBalanceRepository usersLeaveBalanceRepository,
			LeaveApplicationFormRepository leaveApplicationFormRepository)
	{
		this.adminsRepository=adminsRepository;
		this.jwtUtil=jwtUtil;
		this.leaveApplicationFormRepository=leaveApplicationFormRepository;
		this.registrationHistoryRepository=registrationHistoryRepository;
		this.countryCalendarsRepository=countryCalendarsRepository;
		this.rolesRepository = rolesRepository;
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
		
		Roles existingRole = rolesRepository.findByRoleNameIgnoreCase(admins.getRole()).orElse(null);
		
		if(existingRole == null)
		{
			Roles newRole = new Roles();
			newRole.setRoleName("ADMIN");
			newRole.setDescription("All-access system administrator with full control");
			rolesRepository.save(newRole);
		}
		
		
		
		
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
	        return ResponseEntity.status(403).body("You are not authorized to add  new Holidays!");
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
	
	
	public ResponseEntity<String> promotionToUser(int adminId,int userId,String roleName, String token)
	{
		

		
		if (!jwtUtil.validateToken(token)) {
	        return ResponseEntity.status(401).body("Invalid or expired token");
	    }

	    Long tokenUserId = jwtUtil.extractUserId(token);
	    if (tokenUserId == null || tokenUserId != adminId) {
	        return ResponseEntity.status(403).body("You are not authorized to promote the user!");
	    }
		
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
	
	public ResponseEntity<?> getAllLeaveRequests(int adminId)
	{
		
		
		List<LeaveApplicationForm> allUsersLeaveRequests = leaveApplicationFormRepository.findAll();
		
		if(allUsersLeaveRequests == null)
		{
			return ResponseEntity.badRequest().body("Leave Requests are Not Found !");
		}
		
	
		ArrayList<LeaveRequests> allUsersLeavesRequestsList = new ArrayList<LeaveRequests>();
		
		for(LeaveApplicationForm singleUserLeavesRequests : allUsersLeaveRequests)
		{
			LeaveRequests newLeaveRequests = new LeaveRequests();
			Users user = usersRepository.findById(singleUserLeavesRequests.getUserId()).orElse(null);
			newLeaveRequests.setUserName(user.getUserName());
			newLeaveRequests.setLeaveId(singleUserLeavesRequests.getLeaveId());
			newLeaveRequests.setUserId(singleUserLeavesRequests.getUserId());
			newLeaveRequests.setUserRole(singleUserLeavesRequests.getRoleName());
			newLeaveRequests.setLeaveType(singleUserLeavesRequests.getLeaveType());
			newLeaveRequests.setStartDate(singleUserLeavesRequests.getStartDate());
			newLeaveRequests.setEndDate(singleUserLeavesRequests.getEndDate());
			newLeaveRequests.setDuration(singleUserLeavesRequests.getDuration());
			newLeaveRequests.setApprover(singleUserLeavesRequests.getApprover());
			newLeaveRequests.setLeaveStatus(singleUserLeavesRequests.getLeaveStatus());
			allUsersLeavesRequestsList.add(newLeaveRequests);
		}
		
		
		return ResponseEntity.ok(allUsersLeavesRequestsList);
	}
	
	public ResponseEntity<String> approveLeaveRequestByAdmin(int adminId, int leaveId, String token) {
//	    Users admin = usersRepository.findById(adminId).orElse(null);
//	    if (admin == null) {
//	        return ResponseEntity.badRequest().body("Unauthorized access. Only ADMIN can approve leave requests.");
//	    }

	    if (!jwtUtil.validateToken(token)) {
	        return ResponseEntity.status(401).body("Invalid or expired token");
	    }

	    Long tokenUserId = jwtUtil.extractUserId(token);
	    if (tokenUserId == null || tokenUserId != adminId) {
	        return ResponseEntity.status(403).body("You are not authorized to Approve the leave!");
	    }
	    
	    LeaveApplicationForm request = leaveApplicationFormRepository.findById(leaveId).orElse(null);
	    if (request == null || request.getLeaveStatus() != LeaveStatus.PENDING) {
	        return ResponseEntity.badRequest().body("Leave request not found or already processed.");
	    }

	    UsersLeaveBalance balance = usersLeaveBalanceRepository.findByUser_UserId(request.getUserId());
	    float duration = request.getDuration();
	    String leaveType = request.getLeaveType().toUpperCase();

	    switch (leaveType) {
	        case "SICK":
	            balance.setSickLeave(balance.getSickLeave() - duration);
	            break;
	        case "CASUAL":
	            balance.setCasualLeave(balance.getCasualLeave() - duration);
	            break;
	        case "PATERNITY":
	            balance.setPaternityLeave(balance.getPaternityLeave() - duration);
	            break;
	        case "MATERNITY":
	            balance.setMaternityLeave(balance.getMaternityLeave() - duration);
	            break;
	        case "EARNED":
	            balance.setEarnedLeave(balance.getEarnedLeave() - duration);
	            break;
	        default:
	            return ResponseEntity.badRequest().body("Invalid leave type.");
	    }

	    balance.setTotalLeaves(balance.getTotalLeaves() - duration);
	    request.setLeaveStatus(LeaveStatus.APPROVED);

	    usersLeaveBalanceRepository.save(balance);
	    leaveApplicationFormRepository.save(request);

	    return ResponseEntity.ok("Leave request approved successfully.");
	}
	
	public ResponseEntity<String> rejectLeaveRequestByAdmin(int adminId, int leaveId, String token) {
		
		
		if (!jwtUtil.validateToken(token)) {
	        return ResponseEntity.status(401).body("Invalid or expired token");
	    }

	    Long tokenUserId = jwtUtil.extractUserId(token);
	    if (tokenUserId == null || tokenUserId != adminId) {
	        return ResponseEntity.status(403).body("You are not authorized to Reject Leave!");
	    }
		
	    Users admin = usersRepository.findById(adminId).orElse(null);
	    if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getUserRole())) {
	        return ResponseEntity.badRequest().body("Unauthorized access. Only ADMIN can reject leave requests.");
	    }

	    LeaveApplicationForm request = leaveApplicationFormRepository.findById(leaveId).orElse(null);
	    if (request == null || request.getLeaveStatus() != LeaveStatus.PENDING) {
	        return ResponseEntity.badRequest().body("Leave request not found or already processed.");
	    }

	    request.setLeaveStatus(LeaveStatus.REJECTED);
	    leaveApplicationFormRepository.save(request);
	    
	    

	    return ResponseEntity.ok("Leave request rejected successfully.");
	}

	
	public ResponseEntity<List<Roles>> getAllRoles(int adminId)
	{
		List<Roles> roles = rolesRepository.findAll();
		

		if(roles == null)
		{
			ResponseEntity.badRequest().body("Not Found");
		}
		
		ArrayList<Roles> rolesList = new ArrayList<Roles>();
		for(Roles role:roles)
		{
			role.setRoleName(role.getRoleName());
			role.setDescription(role.getDescription());
			rolesList.add(role);
		}
		return ResponseEntity.ok(rolesList);
	}

	
	public ResponseEntity<List<RoleBasedLeaves>> getAllRoleBasedLeavePolicies(int adminId)
	{
		
		List<RoleBasedLeaves> roleBasedLeaves = roleBasedLeavesRepository.findAll();
		
		if(roleBasedLeaves == null)
		{
			ResponseEntity.badRequest().body("Not Found");
		}
		
		
		ArrayList<RoleBasedLeaves> roleBasedLeavesList = new ArrayList<RoleBasedLeaves>();
		for(RoleBasedLeaves newRoleBasedLeaves : roleBasedLeaves)
		{
			newRoleBasedLeaves.setRole(newRoleBasedLeaves.getRole());
			newRoleBasedLeaves.setCasualLeave(newRoleBasedLeaves.getCasualLeave());
			newRoleBasedLeaves.setEarnedLeave(newRoleBasedLeaves.getEarnedLeave());
			newRoleBasedLeaves.setPaternityLeave(newRoleBasedLeaves.getPaternityLeave());
			newRoleBasedLeaves.setMaternityLeave(newRoleBasedLeaves.getMaternityLeave());
			newRoleBasedLeaves.setSickLeave(newRoleBasedLeaves.getSickLeave());
			newRoleBasedLeaves.setTotalLeaves(newRoleBasedLeaves.getTotalLeaves());
			roleBasedLeavesList.add(newRoleBasedLeaves);
		}
		return ResponseEntity.ok(roleBasedLeavesList);
	}
	
	
	public ResponseEntity<List<CountryCalendars>> getAllHolidays(int adminId)
	{
		
		List<CountryCalendars> countryCalendars = countryCalendarsRepository.findAll();
		
		if(countryCalendars == null)
		{
			ResponseEntity.badRequest().body("Not Found");
		}
		
		ArrayList<CountryCalendars> countryCalendarsList = new ArrayList<CountryCalendars>();
		for(CountryCalendars newCountryCalendars : countryCalendars)
		{
			newCountryCalendars.setCalendarYear(newCountryCalendars.getCalendarYear());
			newCountryCalendars.setCountryName(newCountryCalendars.getCountryName());
			newCountryCalendars.setHolidayDate(newCountryCalendars.getHolidayDate());
			newCountryCalendars.setHolidayName(newCountryCalendars.getHolidayName());
			countryCalendarsList.add(newCountryCalendars);
		}
		
		
		return ResponseEntity.ok(countryCalendarsList);
	}
	
	
	public ResponseEntity<String> deleteUser(int adminId, int userId, String token)
	{
		if (!jwtUtil.validateToken(token)) {
	        return ResponseEntity.status(401).body("Invalid or expired token");
	    }

	    Long tokenUserId = jwtUtil.extractUserId(token);
	    if (tokenUserId == null || tokenUserId != adminId) {
	        return ResponseEntity.status(403).body("You are not authorized to Delete User!");
	    }

		Users user = usersRepository.findById(userId).orElse(null);
		UsersLeaveBalance usersLeaveBalance = usersLeaveBalanceRepository.findByUser_UserId(userId);
		List<LeaveApplicationForm> leaveApplicationForm = leaveApplicationFormRepository.findByUserId(userId);
	
		if(user == null || usersLeaveBalance ==null || leaveApplicationForm==null)
		{
			return ResponseEntity.badRequest().body("User Not Found");
		}
		
		for(LeaveApplicationForm newLeaveApplicationForm : leaveApplicationForm)
		{
			leaveApplicationFormRepository.delete(newLeaveApplicationForm);
		}
		
		usersLeaveBalanceRepository.delete(usersLeaveBalance);
		usersRepository.delete(user);
		
		 
		return ResponseEntity.ok("User "+user.getFirstName() +" "+user.getLastName()+" deleted Successfully");
	}
	
	
	
	@Override
	public ResponseEntity<String> updateDetails(int adminId, Admins admin, String token){
		
		if (!jwtUtil.validateToken(token)) {
	        return ResponseEntity.status(401).body("Invalid or expired token");
	    }

	    Long tokenUserId = jwtUtil.extractUserId(token);
	    if (tokenUserId == null || tokenUserId != adminId) {
	        return ResponseEntity.status(403).body("You are not authorized to Update Admin Details!");
	    }
		
	    Admins existingData = adminsRepository.findById(adminId).orElse(admin);
	    if(existingData == null)
	    {
	    	return ResponseEntity.badRequest().body("Admin Not Found");
	    }
	    
	    existingData.setFirstName(admin.getFirstName());
	    existingData.setLastName(admin.getLastName());
	    existingData.setEmail(admin.getEmail());
	    existingData.setAddress(admin.getAddress());
	    existingData.setGender(admin.getGender());
	    existingData.setPhoneNumber(admin.getPhoneNumber());
	    adminsRepository.save(existingData);
	    
	    
		return ResponseEntity.ok("Successfully updated details.");
	}
	
	
	/*
	 * 
	 */
	
	

    @Override
    public ResponseEntity<String> syncHolidays() {
        List<CountryCalendars> excelHolidays = fetchHolidaysFromExcel(FILE_URL);
        List<CountryCalendars> dbHolidays = countryCalendarsRepository.findAll();

        if(excelHolidays == null)
        {
        	return ResponseEntity.badRequest().body("Failed to load the data !");
        }
        
        Map<String, CountryCalendars> excelMap = toMap(excelHolidays);
        Map<String, CountryCalendars> dbMap = toMap(dbHolidays);

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(excelMap.keySet());
        allKeys.addAll(dbMap.keySet());

        int added = 0, updated = 0, deleted = 0;

        for (String key : allKeys) {
            CountryCalendars excelHoliday = excelMap.get(key);
            CountryCalendars dbHoliday = dbMap.get(key);

            if (excelHoliday != null && dbHoliday == null) {
            	countryCalendarsRepository.save(excelHoliday);
                added++;
            } else if (excelHoliday != null && dbHoliday != null) {
                if (!isSame(excelHoliday, dbHoliday)) {
                    dbHoliday.setCalendarYear(excelHoliday.getCalendarYear());
                    dbHoliday.setHolidayDay(excelHoliday.getHolidayDay());
                    dbHoliday.setHolidayDate(excelHoliday.getHolidayDate());
                    dbHoliday.setHolidayName(excelHoliday.getHolidayName());
                    countryCalendarsRepository.save(dbHoliday);
                    updated++;
                }
            } else if (excelHoliday == null && dbHoliday != null) {
            	countryCalendarsRepository.delete(dbHoliday);
                deleted++;
            }
        }
		return ResponseEntity.ok("Sync Complete | Added: "+ added+" | Updated: "+updated +" Deleted: "+ deleted);
		
    }

       

    private boolean isSame(CountryCalendars a, CountryCalendars b) {
        return a.getCountryName().equalsIgnoreCase(b.getCountryName())
                && a.getHolidayDate().equals(b.getHolidayDate())
                && a.getHolidayName().equalsIgnoreCase(b.getHolidayName());
    }

    private Map<String, CountryCalendars> toMap(List<CountryCalendars> list) {
        Map<String, CountryCalendars> map = new HashMap<>();
        for (CountryCalendars c : list) {
            String key = (c.getCountryName() + "|" + c.getHolidayDate() + "|" + c.getHolidayName()).toLowerCase();
            map.put(key, c);
        }
        return map;
    }

    public List<CountryCalendars> fetchHolidaysFromExcel(String fileUrl) {
        List<CountryCalendars> holidays = new ArrayList<>();

        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustAllStrategy())
                    .build();

            var sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(sslContext)
                    .build();

            HttpClientConnectionManager connectionManager =
                    PoolingHttpClientConnectionManagerBuilder.create()
                            .setSSLSocketFactory(sslSocketFactory)
                            .build();

            try (CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build()) {

                HttpGet request = new HttpGet(fileUrl);

                try (CloseableHttpResponse response = httpClient.execute(request);
                     InputStream inputStream = response.getEntity().getContent();
                     Workbook workbook = new XSSFWorkbook(inputStream)) {

                    Sheet sheet = workbook.getSheetAt(0);
                    boolean isHeader = true;
                    DateTimeFormatter[] formatters = {
                            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    };

                    for (Row row : sheet) {
                        if (isHeader) {
                            isHeader = false;
                            continue;
                        }

                        String countryName = getCellValueAsString(row.getCell(1));
                        String holidayName = getCellValueAsString(row.getCell(2));
                        String dateValue = getCellValueAsString(row.getCell(3));

                        if (countryName == null || holidayName == null || dateValue == null) continue;

                        LocalDate holidayDate = parseDate(dateValue, formatters);
                        if (holidayDate == null) continue;

                        CountryCalendars holiday = new CountryCalendars();
                        holiday.setCountryName(countryName.trim());
                        holiday.setHolidayName(holidayName.trim());
                        holiday.setHolidayDate(holidayDate);
                        holiday.setCalendarYear(holidayDate.getYear());
                        holiday.setHolidayDay(holidayDate.getDayOfWeek());

                        holidays.add(holiday);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading Excel: " + e.getMessage());
        }

        return holidays;
    }

    private LocalDate parseDate(String dateValue, DateTimeFormatter[] formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateValue.trim(), formatter);
            } catch (Exception ignored) {}
        }

        try {
            double numeric = Double.parseDouble(dateValue);
            return LocalDate.of(1899, 12, 30).plusDays((long) numeric);
        } catch (Exception ignored) {}
        return null;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    
    
    
    /*
     * 
     */
    
    @Scheduled(cron ="0 0 */1 * * *")
    public String autoApprove() {
        List<LeaveApplicationForm> listOfLeaveApplicationForm = leaveApplicationFormRepository.findAll();
        LocalDate currentDate = LocalDate.now();
        long autoApproveDurationInDays = 1;

        for (LeaveApplicationForm singleLeaveApplicationForm : listOfLeaveApplicationForm) {
            LocalDate appliedDate = singleLeaveApplicationForm.getAppliedDate();
            if (appliedDate == null) continue;

            long timeDiff = ChronoUnit.DAYS.between(appliedDate, currentDate);

            if (singleLeaveApplicationForm.getLeaveStatus().equals(LeaveStatus.PENDING) &&
                timeDiff >= autoApproveDurationInDays) {

                System.out.println("Auto-approving leave with timeDiff: " + timeDiff);

                UsersLeaveBalance singleUsersLeaveBalance = usersLeaveBalanceRepository
                    .findByUser_UserId(singleLeaveApplicationForm.getUserId());

                float duration = singleLeaveApplicationForm.getDuration();
                String leaveType = singleLeaveApplicationForm.getLeaveType().toUpperCase();

                switch (leaveType) {
                    case "SICK":
                        singleUsersLeaveBalance.setSickLeave(singleUsersLeaveBalance.getSickLeave() - duration);
                        break;
                    case "CASUAL":
                        singleUsersLeaveBalance.setCasualLeave(singleUsersLeaveBalance.getCasualLeave() - duration);
                        break;
                    case "PATERNITY":
                        singleUsersLeaveBalance.setPaternityLeave(singleUsersLeaveBalance.getPaternityLeave() - duration);
                        break;
                    case "MATERNITY":
                        singleUsersLeaveBalance.setMaternityLeave(singleUsersLeaveBalance.getMaternityLeave() - duration);
                        break;
                    case "EARNED":
                        singleUsersLeaveBalance.setEarnedLeave(singleUsersLeaveBalance.getEarnedLeave() - duration);
                        break;
                    default:
                        return "Invalid leave type.";
                }

                singleUsersLeaveBalance.setTotalLeaves(singleUsersLeaveBalance.getTotalLeaves() - duration);
                singleLeaveApplicationForm.setLeaveStatus(LeaveStatus.APPROVED);

                usersLeaveBalanceRepository.save(singleUsersLeaveBalance);
                leaveApplicationFormRepository.save(singleLeaveApplicationForm);
            }
        }
        return "Successfully Auto Approved";
    }

}
