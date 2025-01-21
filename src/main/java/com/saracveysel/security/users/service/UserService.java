package com.saracveysel.security.users.service;

import com.saracveysel.security.users.exception.ExceptionMessages;
import com.saracveysel.security.users.exception.UserServiceException;
import com.saracveysel.security.users.model.User;
import com.saracveysel.security.users.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String createUser(String username, String email, String password) {
        if (userRepository.existsByUserName(username)) {
            return "User already exists";
        }

        User user = new User();
        user.setUserName(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER");

        userRepository.save(user);
        return "User created";

    }

    public User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UserServiceException(ExceptionMessages.USER_NOT_FOUND.getMessage()));
    }

    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new UserServiceException(ExceptionMessages.USER_NOT_FOUND.getMessage()));
    }

    public boolean authenticateUser(String username, String password) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UserServiceException(ExceptionMessages.USER_NOT_FOUND.getMessage()));
        return passwordEncoder.matches(password, user.getPassword());
    }
}
