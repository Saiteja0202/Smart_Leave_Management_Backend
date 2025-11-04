package com.smartleavemanagement.DTOs;

import java.time.LocalDate;

import com.smartleavemanagement.enums.LeaveStatus;
import com.smartleavemanagement.enums.LeaveTypePlannedAndUnplanned;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class LeaveRequests {

	
	private int leaveId;
	
	private String userName;
	
	private int userId;
	
	private String userRole;
	
	private String leaveType;
	
	@Enumerated(EnumType.STRING)
	private LeaveStatus leaveStatus;
	
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	private float duration;
	
	private String approver;

	private LeaveTypePlannedAndUnplanned leaveTypePlannedAndUnplanned;

	public int getLeaveId() {
		return leaveId;
	}

	public void setLeaveId(int leaveId) {
		this.leaveId = leaveId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public LeaveStatus getLeaveStatus() {
		return leaveStatus;
	}

	public void setLeaveStatus(LeaveStatus leaveStatus) {
		this.leaveStatus = leaveStatus;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public LeaveTypePlannedAndUnplanned getLeaveTypePlannedAndUnplanned() {
		return leaveTypePlannedAndUnplanned;
	}

	public void setLeaveTypePlannedAndUnplanned(LeaveTypePlannedAndUnplanned leaveTypePlannedAndUnplanned) {
		this.leaveTypePlannedAndUnplanned = leaveTypePlannedAndUnplanned;
	}

	public LeaveRequests(int leaveId, String userName, int userId, String userRole, String leaveType,
			LeaveStatus leaveStatus, LocalDate startDate, LocalDate endDate, float duration, String approver,
			LeaveTypePlannedAndUnplanned leaveTypePlannedAndUnplanned) {
		super();
		this.leaveId = leaveId;
		this.userName = userName;
		this.userId = userId;
		this.userRole = userRole;
		this.leaveType = leaveType;
		this.leaveStatus = leaveStatus;
		this.startDate = startDate;
		this.endDate = endDate;
		this.duration = duration;
		this.approver = approver;
		this.leaveTypePlannedAndUnplanned = leaveTypePlannedAndUnplanned;
	}

	public LeaveRequests() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	
}
