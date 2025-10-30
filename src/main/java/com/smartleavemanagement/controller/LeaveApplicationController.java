package com.smartleavemanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartleavemanagement.DTOs.LeaveStartAndEndDates;
import com.smartleavemanagement.exceptions.InvalidLeaveDates;
import com.smartleavemanagement.model.LeaveApplicationForm;
import com.smartleavemanagement.service.LeaveApplicationService;

@RestController
@RequestMapping("/users")
public class LeaveApplicationController {

    private final LeaveApplicationService leaveApplicationService;

    public LeaveApplicationController(LeaveApplicationService leaveApplicationService) {
        this.leaveApplicationService = leaveApplicationService;
    }


    @PostMapping("/calculate-duration/{userId}")
    public ResponseEntity<?> calculateDuration(
            @PathVariable int userId,
            @RequestBody LeaveStartAndEndDates leaveStartAndEndDates,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return leaveApplicationService.calculateDuration(userId,
                leaveStartAndEndDates.getStartDate(),
                leaveStartAndEndDates.getEndDate(),
                token);
        
    }


    @PostMapping("/apply-leave/{userId}")
    public ResponseEntity<String> applyLeave(
            @PathVariable int userId,
            @RequestBody LeaveApplicationForm leaveApplicationForm,
            @RequestHeader("Authorization") String authHeader) throws InvalidLeaveDates {

        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return leaveApplicationService.applyLeave(userId, leaveApplicationForm,token);
    }


    @GetMapping("/get-leave-requests/{userId}")
    public ResponseEntity<?> getLeaveRequests(@PathVariable int userId) {
        return leaveApplicationService.getLeaveRequests(userId);
    }

    @GetMapping("/get-all-leave-requests/{userId}")
    public ResponseEntity<?> getAllLeaveRequests(@PathVariable int userId) {
        return leaveApplicationService.getAllLeaveRequests(userId);
    }

    @PostMapping("/approve-leave/{userId}/{requesterId}")
    public ResponseEntity<String> approveLeaveRequest(
            @PathVariable int userId,
            @PathVariable int requesterId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return leaveApplicationService.approveLeaveRequest(userId, requesterId,token);
    }


    @PostMapping("/reject-leave/{userId}/{requesterId}")
    public ResponseEntity<String> rejectLeaveRequest(
            @PathVariable int userId,
            @PathVariable int requesterId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return leaveApplicationService.rejectLeaveRequest(userId, requesterId,token);
    }


    @PutMapping("/cancel-leave/{userId}/{leaveId}")
    public ResponseEntity<String> cancelLeave(
            @PathVariable int userId,
            @PathVariable int leaveId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return leaveApplicationService.cancelLeave(userId, leaveId,token);
    }
}
