package com.example.pawinder_server.repository_interfaces;

import com.example.pawinder_server.entity_classes.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Integer> {
    List<City> findByCountryIdOrderByNameAsc(Integer countryId);
}