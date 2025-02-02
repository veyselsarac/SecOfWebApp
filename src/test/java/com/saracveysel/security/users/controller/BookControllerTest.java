package com.saracveysel.security.users.controller;

import com.saracveysel.security.users.model.Books;
import com.saracveysel.security.users.model.User;
import com.saracveysel.security.users.service.BookService;
import com.saracveysel.security.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetBooks_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUserName("testuser");

        when(userService.findByUserName("testuser")).thenReturn(user);
        when(bookService.getBooksByUserId(1L)).thenReturn(List.of(new Books()));

        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBooks_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAddBook_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUserName("testuser");

        Books book = new Books();
        book.setId(1L);
        book.setTitle("Spring Boot Guide");
        book.setAuthor("John Doe");
        book.setPublishedYear(2024);
        book.setUser(user);

        when(userService.findByUserName("testuser")).thenReturn(user);
        when(bookService.saveBook(any(Books.class))).thenReturn(book);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Spring Boot Guide\", \"author\": \"John Doe\", \"publishedYear\": 2024}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testDeleteBook_Success() throws Exception {
        User user = new User();
        user.setId(1L);

        Books book = new Books();
        book.setId(1L);
        book.setUser(user);

        when(userService.findByUserName("testuser")).thenReturn(user);
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testDeleteBook_NotFound() throws Exception {
        when(userService.findByUserName("testuser")).thenReturn(new User());
        when(bookService.getBookById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isForbidden());
    }
}
