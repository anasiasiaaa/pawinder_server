package com.example.pawinder_server.controllers;

import com.example.pawinder_server.entity_classes.Pet;
import com.example.pawinder_server.repository_interfaces.PetRepository;
import com.example.pawinder_server.service.PetSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private static final int CAT_TYPE_ID = 1;
    private static final int DOG_TYPE_ID = 2;

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

    // Постраничная выдача: size кошек + size собак, перемешанных
    @GetMapping
    public List<Pet> getPage(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        List<Pet> result = new ArrayList<>();
        result.addAll(petRepository.findByType_Id(CAT_TYPE_ID, pageable).getContent());
        result.addAll(petRepository.findByType_Id(DOG_TYPE_ID, pageable).getContent());
        Collections.shuffle(result);
        return result;
    }

    @GetMapping("/type/{typeId}")
    public List<Pet> getByType(@PathVariable Integer typeId) {
        return petRepository.findByType_Id(typeId);
    }
}