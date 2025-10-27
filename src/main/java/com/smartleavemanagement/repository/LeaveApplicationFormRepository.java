package com.smartleavemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartleavemanagement.model.LeaveApplicationForm;

public interface LeaveApplicationFormRepository extends JpaRepository<LeaveApplicationForm, Integer> {
	
	
	

}
