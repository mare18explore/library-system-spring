package com.abdimare.library_system_spring.service;

import com.abdimare.library_system_spring.dto.ReviewRequest;
import com.abdimare.library_system_spring.exception.ConflictException;
import com.abdimare.library_system_spring.exception.NotFoundException;
import com.abdimare.library_system_spring.model.Book;
import com.abdimare.library_system_spring.model.Member;
import com.abdimare.library_system_spring.model.Review;
import com.abdimare.library_system_spring.repository.BookRepository;
import com.abdimare.library_system_spring.repository.BorrowingRepository;
import com.abdimare.library_system_spring.repository.MemberRepository;
import com.abdimare.library_system_spring.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import java.util.List;

// the rules for reviews live here, same idea as BorrowingService
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    // needed to check borrowing history before allowing a review
    private final BorrowingRepository borrowingRepository;

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository,
                         MemberRepository memberRepository, BorrowingRepository borrowingRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.borrowingRepository = borrowingRepository;
    }

    public Review addReview(ReviewRequest request) {
        // look up both first, a missing book or member turns into a clear 404 through the exception handler
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new NotFoundException("book " + request.bookId() + " not found"));
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NotFoundException("member " + request.memberId() + " not found"));

        // only people who actually borrowed the book can review it, keeps reviews honest
        if (!borrowingRepository.existsByBookIdAndMemberId(book.getId(), member.getId())) {
            throw new ConflictException("member " + member.getId() + " hasn't borrowed book " + book.getId());
        }

        // the 1 to 5 rating check already happened in ReviewRequest, so the values here are safe to save
        Review review = new Review();
        review.setBook(book);
        review.setMember(member);
        review.setRating(request.rating());
        review.setComment(request.comment());
        return reviewRepository.save(review);
    }

    // returns an empty list instead of an error if the book has no reviews yet
    public List<Review> getReviewsForBook(Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }
}