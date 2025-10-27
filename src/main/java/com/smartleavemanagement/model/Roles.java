package com.smartleavemanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;



@Entity
@Table(name = "roles")
public class Roles {
	
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int roleId;
	
	
	
	@NotBlank(message="Role name is required")
	private String roleName;
	
	
	@NotBlank(message = "Role description is required")
	private String description;

	public Roles(int roleId, String roleName, String description) {
		super();
		this.roleId = roleId;
		this.roleName = roleName;
		this.description = description;
	}

	public Roles() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
