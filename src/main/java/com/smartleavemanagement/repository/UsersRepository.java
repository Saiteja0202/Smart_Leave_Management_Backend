package com.smartleavemanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartleavemanagement.model.Users;

public interface UsersRepository extends JpaRepository<Users, Integer> {

	Optional<Users> findByUserName(String userName);
	boolean existsByUserName(String userName);
	boolean existsByEmail(String email);
	Optional<Users> findByEmail(String email);
	Optional<Users> findByOtp(int otp);
	Optional<Users> findById(int userId);
	Optional<Users> findByRole_RoleNameIgnoreCase(String roleName);
	

	
}
