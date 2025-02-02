package com.saracveysel.security.users.service;

import com.saracveysel.security.users.exception.UserServiceException;
import com.saracveysel.security.users.model.User;
import com.saracveysel.security.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByUserName("testuser")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123!"))
                .thenReturn("hashed_password");

        String result = userService.createUser("testuser", "test@example.com", "SecurePass123!");
        assertEquals("User created", result);
    }

    @Test
    void testCreateUser_Fail_UserExists() {
        when(userRepository.existsByUserName("testuser")).thenReturn(true);
        String result = userService.createUser("testuser", "test@example.com", "SecurePass123!");
        assertEquals("User already exists", result);
    }

    @Test
    void testAuthenticateUser_Success() {
        User user = new User();
        user.setUserName("testuser");
        user.setPassword("hashed_password");

        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("SecurePass123!", "hashed_password")).thenReturn(true);

        boolean authenticated = userService.authenticateUser("testuser", "SecurePass123!");
        assertTrue(authenticated);
    }

    @Test
    void testAuthenticateUser_Fail_WrongPassword() {
        User user = new User();
        user.setUserName("testuser");
        user.setPassword("hashed_password");

        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPass", "hashed_password")).thenReturn(false);

        boolean authenticated = userService.authenticateUser("testuser", "WrongPass");
        assertFalse(authenticated);
    }

    @Test
    void testFindUserByUserName_Success() {
        User user = new User();
        user.setUserName("testuser");

        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(user));

        User foundUser = userService.findByUserName("testuser");
        assertEquals("testuser", foundUser.getUserName());
    }

    @Test
    void testFindUserByUserName_Fail_UserNotFound() {
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.empty());
        assertThrows(UserServiceException.class, () -> userService.findByUserName("testuser"));
    }
}
