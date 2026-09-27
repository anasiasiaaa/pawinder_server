package com.example.pawinder_server.repository_interfaces;

import com.example.pawinder_server.entity_classes.Swipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SwipeRepository extends JpaRepository<Swipe, Integer> {
    List<Swipe> findByUser_Id(Integer userId);
}
