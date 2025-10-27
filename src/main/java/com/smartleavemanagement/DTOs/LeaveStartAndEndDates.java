package com.smartleavemanagement.DTOs;

import java.time.LocalDate;

public class LeaveStartAndEndDates {
	
	private LocalDate startDate;
	
	private LocalDate endDate;

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

	public LeaveStartAndEndDates(LocalDate startDate, LocalDate endDate) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public LeaveStartAndEndDates() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
