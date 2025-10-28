package com.smartleavemanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartleavemanagement.model.LeaveApplicationForm;

public interface LeaveApplicationFormRepository extends JpaRepository<LeaveApplicationForm, Integer> {
	
	
	List<LeaveApplicationForm> findByUserId(int userId);
	
	

}
