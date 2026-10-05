package com.abdimare.library_system_spring.service;

import com.abdimare.library_system_spring.exception.ConflictException;
import com.abdimare.library_system_spring.exception.NotFoundException;
import com.abdimare.library_system_spring.model.Book;
import com.abdimare.library_system_spring.model.Borrowing;
import com.abdimare.library_system_spring.model.Member;
import com.abdimare.library_system_spring.repository.BookRepository;
import com.abdimare.library_system_spring.repository.BorrowingRepository;
import com.abdimare.library_system_spring.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

// the business rules for borrowing live here, the controller just passes requests in
@Service
public class BorrowingService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowingRepository borrowingRepository;

    public BorrowingService(BookRepository bookRepository, MemberRepository memberRepository,
                            BorrowingRepository borrowingRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.borrowingRepository = borrowingRepository;
    }

    // @Transactional means both saves happen together or not at all,
    // so a book can never end up marked unavailable without a borrowing record
    @Transactional
    public Borrowing borrow(Long bookId, Long memberId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("book " + bookId + " not found"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("member " + memberId + " not found"));

        if (!book.isAvailable()) {
            throw new ConflictException("book " + bookId + " is already borrowed");
        }

        Borrowing borrowing = new Borrowing();
        borrowing.setBook(book);
        borrowing.setMember(member);
        book.setAvailable(false);

        bookRepository.save(book);
        return borrowingRepository.save(borrowing);
    }

    @Transactional
    public Borrowing returnBook(Long borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new NotFoundException("borrowing " + borrowingId + " not found"));

        if (borrowing.getReturnedAt() != null) {
            throw new ConflictException("borrowing " + borrowingId + " was already returned");
        }

        borrowing.setReturnedAt(LocalDateTime.now());
        borrowing.getBook().setAvailable(true);

        bookRepository.save(borrowing.getBook());
        return borrowingRepository.save(borrowing);
    }

    public List<Borrowing> getBorrowings(Long memberId) {
        return memberId == null ? borrowingRepository.findAll() : borrowingRepository.findByMemberId(memberId);
    }
}