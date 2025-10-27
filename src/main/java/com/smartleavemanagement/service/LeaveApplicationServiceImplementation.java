package com.smartleavemanagement.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.smartleavemanagement.DTOs.LeaveStartAndEndDates;
import com.smartleavemanagement.enums.LeaveStatus;
import com.smartleavemanagement.model.CountryCalendars;
import com.smartleavemanagement.model.LeaveApplicationForm;
import com.smartleavemanagement.model.RoleBasedLeaves;
import com.smartleavemanagement.model.Users;
import com.smartleavemanagement.model.UsersLeaveBalance;
import com.smartleavemanagement.repository.CountryCalendarsRepository;
import com.smartleavemanagement.repository.LeaveApplicationFormRepository;
import com.smartleavemanagement.repository.RoleBasedLeavesRepository;
import com.smartleavemanagement.repository.UsersLeaveBalanceRepository;
import com.smartleavemanagement.repository.UsersRepository;

@Service
public class LeaveApplicationServiceImplementation implements LeaveApplicationService {
	
	private final UsersRepository usersRepository;
	
	private final CountryCalendarsRepository countryCalendarsRepository;
	
	private final UsersRepository userRepository;
	
	private final UsersLeaveBalanceRepository usersLeaveBalanceRepository;
	
	private final LeaveApplicationFormRepository leaveApplicationFormRepository;
	
	private final RoleBasedLeavesRepository roleBasedLeavesRepository;
	
	public LeaveApplicationServiceImplementation(UsersRepository usersRepository,CountryCalendarsRepository countryCalendarsRepository
			,UsersRepository userRepository,UsersLeaveBalanceRepository usersLeaveBalanceRepository,
			LeaveApplicationFormRepository leaveApplicationFormRepository,
			RoleBasedLeavesRepository roleBasedLeavesRepository)
	{
		this.usersRepository=usersRepository;
		this.countryCalendarsRepository=countryCalendarsRepository;
		this.userRepository=userRepository;
		this.usersLeaveBalanceRepository=usersLeaveBalanceRepository;
		this.leaveApplicationFormRepository=leaveApplicationFormRepository;
		this.roleBasedLeavesRepository=roleBasedLeavesRepository;
	}
	
	public ResponseEntity<?> calculateDuration(int userId,LocalDate startDate, LocalDate endDate)
	{
		Users user = usersRepository.findById(userId).orElse(null);
		
		LocalDate currentDate = LocalDate.now();
		
		
		DayOfWeek startDay = startDate.getDayOfWeek();
		DayOfWeek endDay = endDate.getDayOfWeek();
		
		
		 if (startDate == null || endDate == null) {
		        return ResponseEntity.badRequest().body("Start date or end date is missing.");
		    }
		
		if(startDate.isBefore(currentDate) || endDate.isBefore(currentDate))
		{
			return ResponseEntity.badRequest().body("Select the valid dates");
		}
		 
		if(startDay == DayOfWeek.SATURDAY || startDay == DayOfWeek.SUNDAY)
		{
			return ResponseEntity.badRequest().body("Start day should not start with weekend days !");
		}
		if(endDay == DayOfWeek.SATURDAY || endDay == DayOfWeek.SUNDAY)
		{
			return ResponseEntity.badRequest().body("End day should not start with weekend days !");
		}
		
		if(endDate.isBefore(startDate))
		{
			return ResponseEntity.badRequest().body("The End Date you selected is before the Start Date. Please enter an End Date that is on or after the Start Date.");
		}
		
		List<CountryCalendars> allHolidays = countryCalendarsRepository.findAllByCountryName(user.getCountryName());
		
		for(CountryCalendars holiday : allHolidays)
		{
			if(holiday.getHolidayDate() == startDate)
			{
				return ResponseEntity.badRequest().body("Start day should not start with Holiday !");
			}
			else if(holiday.getHolidayDate() == endDate)
			{
				return ResponseEntity.badRequest().body("End day should not end with Holiday !");
			}
			
		}
		
		float duration = ChronoUnit.DAYS.between(startDate, endDate)+1;
		return ResponseEntity.ok(duration);
	}
	
	public float calculateDuartionDays(LocalDate startDate,LocalDate endDate)
	{
		return ChronoUnit.DAYS.between(startDate, endDate)+1;
	}
	
