package com.smartleavemanagement.model;

import java.time.LocalDate;

import com.smartleavemanagement.enums.LeaveStatus;
import com.smartleavemanagement.enums.LeaveTypePlannedAndUnplanned;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
public class LeaveApplicationForm {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int leaveId;
	

	private int userId;
	
	private String roleName;
	
	
	@NotBlank(message = "Leave type is required")
	private String leaveType;
	
	@NotNull(message = "Start date is required")
	private LocalDate startDate;
	
	@NotNull(message = "Start date is required")
	private LocalDate endDate;
	
	
	@NotBlank(message = "Reason for leave is required")
	private String comments;

	
	@Enumerated(EnumType.STRING)
	private LeaveStatus leaveStatus;
	
	
	private String approver;
	
	private float duration;

	private LocalDate appliedDate;

	@Enumerated(EnumType.STRING)
	private LeaveTypePlannedAndUnplanned leaveTypePlannedAndUnplanned;

	public int getLeaveId() {
		return leaveId;
	}

	public void setLeaveId(int leaveId) {
		this.leaveId = leaveId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public LeaveStatus getLeaveStatus() {
		return leaveStatus;
	}

	public void setLeaveStatus(LeaveStatus leaveStatus) {
		this.leaveStatus = leaveStatus;
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public LocalDate getAppliedDate() {
		return appliedDate;
	}

	public void setAppliedDate(LocalDate appliedDate) {
		this.appliedDate = appliedDate;
	}

	public LeaveTypePlannedAndUnplanned getLeaveTypePlannedAndUnplanned() {
		return leaveTypePlannedAndUnplanned;
	}

	public void setLeaveTypePlannedAndUnplanned(LeaveTypePlannedAndUnplanned leaveTypePlannedAndUnplanned) {
		this.leaveTypePlannedAndUnplanned = leaveTypePlannedAndUnplanned;
	}

	public LeaveApplicationForm() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LeaveApplicationForm(int leaveId, int userId, String roleName,
			@NotBlank(message = "Leave type is required") String leaveType,
			@NotNull(message = "Start date is required") LocalDate startDate,
			@NotNull(message = "Start date is required") LocalDate endDate,
			@NotBlank(message = "Reason for leave is required") String comments, LeaveStatus leaveStatus,
			String approver, float duration, LocalDate appliedDate,
			LeaveTypePlannedAndUnplanned leaveTypePlannedAndUnplanned) {
		super();
		this.leaveId = leaveId;
		this.userId = userId;
		this.roleName = roleName;
		this.leaveType = leaveType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.comments = comments;
		this.leaveStatus = leaveStatus;
		this.approver = approver;
		this.duration = duration;
		this.appliedDate = appliedDate;
		this.leaveTypePlannedAndUnplanned = leaveTypePlannedAndUnplanned;
	}
	
	
	
	
}
