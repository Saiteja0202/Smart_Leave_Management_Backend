package com.smartleavemanagement.model;


import java.time.DayOfWeek;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="country_calendars")
public class CountryCalendars {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int holidayId;
	
	
	@NotBlank(message="Country name is required")
	private String countryName;
	
	
	@NotNull(message="Calendar year is required")
	private int calendarYear;
	
	
	@NotNull(message = "Holiday Date is required")
	private LocalDate holidayDate;
	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "holiday_day")
	private DayOfWeek holidayDay;
	
	@NotBlank(message = "City Name is required")
	private String cityName;
	
	
	@NotBlank(message = "Holiday Name is required")
	private String holidayName;

	public CountryCalendars() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CountryCalendars(int holidayId, @NotBlank(message = "Country name is required") String countryName,
			@NotNull(message = "Calendar year is required") int calendarYear, LocalDate holidayDate,
			 String holidayName,String cityName) {
		super();
		this.holidayId = holidayId;
		this.countryName = countryName;
		this.calendarYear = calendarYear;
		this.holidayDate = holidayDate;
		this.cityName = cityName;
		this.holidayName = holidayName;
	}

	public String getHolidayName() {
		return holidayName;
	}

	public void setHolidayName(String holidayName) {
		this.holidayName = holidayName;
	}

	public int getHolidayId() {
		return holidayId;
	}

	public void setHolidayId(int holidayId) {
		this.holidayId = holidayId;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public int getCalendarYear() {
		return calendarYear;
	}

	public void setCalendarYear(int calendarYear) {
		this.calendarYear = calendarYear;
	}

	public LocalDate getHolidayDate() {
		return holidayDate;
	}

	public void setHolidayDate(LocalDate holidayDate) {
		this.holidayDate = holidayDate;
	}

	public DayOfWeek getHolidayDay() {
		return holidayDay;
	}

	public void setHolidayDay(DayOfWeek holidayDay) {
		this.holidayDay = holidayDay;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	
	

	
	

}
