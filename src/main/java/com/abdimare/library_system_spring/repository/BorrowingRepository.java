package com.abdimare.library_system_spring.repository;

import com.abdimare.library_system_spring.model.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    // "MemberId" reaches through the member relationship to its id, no manual join needed
    List<Borrowing> findByMemberId(Long memberId);

    // a borrowing with no return date means the book is still checked out
    Optional<Borrowing> findByBookIdAndReturnedAtIsNull(Long bookId);

       // used to check that a member actually borrowed a book before letting them review it
    boolean existsByBookIdAndMemberId(Long bookId, Long memberId);
}