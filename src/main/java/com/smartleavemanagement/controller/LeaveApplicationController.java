package com.smartleavemanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.smartleavemanagement.DTOs.LeaveRequests;
import com.smartleavemanagement.DTOs.LeaveStartAndEndDates;
import com.smartleavemanagement.exceptions.InvalidLeaveDates;
import com.smartleavemanagement.model.LeaveApplicationForm;
import com.smartleavemanagement.service.LeaveApplicationService;

@RestController
@RequestMapping("/users")
public class LeaveApplicationController {
	
	
	private final LeaveApplicationService leaveApplicationService;
	
	public LeaveApplicationController(LeaveApplicationService leaveApplicationService)
	{
		this.leaveApplicationService=leaveApplicationService;
	}
	

	@PostMapping("/calculate-duration/{userId}")
	public ResponseEntity<?> calculateDuration(@PathVariable int userId,@RequestBody LeaveStartAndEndDates leaveStartAndEndDates)
	
	{
		return leaveApplicationService.calculateDuration(userId, leaveStartAndEndDates.getStartDate(),leaveStartAndEndDates.getEndDate());
	}
	
	
	@PostMapping("/apply-leave/{userId}")
	public ResponseEntity<String> applyLeave(@PathVariable int userId,@RequestBody LeaveApplicationForm leaveApplicationForm) throws InvalidLeaveDates
	{
		return leaveApplicationService.applyLeave(userId,leaveApplicationForm);
	}
	
	@GetMapping("/get-leave-requests/{userId}")
	public ResponseEntity<?> getLeaveRequests(@PathVariable int userId)
	{
		return leaveApplicationService.getLeaveRequests(userId);
	}
	
	
	@GetMapping("/get-all-leave-requests/{userId}")
	public ResponseEntity<?> getAllLeaveRequests(@PathVariable int userId)
	{
		return leaveApplicationService.getAllLeaveRequests(userId);
	}
	
	
	@PostMapping("/approve-leave/{userId}/{requesterId}")
	public ResponseEntity<String> approveLeaveRequest(@PathVariable int userId, @PathVariable int requesterId)
	{
		return leaveApplicationService.approveLeaveRequest(userId,requesterId);
	}
	
	@PostMapping("/reject-leave/{userId}/{requesterId}")
	public ResponseEntity<String> rejectLeaveRequest(@PathVariable int userId, @PathVariable int requesterId)
	{
		return leaveApplicationService.rejectLeaveRequest(userId,requesterId);
	}

}
