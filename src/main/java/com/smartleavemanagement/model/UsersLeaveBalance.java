package com.smartleavemanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="users_leave_balance")
public class UsersLeaveBalance {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userLeaveBalanceId;
	
	@NotNull(message = "Required")
	private String role;

	@NotNull(message = "Required")
	private float sickLeave;

	@NotNull(message = "Required")
	private float casualLeave;

	@NotNull(message = "Required")
	private float lossOfPay;

	@NotNull(message = "Required")
	private float earnedLeave;

	@NotNull(message = "Required")
	private float paternityLeave;

	@NotNull(message = "Required")
	private float maternityLeave;

	@NotNull(message = "Required")
	private float totalLeaves;
	
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "userId")
	private Users user;


	
	public UsersLeaveBalance(int userLeaveBalanceId, @NotNull(message = "Required") String role,
			@NotNull(message = "Required") float sickLeave, @NotNull(message = "Required") float casualLeave,
			@NotNull(message = "Required") float lossOfPay, @NotNull(message = "Required") float earnedLeave,
			@NotNull(message = "Required") float paternityLeave, @NotNull(message = "Required") float maternityLeave,
			@NotNull(message = "Required") float totalLeaves, Users user) {
		super();
		this.userLeaveBalanceId = userLeaveBalanceId;
		this.role = role;
		this.sickLeave = sickLeave;
		this.casualLeave = casualLeave;
		this.lossOfPay = lossOfPay;
		this.earnedLeave = earnedLeave;
		this.paternityLeave = paternityLeave;
		this.maternityLeave = maternityLeave;
		this.totalLeaves = totalLeaves;
		this.user = user;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public int getUserLeaveBalanceId() {
		return userLeaveBalanceId;
	}

	public void setUserLeaveBalanceId(int userLeaveBalanceId) {
		this.userLeaveBalanceId = userLeaveBalanceId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public float getSickLeave() {
		return sickLeave;
	}

	public void setSickLeave(float sickLeave) {
		this.sickLeave = sickLeave;
	}

	public float getCasualLeave() {
		return casualLeave;
	}

	public void setCasualLeave(float casualLeave) {
		this.casualLeave = casualLeave;
	}

	public float getLossOfPay() {
		return lossOfPay;
	}

	public void setLossOfPay(float lossOfPay) {
		this.lossOfPay = lossOfPay;
	}

	public float getEarnedLeave() {
		return earnedLeave;
	}

	public void setEarnedLeave(float earnedLeave) {
		this.earnedLeave = earnedLeave;
	}

	public float getPaternityLeave() {
		return paternityLeave;
	}

	public void setPaternityLeave(float paternityLeave) {
		this.paternityLeave = paternityLeave;
	}

	public float getMaternityLeave() {
		return maternityLeave;
	}

	public void setMaternityLeave(float maternityLeave) {
		this.maternityLeave = maternityLeave;
	}

	public float getTotalLeaves() {
		return totalLeaves;
	}

	public void setTotalLeaves(float totalLeaves) {
		this.totalLeaves = totalLeaves;
	}

	

	public UsersLeaveBalance() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "UsersLeaveBalance [userLeaveBalanceId=" + userLeaveBalanceId + ", role=" + role + ", sickLeave="
				+ sickLeave + ", casualLeave=" + casualLeave + ", lossOfPay=" + lossOfPay + ", earnedLeave="
				+ earnedLeave + ", paternityLeave=" + paternityLeave + ", maternityLeave=" + maternityLeave
				+ ", totalLeaves=" + totalLeaves + ", user=" + user + "]";
	}
	
	
	
	

}
