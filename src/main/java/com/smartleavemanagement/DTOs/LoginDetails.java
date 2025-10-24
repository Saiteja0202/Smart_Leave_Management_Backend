package com.smartleavemanagement.DTOs;

public class LoginDetails {
	
	private String userName;
	private String password;
	public LoginDetails(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}
	public LoginDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
