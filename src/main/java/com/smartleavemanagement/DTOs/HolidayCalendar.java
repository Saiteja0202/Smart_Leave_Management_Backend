package com.smartleavemanagement.DTOs;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class HolidayCalendar {
	
	private String holidayName;
	
	private DayOfWeek hoilydayDay;
	
	private LocalDate holidayDate;
	
	private String countryName;

	public String getHolidayName() {
		return holidayName;
	}

	public void setHolidayName(String holidayName) {
		this.holidayName = holidayName;
	}

	public DayOfWeek getHoilydayDay() {
		return hoilydayDay;
	}

	public void setHoilydayDay(DayOfWeek hoilydayDay) {
		this.hoilydayDay = hoilydayDay;
	}

	public LocalDate getHolidayDate() {
		return holidayDate;
	}

	public void setHolidayDate(LocalDate holidayDate) {
		this.holidayDate = holidayDate;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public HolidayCalendar(String holidayName, DayOfWeek hoilydayDay, LocalDate holidayDate, String countryName) {
		super();
		this.holidayName = holidayName;
		this.hoilydayDay = hoilydayDay;
		this.holidayDate = holidayDate;
		this.countryName = countryName;
	}

	public HolidayCalendar() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
