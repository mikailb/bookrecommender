package com.example.bookrecommender.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private String description;
    private String coverImageUrl;
    private Integer publishYear;
    private Double averageRating;
    private LocalDateTime createdAt;
    private Integer favoriteCount;
    private List<ReviewDTO> reviews;
}
