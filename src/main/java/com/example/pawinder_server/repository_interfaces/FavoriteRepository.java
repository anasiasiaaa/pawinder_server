package com.example.pawinder_server.repository_interfaces;

import com.example.pawinder_server.entity_classes.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUser_Id(Integer userId);
    boolean existsByUser_IdAndPet_Id(Integer userId, Integer petId);
    Optional<Favorite> findByUser_IdAndPet_Id(Integer userId, Integer petId);
}
