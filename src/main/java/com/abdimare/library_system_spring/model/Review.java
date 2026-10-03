package com.abdimare.library_system_spring.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

// each review belongs to one book and one member, jpa stores them as book_id and member_id
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    // ratings only make sense from 1 to 5, validation rejects anything else
    @Min(1)
    @Max(5)
    private int rating;

    private String comment;

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
    public int getRating() { 
        return rating; 
    }
    public void setRating(int rating) { 
        this.rating = rating; 
    }
    public String getComment() { 
        return comment; 
    }
    public void setComment(String comment) { 
        this.comment = comment; 
    }
}