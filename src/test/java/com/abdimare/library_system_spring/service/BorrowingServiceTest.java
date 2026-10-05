package com.abdimare.library_system_spring.service;

import com.abdimare.library_system_spring.exception.ConflictException;
import com.abdimare.library_system_spring.exception.NotFoundException;
import com.abdimare.library_system_spring.model.Book;
import com.abdimare.library_system_spring.model.Borrowing;
import com.abdimare.library_system_spring.model.Member;
import com.abdimare.library_system_spring.repository.BookRepository;
import com.abdimare.library_system_spring.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// starts the real app with the h2 database, so we're testing the actual service and not a fake version
// @Transactional rolls back every test when it finishes, so each one starts with a clean database
@SpringBootTest
@Transactional
class BorrowingServiceTest {

    // @Autowired pulls the real beans out of the running app, same dependency injection the controllers use
    @Autowired
    private BorrowingService borrowingService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Book book;
    private Member member;

    // runs before every test, so each one gets a fresh available book and a member to borrow it
    @BeforeEach
    void setUp() {
        Book newBook = new Book();
        newBook.setTitle("Dune");
        newBook.setAuthor("Frank Herbert");
        book = bookRepository.save(newBook);

        // the hash doesn't matter here since these tests never log in
        Member newMember = new Member();
        newMember.setName("Abdi");
        newMember.setEmail("abdi@example.com");
        newMember.setPasswordHash("not-a-real-hash");
        member = memberRepository.save(newMember);
    }

    // the happy path, borrowing should save a record and flip the book to unavailable
    @Test
    void borrowingABookMarksItUnavailable() {
        Borrowing borrowing = borrowingService.borrow(book.getId(), member.getId());

        assertThat(borrowing.getId()).isNotNull();
        assertThat(borrowing.getReturnedAt()).isNull();
        // reload the book from the database to make sure the change was actually saved, not just set in memory
        assertThat(bookRepository.findById(book.getId()).orElseThrow().isAvailable()).isFalse();
    }

    // the main business rule, a book that's already out can't be borrowed again
    @Test
    void cannotBorrowABookThatIsAlreadyOut() {
        borrowingService.borrow(book.getId(), member.getId());

        // the second borrow should hit the rule in BorrowingService and throw a 409-style error
        assertThatThrownBy(() -> borrowingService.borrow(book.getId(), member.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already borrowed");
    }

    // returning should stamp a return time and put the book back on the shelf
    @Test
    void returningABookMakesItAvailableAgain() {
        Borrowing borrowing = borrowingService.borrow(book.getId(), member.getId());

        Borrowing returned = borrowingService.returnBook(borrowing.getId());

        assertThat(returned.getReturnedAt()).isNotNull();
        assertThat(bookRepository.findById(book.getId()).orElseThrow().isAvailable()).isTrue();
    }

    // a borrowing can only be closed once, a second return is a mistake and should be rejected
    @Test
    void cannotReturnTheSameBorrowingTwice() {
        Borrowing borrowing = borrowingService.borrow(book.getId(), member.getId());
        borrowingService.returnBook(borrowing.getId());

        assertThatThrownBy(() -> borrowingService.returnBook(borrowing.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already returned");
    }

    // bad ids should give a clear not found error instead of crashing with a null pointer
    @Test
    void borrowingABookThatDoesNotExistThrowsNotFound() {
        // 999 was never created, so this should be a 404-style error
        assertThatThrownBy(() -> borrowingService.borrow(999L, member.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}