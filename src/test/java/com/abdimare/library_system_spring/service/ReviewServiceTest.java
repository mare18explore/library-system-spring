package com.abdimare.library_system_spring.service;

import com.abdimare.library_system_spring.dto.ReviewRequest;
import com.abdimare.library_system_spring.exception.ConflictException;
import com.abdimare.library_system_spring.model.Book;
import com.abdimare.library_system_spring.model.Member;
import com.abdimare.library_system_spring.model.Review;
import com.abdimare.library_system_spring.repository.BookRepository;
import com.abdimare.library_system_spring.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// same setup as BorrowingServiceTest, real app and database, rolled back after every test
@SpringBootTest
@Transactional
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    // needed so a test can borrow the book first, since reviews require a past borrowing
    @Autowired
    private BorrowingService borrowingService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Book book;
    private Member member;

    // fresh book and member before each test, nothing has been borrowed yet
    @BeforeEach
    void setUp() {
        Book newBook = new Book();
        newBook.setTitle("Dune");
        newBook.setAuthor("Frank Herbert");
        book = bookRepository.save(newBook);

        Member newMember = new Member();
        newMember.setName("Abdi");
        newMember.setEmail("abdi@example.com");
        newMember.setPasswordHash("not-a-real-hash");
        member = memberRepository.save(newMember);
    }

    // the borrowed-it-first rule, reviewing without any borrowing history should be blocked
    @Test
    void cannotReviewABookYouNeverBorrowed() {
        ReviewRequest request = new ReviewRequest(book.getId(), member.getId(), 5, "A classic");

        assertThatThrownBy(() -> reviewService.addReview(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("hasn't borrowed");
    }

    // once the member has borrowed the book, the same review should go through
    @Test
    void canReviewABookAfterBorrowingIt() {
        borrowingService.borrow(book.getId(), member.getId());

        Review review = reviewService.addReview(new ReviewRequest(book.getId(), member.getId(), 5, "A classic"));

        assertThat(review.getId()).isNotNull();
        // check it actually shows up when listing reviews, not just that save returned something
        assertThat(reviewService.getReviewsForBook(book.getId())).hasSize(1);
    }
}