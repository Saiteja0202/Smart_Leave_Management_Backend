package com.smartleavemanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartleavemanagement.model.Roles;

public interface RolesRepository extends JpaRepository<Roles, Integer> {

	
	Optional<Roles> findByRoleNameIgnoreCase(String roleName);
	
}
