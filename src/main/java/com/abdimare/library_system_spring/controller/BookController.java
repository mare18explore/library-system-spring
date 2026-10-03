package com.abdimare.library_system_spring.controller;

import com.abdimare.library_system_spring.model.Book;
import com.abdimare.library_system_spring.repository.BookRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// @RestController means every method returns json, same idea as [ApiController] in asp.net core
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    // spring sees this constructor and passes in the repository automatically,
    // that's dependency injection
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // GET /api/books lists everything, GET /api/books?title=dune searches by title
    @GetMapping
    public List<Book> getBooks(@RequestParam(required = false) String title) {
        if (title != null && !title.isBlank()) {
            return bookRepository.findByTitleContainingIgnoreCase(title);
        }
        return bookRepository.findAll();
    }

    @GetMapping("/available")
    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailableTrue();
    }

    // returns 404 if the id doesn't exist instead of crashing
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // @Valid runs the @NotBlank checks on the entity, bad input gets a 400
    // automatically
    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        Book saved = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book updated) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(updated.getTitle());
                    book.setAuthor(updated.getAuthor());
                    book.setIsbn(updated.getIsbn());
                    book.setAvailable(updated.isAvailable());
                    return ResponseEntity.ok(bookRepository.save(book));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 204 means it worked and there's nothing to send back
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}