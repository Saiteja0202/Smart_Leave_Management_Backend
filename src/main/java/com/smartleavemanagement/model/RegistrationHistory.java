package com.smartleavemanagement.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="registration_history")
public class RegistrationHistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int registrationId;
	
	private String firstName;
	
	private String lastName;
	
	private int userId;
	
	private LocalDateTime registerDate;
	
	private String email;
	
	private String role;

	public RegistrationHistory(int registrationId, String firstName, String lastName, int userId,
			LocalDateTime registerDate, String email) {
		super();
		this.registrationId = registrationId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userId = userId;
		this.registerDate = registerDate;
		this.email = email;
	}

	public int getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(int registrationId) {
		this.registrationId = registrationId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public LocalDateTime getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(LocalDateTime registerDate) {
		this.registerDate =  registerDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public RegistrationHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getRole() {
		return role;
	}

	public void setRole(String roles) {
		role = roles;
	}
	
	
	
	

}
