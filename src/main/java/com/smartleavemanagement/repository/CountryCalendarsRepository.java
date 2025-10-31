package com.smartleavemanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.smartleavemanagement.model.CountryCalendars;

public interface CountryCalendarsRepository extends JpaRepository<CountryCalendars, Integer> {
	
	List<CountryCalendars> findByCountryName(String countryName);
	
	List<CountryCalendars> findAllByCountryName(String countryName);
	

    @Query("SELECT c FROM CountryCalendars c WHERE c.countryName = :countryName AND c.holidayDate = :holidayDate AND c.holidayName = :holidayName")
    Optional<CountryCalendars> findExistingHoliday(String countryName, LocalDate holidayDate, String holidayName);
}
