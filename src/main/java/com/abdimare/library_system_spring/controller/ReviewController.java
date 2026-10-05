package com.abdimare.library_system_spring.controller;

import com.abdimare.library_system_spring.dto.ReviewRequest;
import com.abdimare.library_system_spring.model.Review;
import com.abdimare.library_system_spring.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Review> addReview(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.addReview(request));
    }

    // GET /api/reviews?bookId=1 lists every review for that book
    @GetMapping
    public List<Review> getReviews(@RequestParam Long bookId) {
        return reviewService.getReviewsForBook(bookId);
    }
}