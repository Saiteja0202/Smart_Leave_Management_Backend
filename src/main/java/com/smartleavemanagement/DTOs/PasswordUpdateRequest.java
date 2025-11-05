package com.smartleavemanagement.DTOs;

public class PasswordUpdateRequest {
    private String oldPassword;
    private String newPassword;

    
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

	public PasswordUpdateRequest(String oldPassword, String newPassword) {
		super();
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}

	public PasswordUpdateRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
    
}

