package com.smartleavemanagement.service;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;

import com.smartleavemanagement.exceptions.InvalidLeaveDates;
import com.smartleavemanagement.model.LeaveApplicationForm;

public interface LeaveApplicationService {

    ResponseEntity<?> calculateDuration(int userId, LocalDate startDate, LocalDate endDate, String token);

    ResponseEntity<String> applyLeave(int userId, LeaveApplicationForm leaveApplicationForm, String token) throws InvalidLeaveDates;

    ResponseEntity<?> getLeaveRequests(int userId);

    ResponseEntity<?> getAllLeaveRequests(int userId);

    ResponseEntity<String> approveLeaveRequest(int userId, int requesterId, String token);

    ResponseEntity<String> rejectLeaveRequest(int userId, int requesterId, String token);

    ResponseEntity<String> cancelLeave(int userId, int leaveId, String token);
}
