package com.example.pawinder_server.controllers;

import com.example.pawinder_server.entity_classes.City;
import com.example.pawinder_server.entity_classes.Country;
import com.example.pawinder_server.entity_classes.User;
import com.example.pawinder_server.repository_interfaces.CityRepository;
import com.example.pawinder_server.repository_interfaces.CountryRepository;
import com.example.pawinder_server.repository_interfaces.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProfileController {

    // имена полей совпадают с клиентскими моделями (Gson читает по имени поля)
    public record ProfileResponse(Integer id_user, String nickname, String email,
                                  String name, String surname,
                                  Integer id_country, String country,
                                  Integer id_city, String city) {}

    public record ProfileUpdateRequest(String name, String surname, Integer id_city) {}

    public record CountryDto(Integer id_country, String country) {}

    public record CityDto(Integer id_city, String city, Integer id_country) {}

    private static final int MAX_FIELD_LENGTH = 45; // varchar(45) в таблице user

    private final UserRepository users;
    private final CountryRepository countries;
    private final CityRepository cities;

    public ProfileController(UserRepository users, CountryRepository countries, CityRepository cities) {
        this.users = users;
        this.countries = countries;
        this.cities = cities;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Integer id) {
        Optional<User> found = users.findById(id);
        if (found.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }
        return ResponseEntity.ok(toResponse(found.get()));
    }

    @PutMapping("/users/{id}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Integer id, @RequestBody ProfileUpdateRequest r) {
        Optional<User> found = users.findById(id);
        if (found.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }

        String name = clean(r.name());
        String surname = clean(r.surname());
        if ((name != null && name.length() > MAX_FIELD_LENGTH)
                || (surname != null && surname.length() > MAX_FIELD_LENGTH)) {
            return ResponseEntity.badRequest().body("Не больше 45 символов");
        }
        if (r.id_city() != null && !cities.existsById(r.id_city())) {
            return ResponseEntity.badRequest().body("Такого города нет");
        }

        User u = found.get();
        u.setName(name);
        u.setSurname(surname);
        u.setCityId(r.id_city());
        users.save(u);

        return ResponseEntity.ok(toResponse(u));
    }

    @GetMapping("/countries")
    public List<CountryDto> getCountries() {
        return countries.findAllByOrderByNameAsc().stream()
                .map(c -> new CountryDto(c.getId(), c.getName()))
                .toList();
    }

    @GetMapping("/countries/{id}/cities")
    public List<CityDto> getCities(@PathVariable Integer id) {
        return cities.findByCountryIdOrderByNameAsc(id).stream()
                .map(c -> new CityDto(c.getId(), c.getName(), c.getCountryId()))
                .toList();
    }

    private ProfileResponse toResponse(User u) {
        City city = u.getCityId() == null ? null : cities.findById(u.getCityId()).orElse(null);
        Country country = city == null ? null : countries.findById(city.getCountryId()).orElse(null);
        return new ProfileResponse(
                u.getId(), u.getNickname(), u.getEmail(),
                u.getName(), u.getSurname(),
                country == null ? null : country.getId(),
                country == null ? null : country.getName(),
                city == null ? null : city.getId(),
                city == null ? null : city.getName());
    }

    private static String clean(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}