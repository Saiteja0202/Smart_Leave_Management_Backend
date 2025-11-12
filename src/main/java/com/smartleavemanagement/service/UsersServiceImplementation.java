package com.smartleavemanagement.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smartleavemanagement.DTOs.HolidayCalendar;
import com.smartleavemanagement.DTOs.LoginResponse;
import com.smartleavemanagement.DTOs.UserLeaveBalancedays;
import com.smartleavemanagement.enums.OtpStatus;
import com.smartleavemanagement.model.CountryCalendars;
import com.smartleavemanagement.model.LeaveApplicationForm;
import com.smartleavemanagement.model.RegistrationHistory;
import com.smartleavemanagement.model.RoleBasedLeaves;
import com.smartleavemanagement.model.Roles;
import com.smartleavemanagement.model.Users;
import com.smartleavemanagement.model.UsersLeaveBalance;
import com.smartleavemanagement.repository.CountryCalendarsRepository;
import com.smartleavemanagement.repository.LeaveApplicationFormRepository;
import com.smartleavemanagement.repository.RegistrationHistoryRepository;
import com.smartleavemanagement.repository.RoleBasedLeavesRepository;
import com.smartleavemanagement.repository.RolesRepository;
import com.smartleavemanagement.repository.UsersLeaveBalanceRepository;
import com.smartleavemanagement.repository.UsersRepository;
import com.smartleavemanagement.securityconfiguration.JwtUtil;

@Service
public class UsersServiceImplementation implements UsersService {

	private final UsersRepository usersRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	private final RolesRepository rolesRepository;

	private final JavaMailSender mailSender;

	private final RegistrationHistoryRepository registrationHistoryRepository;

	private final CountryCalendarsRepository countryCalendarsRepository;

	private final LeaveApplicationFormRepository leaveApplicationFormRepository;

	private final UsersLeaveBalanceRepository usersLeaveBalanceRepository;

	private final RoleBasedLeavesRepository roleBasedLeavesRepository;

	public UsersServiceImplementation(UsersRepository usersRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
			RolesRepository rolesRepository, JavaMailSender mailSender,
			RegistrationHistoryRepository registrationHistoryRepository,
			CountryCalendarsRepository countryCalendarsRepository,
			UsersLeaveBalanceRepository usersLeaveBalanceRepository,
			RoleBasedLeavesRepository roleBasedLeavesRepository,
			LeaveApplicationFormRepository leaveApplicationFormRepository) {
		this.usersRepository = usersRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.rolesRepository = rolesRepository;
		this.mailSender = mailSender;
		this.registrationHistoryRepository = registrationHistoryRepository;
		this.countryCalendarsRepository = countryCalendarsRepository;
		this.usersLeaveBalanceRepository = usersLeaveBalanceRepository;
		this.roleBasedLeavesRepository = roleBasedLeavesRepository;
		this.leaveApplicationFormRepository = leaveApplicationFormRepository;
	}

