package com.smartleavemanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartleavemanagement.model.CountryCalendars;

public interface CountryCalendarsRepository extends JpaRepository<CountryCalendars, Integer> {
	
	List<CountryCalendars> findByCountryName(String countryName);
	
	List<CountryCalendars> findAllByCountryName(String countryName);
}
