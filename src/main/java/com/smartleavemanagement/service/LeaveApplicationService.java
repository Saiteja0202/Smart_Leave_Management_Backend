package com.smartleavemanagement.service;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;

import com.smartleavemanagement.DTOs.LeaveStartAndEndDates;
import com.smartleavemanagement.model.LeaveApplicationForm;

public interface LeaveApplicationService {
	
	ResponseEntity<?> calculateDuration(int userId,LocalDate startDate,LocalDate endDate);
	ResponseEntity<String> applyLeave(int userId,LeaveApplicationForm leaveApplicationForm);

}
