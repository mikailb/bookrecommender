package com.example.bookrecommender.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBookResponse {
    private Long id;
    private BookResponse book;
    private Integer rating;
    private LocalDateTime readAt;
    private Boolean isFavorite;
}
