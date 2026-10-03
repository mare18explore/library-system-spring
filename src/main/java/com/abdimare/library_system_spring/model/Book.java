package com.abdimare.library_system_spring.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

// @Entity tells jpa this class is a database table, same idea as a DbSet in ef core
@Entity
public class Book {

    // auto-increment primary key, the database picks the next id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    private String isbn;

    // every new book starts out available, same default as the c# version
    private boolean available = true;

    public Long getId() { 
        return id; 
    }
    public String getTitle() { 
        return title; 
    }
    public void setTitle(String title) { 
        this.title = title; 
    }
    public String getAuthor() { 
        return author; 
    }
    public void setAuthor(String author) { 
        this.author = author; 
    }
    public String getIsbn() { 
        return isbn; 
    }
    public void setIsbn(String isbn) { 
        this.isbn = isbn; 
    }
    public boolean isAvailable() { 
        return available; 
    }
    public void setAvailable(boolean available) { 
        this.available = available; 
    }
}