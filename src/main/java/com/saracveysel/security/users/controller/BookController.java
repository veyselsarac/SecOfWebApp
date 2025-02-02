package com.saracveysel.security.users.controller;

import com.saracveysel.security.users.model.Books;
import com.saracveysel.security.users.model.User;
import com.saracveysel.security.users.service.BookService;
import com.saracveysel.security.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final UserService userService;

    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Books>> getUserBooks(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUserName(userDetails.getUsername());
        return ResponseEntity.ok(bookService.getBooksByUserId(user.getId()));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Books> addBook(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Books book) {
        User user = userService.findByUserName(userDetails.getUsername());
        book.setUser(user);
        return ResponseEntity.ok(bookService.saveBook(book));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Books> updateBook(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody Books bookDetails) {

        User user = userService.findByUserName(userDetails.getUsername());
        Long userId = user.getId();

        Optional<Books> existingBook = bookService.getBookById(id);
        if (existingBook.isEmpty() || !existingBook.get().getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        Books book = existingBook.get();
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setPublishedYear(bookDetails.getPublishedYear());

        return ResponseEntity.ok(bookService.saveBook(book));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteBook(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        User user = userService.findByUserName(userDetails.getUsername());
        Long userId = user.getId();

        Optional<Books> book = bookService.getBookById(id);
        if (book.isEmpty() || !book.get().getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
