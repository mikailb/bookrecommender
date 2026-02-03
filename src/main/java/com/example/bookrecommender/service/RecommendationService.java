package com.example.bookrecommender.service;

import com.example.bookrecommender.dto.BookResponse;
import com.example.bookrecommender.entity.Book;
import com.example.bookrecommender.entity.User;
import com.example.bookrecommender.repository.BookRepository;
import com.example.bookrecommender.repository.UserBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    
    @Autowired
    private UserBookRepository userBookRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    public List<BookResponse> getRecommendations(User user) {
        // Get user's favorite genres and authors based on highly rated books
        List<String> favoriteGenres = userBookRepository.findFavoriteGenresByUserId(user.getId());
        List<String> favoriteAuthors = userBookRepository.findFavoriteAuthorsByUserId(user.getId());
        
        // Get books user has already read
        Set<Long> readBookIds = userBookRepository.findByUserId(user.getId()).stream()
                .map(ub -> ub.getBook().getId())
                .collect(Collectors.toSet());
        
        // Find books by favorite genres and authors that user hasn't read
        List<Book> recommendedBooks = new ArrayList<>();
        
        if (!favoriteGenres.isEmpty()) {
            List<Book> genreBooks = bookRepository.findByGenreIn(favoriteGenres);
            recommendedBooks.addAll(genreBooks);
        }
        
        if (!favoriteAuthors.isEmpty()) {
            List<Book> authorBooks = bookRepository.findByAuthorIn(favoriteAuthors);
            recommendedBooks.addAll(authorBooks);
        }
        
        // If no preferences found, return top-rated books
        if (recommendedBooks.isEmpty()) {
            recommendedBooks = bookRepository.findAll();
        }
        
        // Filter out books user has already read and score by relevance
        Map<Long, Double> bookScores = new HashMap<>();
        
        for (Book book : recommendedBooks) {
            if (readBookIds.contains(book.getId())) {
                continue;
            }
            
            double score = 0.0;
            
            // Score based on genre match
            if (favoriteGenres.contains(book.getGenre())) {
                score += 2.0;
            }
            
            // Score based on author match
            if (favoriteAuthors.contains(book.getAuthor())) {
                score += 2.0;
            }
            
            // Add book's average rating to score
            if (book.getAverageRating() != null) {
                score += book.getAverageRating();
            }
            
            bookScores.put(book.getId(), score);
        }
        
        // Sort books by score and return top 10
        List<Long> topBookIds = bookScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        // Fetch all books in a single query to avoid N+1 problem
        List<Book> topBooks = bookRepository.findAllById(topBookIds);
        
        // Maintain the score-based order
        Map<Long, Book> bookMap = topBooks.stream()
                .collect(Collectors.toMap(Book::getId, book -> book));
        
        return topBookIds.stream()
                .map(bookMap::get)
                .filter(Objects::nonNull)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private BookResponse convertToResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .genre(book.getGenre())
                .description(book.getDescription())
                .coverImageUrl(book.getCoverImageUrl())
                .publishYear(book.getPublishYear())
                .averageRating(book.getAverageRating())
                .createdAt(book.getCreatedAt())
                .build();
    }
}
