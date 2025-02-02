package com.saracveysel.security.users.service;

import com.saracveysel.security.users.model.Books;
import com.saracveysel.security.users.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBooksByUserId_Success() {
        when(bookRepository.findByUserId(1L)).thenReturn(List.of(new Books()));
        List<Books> books = bookService.getBooksByUserId(1L);
        assertFalse(books.isEmpty());
    }

    @Test
    void testGetBookById_Success() {
        Books book = new Books();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Books> foundBook = bookService.getBookById(1L);
        assertTrue(foundBook.isPresent());
        assertEquals(1L, foundBook.get().getId());
    }

    @Test
    void testSaveBook_Success() {
        Books book = new Books();
        when(bookRepository.save(book)).thenReturn(book);

        Books savedBook = bookService.saveBook(book);
        assertNotNull(savedBook);
    }

    @Test
    void testDeleteBook_Success() {
        doNothing().when(bookRepository).deleteById(1L);
        bookService.deleteBook(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }
}
