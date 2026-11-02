package com.example.pawinder_server.controllers;

import com.example.pawinder_server.entity_classes.User;
import com.example.pawinder_server.repository_interfaces.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public record RegisterRequest(String nickname, String email, String password) {}

    public record LoginRequest(String nickname, String password) {}

    // имя поля id_user совпадает с клиентской моделью User
    public record UserResponse(Integer id_user, String nickname, String email) {}

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_FIELD_LENGTH = 45; // varchar(45) в таблице user

    private final UserRepository users;

    public AuthController(UserRepository users) {
        this.users = users;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest r) {
        if (isBlank(r.nickname()) || isBlank(r.email()) || isBlank(r.password())
                || !r.email().contains("@")
                || r.password().length() < MIN_PASSWORD_LENGTH
                || r.nickname().length() > MAX_FIELD_LENGTH
                || r.email().length() > MAX_FIELD_LENGTH) {
            return ResponseEntity.badRequest().body("Некорректные данные");
        }
        if (users.existsByNickname(r.nickname()) || users.existsByEmail(r.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Логин или почта уже заняты");
        }

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        User u = new User();
        u.setNickname(r.nickname());
        u.setEmail(r.email());
        u.setSalt(Base64.getEncoder().encodeToString(salt)); // 24 символа, влезает в varchar(45)
        u.setPassHash(hash(r.password(), salt));
        users.save(u);

        return ResponseEntity.ok(new UserResponse(u.getId(), u.getNickname(), u.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest r) {
        Optional<User> found = (isBlank(r.nickname()) || isBlank(r.password()))
                ? Optional.empty()
                : users.findByNickname(r.nickname());

        if (found.isEmpty()) {
            return unauthorized();
        }
        User u = found.get();
        String calculated = hash(r.password(), Base64.getDecoder().decode(u.getSalt()));
        boolean ok = MessageDigest.isEqual(
                calculated.getBytes(StandardCharsets.UTF_8),
                u.getPassHash().getBytes(StandardCharsets.UTF_8));
        if (!ok) {
            return unauthorized();
        }
        return ResponseEntity.ok(new UserResponse(u.getId(), u.getNickname(), u.getEmail()));
    }

    private static ResponseEntity<String> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    // PBKDF2 из стандартного JDK: без новых зависимостей в pom.xml
    private static String hash(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            byte[] h = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(h);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка хэширования пароля", e);
        }
    }
}
