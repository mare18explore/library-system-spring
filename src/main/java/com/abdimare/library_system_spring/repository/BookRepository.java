package com.abdimare.library_system_spring.repository;

import com.abdimare.library_system_spring.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// spring data writes the implementation for us at startup, we only declare what we need
public interface BookRepository extends JpaRepository<Book, Long> {

    // spring builds the query from the method name, this becomes a case-insensitive LIKE search
    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAvailableTrue();
}