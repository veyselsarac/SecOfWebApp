package com.saracveysel.security.users.controller;

import com.saracveysel.security.config.SecurityConfig;
import com.saracveysel.security.users.exception.UserServiceException;
import com.saracveysel.security.users.model.User;
import com.saracveysel.security.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping
public class UserController {

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Constructor Injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody User requestUser) {
        // Şifre güvenliğini kontrol et
        if (!userService.isValidPassword(requestUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Password must be at least 8 characters long, contain at least one digit, one uppercase letter, one lowercase letter, and one special character.");
        }

        // Kullanıcı oluştur
        String result = userService.createUser(
                requestUser.getUserName(),
                requestUser.getEmail(),
                requestUser.getPassword()
        );

        if ("User created".equals(result)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }



    @GetMapping("/me")
    public ResponseEntity<User> getAuthenticatedUser() {
        try {
            User user = userService.getAuthenticatedUser();
            return ResponseEntity.ok(user);
        } catch (UserServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.findByUserName(username);
            return ResponseEntity.ok(user);
        } catch (UserServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User requestUser) {
        try {
            User user = userService.findByUserName(requestUser.getUserName());

            // Kullanıcı bulunamazsa
            if (user == null) {
                log.warn("Failed login attempt: User not found - {}", requestUser.getUserName());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Şifre doğrulama
            if (!passwordEncoder.matches(requestUser.getPassword(), user.getPassword())) {
                log.warn("Failed login attempt: Incorrect password - {}", requestUser.getUserName());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            log.info("Successful login attempt: {}", requestUser.getUserName());

            return ResponseEntity.ok("Login successful");

        } catch (Exception e) {
            log.error("Unexpected error during login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
}
