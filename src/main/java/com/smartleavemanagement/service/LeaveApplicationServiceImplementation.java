package com.smartleavemanagement.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.query.NativeQuery.ReturnableResultNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.smartleavemanagement.DTOs.LeaveRequests;
import com.smartleavemanagement.DTOs.LeaveStartAndEndDates;
import com.smartleavemanagement.enums.LeaveStatus;
import com.smartleavemanagement.exceptions.InvalidLeaveDates;
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
		 
		 
		 
		 List<LeaveApplicationForm> newLeaveApplicationForms = leaveApplicationFormRepository.findByUserId(userId); 
		 for(LeaveApplicationForm newLeaveApplicationForm : newLeaveApplicationForms)
		 {
			 long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
			 
			 for(int i=0;i<daysBetween;i++)
			 {
				 LocalDate date = startDate.plusDays(i);
				 if((newLeaveApplicationForm.getStartDate() == date) || (newLeaveApplicationForm.getEndDate()==date))
				 {
					 return ResponseEntity.badRequest().body("You cannot apply leave on same date that previously applied");
				 }
			 }
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
		
		float duration = getWeekdaysBetween(startDate,endDate);
		return ResponseEntity.ok(duration);
	}
	
	
	
	
	 public static float getWeekdaysBetween(LocalDate startDate, LocalDate endDate) {
	        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
	        float weekdays = 0;

	        for (int i = 0; i < daysBetween; i++) {
	            DayOfWeek day = startDate.plusDays(i).getDayOfWeek();
	            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
	                weekdays++;
	            }
	        }

	        return weekdays;
	    }
	
	public float calculateDuartionDays(int userId,LocalDate startDate,LocalDate endDate) throws InvalidLeaveDates
	{
		
		Users user = usersRepository.findById(userId).orElse(null);
		
		LocalDate currentDate = LocalDate.now();
		
		
		DayOfWeek startDay = startDate.getDayOfWeek();
		DayOfWeek endDay = endDate.getDayOfWeek();
		
		
		 if (startDate == null || endDate == null) {
			 	throw new InvalidLeaveDates("Start date or end date is missing.");
		       
		    }
		
		if(startDate.isBefore(currentDate) || endDate.isBefore(currentDate))
		{
			throw new InvalidLeaveDates("Select the valid dates.");
			
		}
		 
		if(startDay == DayOfWeek.SATURDAY || startDay == DayOfWeek.SUNDAY)
		{
			throw new InvalidLeaveDates("Start day should not start with weekend days !");
		}
		if(endDay == DayOfWeek.SATURDAY || endDay == DayOfWeek.SUNDAY)
		{
			throw new InvalidLeaveDates("End day should not start with weekend days !");
		}
		
		if(endDate.isBefore(startDate))
		{
			throw new InvalidLeaveDates("The End Date you selected is before the Start Date. Please enter an End Date that is on or after the Start Date.");
		}
		
		List<CountryCalendars> allHolidays = countryCalendarsRepository.findAllByCountryName(user.getCountryName());
		
		for(CountryCalendars holiday : allHolidays)
		{
			if(holiday.getHolidayDate() == startDate)
			{
				throw new InvalidLeaveDates("Start day should not start with Holiday !");
			}
			else if(holiday.getHolidayDate() == endDate)
			{
				throw new InvalidLeaveDates("End day should not end with Holiday !");
			}
			
		}
		return getWeekdaysBetween(startDate,endDate);
	}
	
	public ResponseEntity<String> applyLeave(int userId, LeaveApplicationForm leaveApplicationForm) {
	    try {
	        Users user = userRepository.findById(userId).orElse(null);
	        if (user == null) {
	            return ResponseEntity.badRequest().body("User not found.");
	        }

	        UsersLeaveBalance leaveBalance = usersLeaveBalanceRepository.findByUser_UserId(userId);
	        if (leaveBalance == null) {
	            return ResponseEntity.badRequest().body("Leave balance not found for user.");
	        }

	        String leaveType = leaveApplicationForm.getLeaveType();
	        float duration = calculateDuartionDays(userId, leaveApplicationForm.getStartDate(), leaveApplicationForm.getEndDate());

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
	        newLeaveApplicationForm.setAppliedDate(LocalDate.now());

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
	    } catch (InvalidLeaveDates e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
	    }
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
	
	
	
	public ResponseEntity<?> getLeaveRequests(int userId)
	{
		
		Users user = userRepository.findById(userId).orElse(null);
		
		List<LeaveApplicationForm> userLeaveRequests = leaveApplicationFormRepository.findByUserId(userId);
		
		if(userLeaveRequests == null)
		{
			return ResponseEntity.badRequest().body("Leave requests are Not Found !");
		}
		ArrayList<LeaveRequests> newUserLeaveRequests = new ArrayList<>();
		for(LeaveApplicationForm userLeaveRequestsList : userLeaveRequests)
		{
			
			LeaveRequests newLeaveRequests = new LeaveRequests();

			newLeaveRequests.setLeaveType(userLeaveRequestsList.getLeaveType());
			newLeaveRequests.setStartDate(userLeaveRequestsList.getStartDate());
			newLeaveRequests.setEndDate(userLeaveRequestsList.getEndDate());
			newLeaveRequests.setDuration(userLeaveRequestsList.getDuration());
			newLeaveRequests.setApprover(userLeaveRequestsList.getApprover());
			newLeaveRequests.setLeaveStatus(userLeaveRequestsList.getLeaveStatus());
			newLeaveRequests.setUserName(user.getUserName());
			newLeaveRequests.setLeaveId(userLeaveRequestsList.getLeaveId());
			newLeaveRequests.setUserId(userLeaveRequestsList.getUserId());
			newLeaveRequests.setUserRole(userLeaveRequestsList.getRoleName());
			newUserLeaveRequests.add(newLeaveRequests);
		}
		
		return ResponseEntity.ok(newUserLeaveRequests);
	}
	
	public ResponseEntity<?> getAllLeaveRequests(int userId)
	{
		return ResponseEntity.ok(get(userId));
	}
	

	
	

	public Object get(int userId) {
	    Users user = usersRepository.findById(userId).orElse(null);
	    if (user == null) {
	        return "User not found!";
	    }

	    List<LeaveApplicationForm> allUsersLeaveRequests = leaveApplicationFormRepository.findAll();
	    if (allUsersLeaveRequests == null || allUsersLeaveRequests.isEmpty()) {
	        return "Leave Requests are Not Found!";
	    }

	    Map<String, List<String>> roleViewMap = Map.of(
	        "HR_MANAGER", List.of("TEAM_MANAGER", "TEAM_LEAD", "TEAM_MEMBER"),
	        "TEAM_MANAGER", List.of("TEAM_LEAD", "TEAM_MEMBER")
	    );

	    List<String> viewableRoles = roleViewMap.getOrDefault(user.getUserRole(), List.of());

	    List<LeaveRequests> filteredRequests = allUsersLeaveRequests.stream()
	        .filter(form -> viewableRoles.contains(form.getRoleName()))
	        .map(form -> mapToLeaveRequest(form, user.getUserName()))
	        .collect(Collectors.toList());

	    return filteredRequests;
	}

	private LeaveRequests mapToLeaveRequest(LeaveApplicationForm form, String userName) {
	    LeaveRequests request = new LeaveRequests();
	    request.setUserName(userName);
	    request.setLeaveId(form.getLeaveId());
	    request.setUserId(form.getUserId());
	    request.setUserRole(form.getRoleName());
	    request.setLeaveType(form.getLeaveType());
	    request.setStartDate(form.getStartDate());
	    request.setEndDate(form.getEndDate());
	    request.setDuration(form.getDuration());
	    request.setApprover(form.getApprover());
	    request.setLeaveStatus(form.getLeaveStatus());
	    return request;
	}
	
	
	public ResponseEntity<String> approveLeaveRequest(int userId,int requesterId)
	{
		List<LeaveRequests> newUserLeaveRequests = (List<LeaveRequests>) get(userId);
		for(LeaveRequests singleNewUserLeaveRequests:newUserLeaveRequests)
		{
			if(singleNewUserLeaveRequests.getLeaveStatus().equals(LeaveStatus.PENDING))
			{
				
				List<LeaveApplicationForm> newLeaveApplicationForm = leaveApplicationFormRepository.findByUserId(requesterId);
				for(LeaveApplicationForm singleNewLeaveApplicationForm :newLeaveApplicationForm)
				{
					UsersLeaveBalance newUserLeaveBalance = usersLeaveBalanceRepository.findById(singleNewLeaveApplicationForm.getUserId()).orElse(null);
					singleNewLeaveApplicationForm.setLeaveStatus(LeaveStatus.APPROVED);
					float duration = singleNewLeaveApplicationForm.getDuration();
					String leaveType = singleNewLeaveApplicationForm.getLeaveType();
					float leaveBalanceToDeduct = 0.0f;
					switch (leaveType.toUpperCase())
					{
					case "SICK":
						leaveBalanceToDeduct = newUserLeaveBalance.getSickLeave();
						newUserLeaveBalance.setSickLeave(leaveBalanceToDeduct-duration);
						newUserLeaveBalance.setTotalLeaves(duration);
						break;
					case "CASUAL":
						leaveBalanceToDeduct = newUserLeaveBalance.getCasualLeave();
						newUserLeaveBalance.setCasualLeave(leaveBalanceToDeduct-duration);
						newUserLeaveBalance.setTotalLeaves(duration);
						break;
					case "PATERNITY":
						leaveBalanceToDeduct = newUserLeaveBalance.getPaternityLeave();
						newUserLeaveBalance.setPaternityLeave(leaveBalanceToDeduct-duration);
						newUserLeaveBalance.setTotalLeaves(duration);
						break;
					case "MATERNITY":
						leaveBalanceToDeduct = newUserLeaveBalance.getMaternityLeave();
						newUserLeaveBalance.setMaternityLeave(leaveBalanceToDeduct-duration);
						newUserLeaveBalance.setTotalLeaves(duration);
						break;
					case "EARNED":
						leaveBalanceToDeduct = newUserLeaveBalance.getEarnedLeave();
						newUserLeaveBalance.setEarnedLeave(leaveBalanceToDeduct-duration);
						newUserLeaveBalance.setTotalLeaves(duration);
						break;
					 default:
			                return ResponseEntity.badRequest().body("Invalid leave type.");
					}
					usersLeaveBalanceRepository.save(newUserLeaveBalance);
					leaveApplicationFormRepository.save(singleNewLeaveApplicationForm);
				}
			}
		}
		return ResponseEntity.ok("Successfully Approved");
	}
	public ResponseEntity<String> rejectLeaveRequest(int userId,int requesterId)
	{
		List<LeaveRequests> newUserLeaveRequests = (List<LeaveRequests>) get(userId);
		for(LeaveRequests singleNewUserLeaveRequests:newUserLeaveRequests)
		{
			if(singleNewUserLeaveRequests.getLeaveStatus().equals(LeaveStatus.PENDING))
			{
				
				List<LeaveApplicationForm> newLeaveApplicationForm = leaveApplicationFormRepository.findByUserId(requesterId);
				for(LeaveApplicationForm singleNewLeaveApplicationForm :newLeaveApplicationForm)
				{
					singleNewLeaveApplicationForm.setLeaveStatus(LeaveStatus.REJECTED);
					leaveApplicationFormRepository.save(singleNewLeaveApplicationForm);
				}
			}
		}
		return ResponseEntity.ok("Successfully Rejected");
	}


}
