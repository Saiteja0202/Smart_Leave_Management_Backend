package com.smartleavemanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartleavemanagement.model.Admins;

public interface AdminsRepository extends JpaRepository<Admins, Integer> {

	
	Optional<Admins> findByUserName(String userName);
	boolean existsByUserName(String userName);
	boolean existsByEmail(String email);
	
	
}
