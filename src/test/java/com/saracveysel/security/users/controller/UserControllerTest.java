package com.saracveysel.security.users.controller;

import com.saracveysel.security.users.model.User;
import com.saracveysel.security.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testCreateUser_Success() throws Exception {
        when(userService.isValidPassword(anyString())).thenReturn(true);
        when(userService.createUser(anyString(), anyString(), anyString())).thenReturn("User created");

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\": \"testuser\", \"email\": \"test@example.com\", \"password\": \"StrongPass123!\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateUser_Fail_InvalidPassword() throws Exception {
        when(userService.isValidPassword(anyString())).thenReturn(false);

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\": \"testuser\", \"email\": \"test@example.com\", \"password\": \"weak\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAuthenticatedUser_Success() throws Exception {
        User user = new User();
        user.setUserName("testuser");

        when(userService.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(get("/me"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAuthenticatedUser_Unauthorized() throws Exception {
        mockMvc.perform(get("/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetUserByUsername_Success() throws Exception {
        User user = new User();
        user.setUserName("testuser");

        when(userService.findByUserName("testuser")).thenReturn(user);

        mockMvc.perform(get("/testuser"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetUserByUsername_NotFound() throws Exception {
        when(userService.findByUserName("testuser")).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/testuser"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testLogin_Success() throws Exception {
        when(userService.findByUserName("testuser")).thenReturn(new User());
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\": \"testuser\", \"password\": \"StrongPass123!\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testLogin_Fail_InvalidCredentials() throws Exception {
        when(userService.findByUserName("testuser")).thenReturn(new User());
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\": \"testuser\", \"password\": \"wrongpass\"}"))
                .andExpect(status().isUnauthorized());
    }
}
