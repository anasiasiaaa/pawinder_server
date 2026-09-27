package com.example.pawinder_server.repository_interfaces;

import com.example.pawinder_server.entity_classes.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUser_Id(Integer userId);
}
