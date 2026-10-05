package com.abdimare.library_system_spring.controller;

import com.abdimare.library_system_spring.dto.BorrowRequest;
import com.abdimare.library_system_spring.model.Borrowing;
import com.abdimare.library_system_spring.service.BorrowingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// thin controller on purpose, all the rules live in BorrowingService
@RestController
@RequestMapping("/api/borrowings")
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PostMapping
    public ResponseEntity<Borrowing> borrow(@Valid @RequestBody BorrowRequest request) {
        Borrowing borrowing = borrowingService.borrow(request.bookId(), request.memberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowing);
    }

    @PostMapping("/{id}/return")
    public Borrowing returnBook(@PathVariable Long id) {
        return borrowingService.returnBook(id);
    }

    // GET /api/borrowings for everything, or ?memberId=1 for one member's history
    @GetMapping
    public List<Borrowing> getBorrowings(@RequestParam(required = false) Long memberId) {
        return borrowingService.getBorrowings(memberId);
    }
}