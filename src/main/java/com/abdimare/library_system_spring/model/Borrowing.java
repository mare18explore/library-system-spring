package com.abdimare.library_system_spring.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // instead of storing a raw book id like the c# version, jpa links the actual book
    // the database still gets a book_id foreign key column under the hood
    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime borrowedAt = LocalDateTime.now();

    // stays null until the book comes back, same as DateTime? in c#
    private LocalDateTime returnedAt;

    public Long getId() { 
        return id; 
    }
    public Book getBook() { 
        return book; 
    }
    public void setBook(Book book) { 
        this.book = book; 
    }
    public Member getMember() { 
        return member; 
    }
    public void setMember(Member member) { 
        this.member = member; 
    }
    public LocalDateTime getBorrowedAt() { 
        return borrowedAt; 
    }
    public LocalDateTime getReturnedAt() { 
        return returnedAt; 
    }
    public void setReturnedAt(LocalDateTime returnedAt) { 
        this.returnedAt = returnedAt; 
    }
}