package com.example.pawinder_server.controllers;

import com.example.pawinder_server.entity_classes.Pet;
import com.example.pawinder_server.repository_interfaces.PetRepository;
import com.example.pawinder_server.service.PetSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    @Autowired
    private PetSyncService syncService;
    @Autowired
    private PetRepository petRepository;

    @PostMapping("/sync/cats")
    public String syncCats(@RequestParam(defaultValue = "20") int count) {
        syncService.syncCats(count);
        return "Синхронизировано";
    }

    @PostMapping("/sync/dogs")
    public String syncDogs(@RequestParam(defaultValue = "20") int count) {
        syncService.syncDogs(count);
        return "Синхронизировано";
    }

    @GetMapping
    public List<Pet> getAll() {
        return petRepository.findAll();
    }

    @GetMapping("/type/{typeId}")
    public List<Pet> getByType(@PathVariable Integer typeId) {
        return petRepository.findByType_Id(typeId);
    }
}