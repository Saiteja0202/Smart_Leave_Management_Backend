package com.smartleavemanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartleavemanagement.model.Admins;
import com.smartleavemanagement.model.Users;
import com.smartleavemanagement.model.UsersLeaveBalance;

public interface UsersLeaveBalanceRepository extends JpaRepository<UsersLeaveBalance, Integer> {

	UsersLeaveBalance findByUser_UserId(int userId);
	
	

}