	public ResponseEntity<String> applyLeave(int userId, LeaveApplicationForm leaveApplicationForm) {
	    Users user = userRepository.findById(userId).orElse(null);
	    if (user == null) {
	        return ResponseEntity.badRequest().body("User not found.");
	    }

	    UsersLeaveBalance leaveBalance = usersLeaveBalanceRepository.findByUser_UserId(userId);
	    if (leaveBalance == null) {
	        return ResponseEntity.badRequest().body("Leave balance not found for user.");
	    }

	    String leaveType = leaveApplicationForm.getLeaveType();
	    float duration = calculateDuartionDays(leaveApplicationForm.getStartDate(), leaveApplicationForm.getEndDate());

	    boolean isLeaveAllowed = false;

	    switch (leaveType.toUpperCase()) {
	        case "SICK":
	            isLeaveAllowed = leaveBalance.getSickLeave() >= duration;
	            break;
	        case "CASUAL":
	            isLeaveAllowed = leaveBalance.getCasualLeave() >= duration;
	            break;
	        case "PATERNITY":
	            isLeaveAllowed = leaveBalance.getPaternityLeave() >= duration;
	            break;
	        case "MATERNITY":
	            isLeaveAllowed = leaveBalance.getMaternityLeave() >= duration;
	            break;
	        case "LOSS_OF_PAY":
	            isLeaveAllowed = leaveBalance.getLossOfPay() >= duration;
	            break;
	        case "EARNED":
	            isLeaveAllowed = leaveBalance.getEarnedLeave() >= duration;
	            break;
	        default:
	            return ResponseEntity.badRequest().body("Invalid leave type.");
	    }

	    if (!isLeaveAllowed) {
	        return ResponseEntity.badRequest().body("Insufficient leave balance for " + leaveType + " leave.");
	    }

	    LeaveApplicationForm newLeaveApplicationForm = new LeaveApplicationForm();
	    newLeaveApplicationForm.setStartDate(leaveApplicationForm.getStartDate());
	    newLeaveApplicationForm.setEndDate(leaveApplicationForm.getEndDate());
	    newLeaveApplicationForm.setComments(leaveApplicationForm.getComments());
	    newLeaveApplicationForm.setLeaveStatus(LeaveStatus.PENDING);
	    newLeaveApplicationForm.setDuration(duration);
	    newLeaveApplicationForm.setLeaveType(leaveType);
	    newLeaveApplicationForm.setRoleName(user.getUserRole());
	    newLeaveApplicationForm.setUserId(userId);

	
	    String approverRole;
	    switch (user.getUserRole()) {
	        case "TEAM_MEMBER":
	        case "TEAM_LEAD":
	            approverRole = "TEAM_MANAGER";
	            break;
	        case "TEAM_MANAGER":
	            approverRole = "HR_MANAGER";
	            break;
	        case "HR_MANAGER":
	            approverRole = "ADMIN";
	            break;
	        default:
	            approverRole = "ADMIN"; 
	    }

	    newLeaveApplicationForm.setApprover(approverRole);

	    
	    leaveApplicationFormRepository.save(newLeaveApplicationForm);

	    return ResponseEntity.ok("Successfully applied for leave, waiting for approval!");
	}

	
	
	
	@Scheduled(cron = "0 0 0 31 12 *")
	    public void resetLeaveBalancesForNewYear() {
	        List<UsersLeaveBalance> allUserBalances = usersLeaveBalanceRepository.findAll();

	        for (UsersLeaveBalance userBalance : allUserBalances) {
	            String role = userBalance.getUser().getUserRole();
	            RoleBasedLeaves defaultPolicy = roleBasedLeavesRepository.findByRole(role).orElse(null);

	            if (defaultPolicy != null) {
	                float carryForwardEarned = userBalance.getEarnedLeave();

	                userBalance.setSickLeave(defaultPolicy.getSickLeave());
	                userBalance.setCasualLeave(defaultPolicy.getCasualLeave());
	                userBalance.setPaternityLeave(defaultPolicy.getPaternityLeave());
	                userBalance.setMaternityLeave(defaultPolicy.getMaternityLeave());
	                userBalance.setLossOfPay(defaultPolicy.getLossOfPay());
	                userBalance.setEarnedLeave(carryForwardEarned + defaultPolicy.getEarnedLeave());
	                float totalLeaves = defaultPolicy.getSickLeave()+defaultPolicy.getCasualLeave()+
	                		defaultPolicy.getPaternityLeave()+defaultPolicy.getMaternityLeave()+
	                		defaultPolicy.getLossOfPay()+(carryForwardEarned + defaultPolicy.getEarnedLeave());
	                userBalance.setTotalLeaves(totalLeaves);
	                usersLeaveBalanceRepository.save(userBalance);
	            }
	        }

	        System.out.println("Leave balances reset for the new year.");
	    }

}
