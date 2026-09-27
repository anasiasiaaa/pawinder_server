package com.example.pawinder_server.repository_interfaces;

import com.example.pawinder_server.entity_classes.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Integer> {
    List<Pet> findByType_Id(Integer typeId);
    boolean existsByApiPetIdAndType_Id(String apiPetId, Integer typeId);
}
