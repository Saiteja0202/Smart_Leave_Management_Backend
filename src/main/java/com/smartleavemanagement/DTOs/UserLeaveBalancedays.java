package com.smartleavemanagement.DTOs;

public class UserLeaveBalancedays {
	
	
	private float sickLeave;
	
	private float casualLeave;
	
	private float lossOfPay;
	
	private float earnedLeave;
	
	private float paternityLeave;
	
	private float maternityLeave;
	
	private float totalLeaves;

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

	public UserLeaveBalancedays(float sickLeave, float casualLeave, float lossOfPay, float earnedLeave,
			float paternityLeave, float maternityLeave, float totalLeaves) {
		super();
		this.sickLeave = sickLeave;
		this.casualLeave = casualLeave;
		this.lossOfPay = lossOfPay;
		this.earnedLeave = earnedLeave;
		this.paternityLeave = paternityLeave;
		this.maternityLeave = maternityLeave;
		this.totalLeaves = totalLeaves;
	}

	public UserLeaveBalancedays() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	

}
