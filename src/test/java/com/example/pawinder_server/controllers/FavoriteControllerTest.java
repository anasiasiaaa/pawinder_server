package com.example.pawinder_server.controllers;

import com.example.pawinder_server.entity_classes.Favorite;
import com.example.pawinder_server.entity_classes.Pet;
import com.example.pawinder_server.entity_classes.User;
import com.example.pawinder_server.repository_interfaces.FavoriteRepository;
import com.example.pawinder_server.repository_interfaces.PetRepository;
import com.example.pawinder_server.repository_interfaces.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FavoriteControllerTest {

    private FavoriteRepository favorites;
    private UserRepository users;
    private PetRepository pets;
    private FavoriteController controller;

    @BeforeEach
    void setUp() {
        favorites = mock(FavoriteRepository.class);
        users = mock(UserRepository.class);
        pets = mock(PetRepository.class);
        controller = new FavoriteController(favorites, users, pets);
    }

    @Test
    void addCreatesFavoriteOnce() {
        when(users.findById(1)).thenReturn(Optional.of(new User()));
        when(pets.findById(5)).thenReturn(Optional.of(new Pet()));
        when(favorites.existsByUser_IdAndPet_Id(1, 5)).thenReturn(false);

        ResponseEntity<?> r = controller.add(1, 5);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        verify(favorites).save(any(Favorite.class));
    }

    // FR-03: повторное добавление не создаёт вторую запись
    @Test
    void addTwiceDoesNotDuplicate() {
        when(users.findById(1)).thenReturn(Optional.of(new User()));
        when(pets.findById(5)).thenReturn(Optional.of(new Pet()));
        when(favorites.existsByUser_IdAndPet_Id(1, 5)).thenReturn(true);

        ResponseEntity<?> r = controller.add(1, 5);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        verify(favorites, never()).save(any());
    }

    @Test
    void addRaceOnUniqueKeyIsTreatedAsAlreadyAdded() {
        when(users.findById(1)).thenReturn(Optional.of(new User()));
        when(pets.findById(5)).thenReturn(Optional.of(new Pet()));
        when(favorites.existsByUser_IdAndPet_Id(1, 5)).thenReturn(false);
        when(favorites.save(any())).thenThrow(new DataIntegrityViolationException("dup"));

        assertEquals(HttpStatus.OK, controller.add(1, 5).getStatusCode());
    }

    @Test
    void addUnknownUserOrPetIs404() {
        when(users.findById(1)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, controller.add(1, 5).getStatusCode());

        when(users.findById(2)).thenReturn(Optional.of(new User()));
        when(pets.findById(9)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, controller.add(2, 9).getStatusCode());
        verify(favorites, never()).save(any());
    }

    @Test
    void listReturnsPetsOfUser() {
        Pet pet = new Pet();
        Favorite f = new Favorite();
        f.setPet(pet);
        when(users.existsById(1)).thenReturn(true);
        when(favorites.findByUser_Id(1)).thenReturn(List.of(f));

        ResponseEntity<?> r = controller.list(1);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(List.of(pet), r.getBody());
    }

    @Test
    void listUnknownUserIs404() {
        when(users.existsById(7)).thenReturn(false);
        assertEquals(HttpStatus.NOT_FOUND, controller.list(7).getStatusCode());
    }

    @Test
    void removeDeletesAndIsIdempotent() {
        Favorite f = new Favorite();
        when(favorites.findByUser_IdAndPet_Id(1, 5)).thenReturn(Optional.of(f), Optional.empty());

        assertEquals(HttpStatus.NO_CONTENT, controller.remove(1, 5).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.remove(1, 5).getStatusCode());
        verify(favorites, times(1)).delete(f);
    }
}