	@Override
	public ResponseEntity<String> registerUser(Users user) {
		if (user.getRole() != null && user.getRole().getRoleName() != null
				&& user.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {

			return ResponseEntity.badRequest().body("Admin must register via /admin/registration");
		}

		if (usersRepository.existsByUserName(user.getUserName())) {
			return ResponseEntity.badRequest().body("Username already exists");
		}

		if (usersRepository.existsByEmail(user.getEmail())) {
			return ResponseEntity.badRequest().body("Email already exists");
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setOtp(0);
		user.setOtpStatus(OtpStatus.GENERATE);

		Roles assignedRole = user.getRole();
		if (assignedRole == null || assignedRole.getRoleName() == null) {
			assignedRole = rolesRepository.findByRoleNameIgnoreCase("TEAM_MEMBER").orElse(null);
			if (assignedRole == null) {
				return ResponseEntity.status(400).body("Default role not found");
			}
		}

		List<CountryCalendars> calendars = countryCalendarsRepository.findByCountryName(user.getCountryName());

		if (calendars.isEmpty()) {
			return ResponseEntity.status(400).body("Country calendar not found");
		}

		CountryCalendars assignedCountryCalendar = calendars.get(0);
		user.setCountryName(assignedCountryCalendar.getCountryName());

		user.setRole(assignedRole);
		user.setUserRole(assignedRole.getRoleName());

		usersRepository.save(user);
		RegistrationHistory registrationHistory = new RegistrationHistory();
		registrationHistory.setFirstName(user.getFirstName());
		registrationHistory.setLastName(user.getLastName());
		registrationHistory.setEmail(user.getEmail());
		registrationHistory.setUserId(user.getUserId());
		registrationHistory.setRegisterDate(LocalDateTime.now());
		registrationHistory.setRole(user.getUserRole());

		registrationHistoryRepository.save(registrationHistory);
		addRoleBasedLeavesToUsers(user.getUserId(), user.getUserRole());
		return ResponseEntity.ok("User registered successfully");
	}

	@Override
	public ResponseEntity<?> login(String username, String password) {
		Users user = usersRepository.findByUserName(username).orElse(null);
		if (user == null) {
			return ResponseEntity.status(404).body("User not found");
		}

		if (!passwordEncoder.matches(password, user.getPassword())) {
			return ResponseEntity.status(400).body("Invalid credentials");
		}

		String token = jwtUtil.generateToken(user.getUserName(), (long) user.getUserId(), user.getRole().getRoleName());

		LoginResponse response = new LoginResponse(user.getUserId(), user.getRole().getRoleName(), user.getEmail(),
				token);

		return ResponseEntity.ok(response);

	}

	@Override
	public ResponseEntity<String> updateUserDetails(int userId, Users updatedUser, String token) {
		if (!jwtUtil.validateToken(token)) {
			return ResponseEntity.status(401).body("Invalid or expired token");
		}

		String usernameFromToken = jwtUtil.extractUsername(token);
		Users existingUser = usersRepository.findById(userId).orElse(null);

		if (existingUser == null) {
			return ResponseEntity.status(404).body("User not found");
		}

		if (!existingUser.getUserName().equals(usernameFromToken)) {
			return ResponseEntity.status(403).body("You are not authorized to update this user's details");
		}

		existingUser.setFirstName(updatedUser.getFirstName());
		existingUser.setLastName(updatedUser.getLastName());
		existingUser.setEmail(updatedUser.getEmail());
		existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
		existingUser.setAddress(updatedUser.getAddress());
		existingUser.setGender(updatedUser.getGender());
		existingUser.setCountryName(updatedUser.getCountryName());

		usersRepository.save(existingUser);
		return ResponseEntity.ok("User details updated successfully");
	}

	@Override
	public ResponseEntity<String> generateOtp(String email, String context) {

		Optional<Users> optionalUser = usersRepository.findByEmail(email);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(404).body("User not found");
		}
		Users user = optionalUser.get();

		int otp = (int) (Math.random() * 9000) + 1000;

		user.setOtp(otp);
		user.setOtpStatus(OtpStatus.PENDING);
		usersRepository.save(user);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Your OTP for " + context + " recovery");
		message.setText("Dear " + user.getFirstName() + " " + user.getLastName() + ",\n\nYour OTP is: " + otp
				+ "\n\nRegards,\nSmart Leave Management Team");

		mailSender.send(message);

		return ResponseEntity.ok("OTP generated and sent to email");
	}

	@Override
	public ResponseEntity<String> verifyOtp(int otp, String context) {
		Users user = usersRepository.findByOtp(otp).orElse(null);

		if (user == null) {
			return ResponseEntity.badRequest().body("Wrong OTP");
		}

		if (user.getOtpStatus() != OtpStatus.PENDING) {
			user.setOtpStatus(OtpStatus.EXPIRED);
			usersRepository.save(user);
			return ResponseEntity.badRequest().body("OTP expired or already used");
		}

		user.setOtpStatus(OtpStatus.VERIFIED);
		usersRepository.save(user);

		if (context.equalsIgnoreCase("username")) {
			return ResponseEntity.ok("Username: " + user.getUserName());
		} else {
			return ResponseEntity.ok("UserId : " + user.getUserId());
		}
	}

	@Override
	public ResponseEntity<String> updatePassword(int userId, String oldPassword, String newPassword, String token) {
		if (!jwtUtil.validateToken(token)) {
			return ResponseEntity.status(401).body("Invalid or expired token");
		}

		Long tokenUserId = jwtUtil.extractUserId(token);
		if (tokenUserId == null || tokenUserId != userId) {
			return ResponseEntity.status(403).body("You are not authorized to update this user's password");
		}

		Users user = usersRepository.findById(userId).orElse(null);
		if (user == null) {
			return ResponseEntity.status(404).body("User not found");
		}

		if (user.getOtpStatus() != OtpStatus.VERIFIED) {
			return ResponseEntity.badRequest().body("OTP verification required before updating password");
		}

		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			return ResponseEntity.badRequest().body("Old password does not match");
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		user.setOtpStatus(OtpStatus.GENERATE);
		usersRepository.save(user);

		return ResponseEntity.ok("Password updated successfully");
	}

	public ResponseEntity<List<HolidayCalendar>> getHolidays(int userId) {
		Users user = usersRepository.findById(userId).orElse(null);
		if (user == null) {
			return ResponseEntity.status(404).body(null);
		}

		List<CountryCalendars> countryCalendarsList = countryCalendarsRepository
				.findAllByCountryName(user.getCountryName());

		List<HolidayCalendar> holidaysCalendar = new ArrayList<>();
		LocalDate currentLocalDate = LocalDate.now();
		int currentYear = currentLocalDate.getYear();
	
		for (CountryCalendars cc : countryCalendarsList) {
			if(cc.getCalendarYear() == currentYear)
			{
				HolidayCalendar holidayCalendar = new HolidayCalendar();
				holidayCalendar.setCountryName(cc.getCountryName());
				holidayCalendar.setHolidayName(cc.getHolidayName());
				holidayCalendar.setHolidayDate(cc.getHolidayDate());
				holidayCalendar.setHoilydayDay(cc.getHolidayDay());
				holidaysCalendar.add(holidayCalendar);
			}
		}
		
		return ResponseEntity.ok(holidaysCalendar);
	}

	public String addRoleBasedLeavesToUsers(int userId, String roleName) {
		RoleBasedLeaves roleBasedLeaves = roleBasedLeavesRepository.findByRole(roleName).orElse(null);

		Roles role = rolesRepository.findByRoleNameIgnoreCase(roleName).orElse(null);
		if (role == null) {
			return "Role not found";
		}

		Users usersWithRole = usersRepository.findById(userId).orElse(null);

		UsersLeaveBalance balance = new UsersLeaveBalance();

		balance.setUser(usersWithRole);
		balance.setRole(role.getRoleName());
		balance.setCasualLeave(roleBasedLeaves.getCasualLeave());
		balance.setEarnedLeave(roleBasedLeaves.getEarnedLeave());
		balance.setLossOfPay(roleBasedLeaves.getLossOfPay());
		balance.setMaternityLeave(roleBasedLeaves.getMaternityLeave());
		balance.setPaternityLeave(roleBasedLeaves.getPaternityLeave());
		balance.setSickLeave(roleBasedLeaves.getSickLeave());
		float totalLeaves = roleBasedLeaves.getSickLeave() + roleBasedLeaves.getCasualLeave()
				+ roleBasedLeaves.getEarnedLeave() + roleBasedLeaves.getLossOfPay()
				+ roleBasedLeaves.getMaternityLeave() + roleBasedLeaves.getPaternityLeave();

		balance.setTotalLeaves(totalLeaves);

		usersLeaveBalanceRepository.save(balance);

		return "Successfully added leaves to user";

	}

	public ResponseEntity<List<UserLeaveBalancedays>> getUserLeaveBalance(int userId) {

		UsersLeaveBalance userLeaveBalance = usersLeaveBalanceRepository.findByUser_UserId(userId);

		UserLeaveBalancedays userLeaveBalanceDays = new UserLeaveBalancedays();
		userLeaveBalanceDays.setCasualLeave(userLeaveBalance.getCasualLeave());
		userLeaveBalanceDays.setEarnedLeave(userLeaveBalance.getEarnedLeave());
		userLeaveBalanceDays.setLossOfPay(userLeaveBalance.getLossOfPay());
		userLeaveBalanceDays.setMaternityLeave(userLeaveBalance.getMaternityLeave());
		userLeaveBalanceDays.setPaternityLeave(userLeaveBalance.getPaternityLeave());
		userLeaveBalanceDays.setSickLeave(userLeaveBalance.getSickLeave());
		userLeaveBalanceDays.setTotalLeaves(userLeaveBalance.getTotalLeaves());

		ArrayList<UserLeaveBalancedays> userLeaveBalanceList = new ArrayList<>();
		userLeaveBalanceList.add(userLeaveBalanceDays);

		return ResponseEntity.ok(userLeaveBalanceList);
	}

	@Override
	public ResponseEntity<String> updateNewPassword(int userId, String newPassword) {

		Users user = usersRepository.findById(userId).orElse(null);
		if (user == null) {
			return ResponseEntity.status(404).body("User not found");
		}

		if (user.getOtpStatus() != OtpStatus.VERIFIED) {
			return ResponseEntity.badRequest().body("OTP verification required before updating password");
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		user.setOtpStatus(OtpStatus.GENERATE);
		usersRepository.save(user);

		return ResponseEntity.ok("Password updated successfully");
	}

	public ResponseEntity<String> deleteAccount(int userId, String token) {

		if (!jwtUtil.validateToken(token)) {
			return ResponseEntity.status(401).body("Invalid or expired token");
		}

		Long tokenUserId = jwtUtil.extractUserId(token);
		if (tokenUserId == null || tokenUserId != userId) {
			return ResponseEntity.status(403).body("You are not authorized to Delete User!");
		}

		Users user = usersRepository.findById(userId).orElse(null);
		UsersLeaveBalance usersLeaveBalance = usersLeaveBalanceRepository.findByUser_UserId(userId);
		List<LeaveApplicationForm> leaveApplicationForm = leaveApplicationFormRepository.findByUserId(userId);

		if (user == null || usersLeaveBalance == null || leaveApplicationForm == null) {
			return ResponseEntity.badRequest().body("User Not Found");
		}

		for (LeaveApplicationForm newLeaveApplicationForm : leaveApplicationForm) {
			leaveApplicationFormRepository.delete(newLeaveApplicationForm);
		}

		usersLeaveBalanceRepository.delete(usersLeaveBalance);
		usersRepository.delete(user);

		return ResponseEntity.ok("Successfully Deleted Account");
	}

	public ResponseEntity<List<String>> getAllCountriesForUsers() {

		List<CountryCalendars> allCountryCalendars = countryCalendarsRepository.findAll();
		Set<String> countries = new HashSet<String>();
		for (CountryCalendars countryCalendars : allCountryCalendars) {

			countries.add(countryCalendars.getCountryName());
		}
		ArrayList<String> allCountries = new ArrayList<>(countries);
		return ResponseEntity.ok(allCountries);
	}
	
	
	public ResponseEntity<List<String>> getAllCities(String countryName){
		
		List<CountryCalendars> allCountryCalendars = countryCalendarsRepository.findAll();
		Set<String> cities = new HashSet<String>();
		for (CountryCalendars countryCalendars : allCountryCalendars) {

			if(countryCalendars.getCountryName().equals(countryName))
			{
				cities.add(countryCalendars.getCityName());
			}
		}
		ArrayList<String> allCities = new ArrayList<>(cities);
		return ResponseEntity.ok(allCities);
	}

}
