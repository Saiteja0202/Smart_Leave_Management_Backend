package com.smartleavemanagement.repository;



import org.springframework.data.jpa.repository.JpaRepository;


import com.smartleavemanagement.model.UsersLeaveBalance;

public interface UsersLeaveBalanceRepository extends JpaRepository<UsersLeaveBalance, Integer> {

	UsersLeaveBalance findByUser_UserId(int userId);
	
	

}
