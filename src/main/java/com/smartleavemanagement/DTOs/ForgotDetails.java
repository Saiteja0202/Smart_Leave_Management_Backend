package com.smartleavemanagement.DTOs;

public class ForgotDetails {
	
	
	private int otp;
	
	private String email;
	
	private String newPassword;

	public int getOtp() {
		return otp;
	}

	public void setOtp(int otp) {
		this.otp = otp;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public ForgotDetails(int otp, String email, String newPassword) {
		super();
		this.otp = otp;
		this.email = email;
		this.newPassword = newPassword;
	}

	public ForgotDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
