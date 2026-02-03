package com.example.bookrecommender.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "books")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String author;
    
    @Column(unique = true)
    private String isbn;
    
    @Column(nullable = false)
    private String genre;
    
    @Column(length = 2000)
    private String description;
    
    @Column
    private String coverImageUrl;
    
    @Column
    private Integer publishYear;
    
    @Column
    private Double averageRating;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserBook> userBooks;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (averageRating == null) {
            averageRating = 0.0;
        }
    }
}
