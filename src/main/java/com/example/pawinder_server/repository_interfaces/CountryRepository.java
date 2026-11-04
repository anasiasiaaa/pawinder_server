package com.example.pawinder_server.repository_interfaces;

import com.example.pawinder_server.entity_classes.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    List<Country> findAllByOrderByNameAsc();
}