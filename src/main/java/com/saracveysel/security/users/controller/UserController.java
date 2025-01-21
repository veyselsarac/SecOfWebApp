package com.saracveysel.security.users.controller;

import com.saracveysel.security.users.exception.UserServiceException;
import com.saracveysel.security.users.model.User;
import com.saracveysel.security.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class UserController {

    private final UserService userService;

    // Constructor Injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Kullanıcı kaydı (signup).
     * Artık @RequestBody ile JSON'dan okuyoruz.
     *
     * Örnek POST isteği (JSON):
     * {
     *   "userName": "john",
     *   "email": "john@example.com",
     *   "password": "123456"
     * }
     */
    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody User requestUser) {
        // Servis katmanında var olan metodu çağırıyoruz.
        // userName, email, password bilgilerini JSON'dan alıyoruz.
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

    /**
     * Oturum açmış kullanıcının bilgilerini döndürür.
     * Örnek: GET /users/me
     */
    @GetMapping("/me")
    public ResponseEntity<User> getAuthenticatedUser() {
        try {
            User user = userService.getAuthenticatedUser();
            return ResponseEntity.ok(user);
        } catch (UserServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Kullanıcı adından kullanıcı bilgisi getirme.
     * Örnek: GET /users/john
     */
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.findByUserName(username);
            return ResponseEntity.ok(user);
        } catch (UserServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Login endpointi. Artık @RequestBody ile JSON'dan okuyoruz.
     *
     * Örnek POST isteği (JSON):
     * {
     *   "userName": "john",
     *   "password": "123456"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User requestUser) {
        try {
            boolean authenticated = userService.authenticateUser(
                    requestUser.getUserName(),
                    requestUser.getPassword()
            );
            if (authenticated) {
                return ResponseEntity.ok("Login successful");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (UserServiceException e) {
            // Eğer kullanıcı yoksa veya başka bir hata oluşursa
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
