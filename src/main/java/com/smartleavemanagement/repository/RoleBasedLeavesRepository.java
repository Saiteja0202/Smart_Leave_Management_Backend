package com.smartleavemanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartleavemanagement.model.RoleBasedLeaves;

public interface RoleBasedLeavesRepository extends JpaRepository<RoleBasedLeaves, Integer> {
	
	
	Optional<RoleBasedLeaves> findByRole(String role);
	
	Optional<RoleBasedLeaves> findByRoleBasedLeaveId(int roleBasedLeaveId);

}
