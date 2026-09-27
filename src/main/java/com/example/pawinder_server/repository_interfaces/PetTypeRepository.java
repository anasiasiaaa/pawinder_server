package com.example.pawinder_server.repository_interfaces;

import com.example.pawinder_server.entity_classes.PetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetTypeRepository extends JpaRepository<PetType, Integer> {
    Optional<PetType> findByTypeName(String typeName);
}
