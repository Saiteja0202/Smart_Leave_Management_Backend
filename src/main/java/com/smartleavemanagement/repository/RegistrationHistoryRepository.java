package com.smartleavemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartleavemanagement.model.RegistrationHistory;

public interface RegistrationHistoryRepository extends JpaRepository<RegistrationHistory, Integer> {

}
