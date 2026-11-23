package com.example.pawinder_server.controllers;

import com.example.pawinder_server.entity_classes.Favorite;
import com.example.pawinder_server.entity_classes.Pet;
import com.example.pawinder_server.entity_classes.User;
import com.example.pawinder_server.repository_interfaces.FavoriteRepository;
import com.example.pawinder_server.repository_interfaces.PetRepository;
import com.example.pawinder_server.repository_interfaces.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/** Избранное пользователя: список, добавление (без дублей) и удаление. */
@RestController
@RequestMapping("/api/users/{userId}/favorites")
public class FavoriteController {

    private final FavoriteRepository favorites;
    private final UserRepository users;
    private final PetRepository pets;

    public FavoriteController(FavoriteRepository favorites, UserRepository users, PetRepository pets) {
        this.favorites = favorites;
        this.users = users;
        this.pets = pets;
    }

    /** Питомцы из избранного пользователя (ответ — список Pet, как в /api/pets). */
    @GetMapping
    public ResponseEntity<?> list(@PathVariable Integer userId) {
        if (!users.existsById(userId)) {
            return notFound("Пользователь не найден");
        }
        List<Pet> result = favorites.findByUser_Id(userId).stream()
                .map(Favorite::getPet)
                .toList();
        return ResponseEntity.ok(result);
    }

    /**
     * Добавляет питомца в избранное. Повторный вызов не создаёт вторую запись
     * (FR-03): сервер вернёт 200 вместо 201.
     */
    @PostMapping("/{petId}")
    public ResponseEntity<?> add(@PathVariable Integer userId, @PathVariable Integer petId) {
        Optional<User> user = users.findById(userId);
        if (user.isEmpty()) {
            return notFound("Пользователь не найден");
        }
        Optional<Pet> pet = pets.findById(petId);
        if (pet.isEmpty()) {
            return notFound("Питомец не найден");
        }
        if (favorites.existsByUser_IdAndPet_Id(userId, petId)) {
            return ResponseEntity.ok(pet.get());
        }

        Favorite f = new Favorite();
        f.setUser(user.get());
        f.setPet(pet.get());
        try {
            favorites.save(f);
        } catch (DataIntegrityViolationException e) {
            // параллельный запрос успел раньше — сработал uk_user_pet_fav
            return ResponseEntity.ok(pet.get());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(pet.get());
    }

    /** Убирает питомца из избранного; если его там не было — тоже 204. */
    @DeleteMapping("/{petId}")
    public ResponseEntity<?> remove(@PathVariable Integer userId, @PathVariable Integer petId) {
        favorites.findByUser_IdAndPet_Id(userId, petId).ifPresent(favorites::delete);
        return ResponseEntity.noContent().build();
    }

    private static ResponseEntity<String> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }
}
