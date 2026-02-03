package com.example.bookrecommender.service;

import com.example.bookrecommender.dto.BookResponse;
import com.example.bookrecommender.entity.Book;
import com.example.bookrecommender.entity.User;
import com.example.bookrecommender.entity.UserBook;
import com.example.bookrecommender.repository.BookRepository;
import com.example.bookrecommender.repository.UserBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    
    @Mock
    private UserBookRepository userBookRepository;
    
    @Mock
    private BookRepository bookRepository;
    
    @InjectMocks
    private RecommendationService recommendationService;
    
    private User user;
    private Book book1;
    private Book book2;
    private Book book3;
    private UserBook userBook1;
    
    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build();
        
        book1 = Book.builder()
                .id(1L)
                .title("Read Book")
                .author("Author A")
                .genre("Fiction")
                .averageRating(4.5)
                .createdAt(LocalDateTime.now())
                .build();
        
        book2 = Book.builder()
                .id(2L)
                .title("Recommended Book 1")
                .author("Author A")
                .genre("Fiction")
                .averageRating(4.8)
                .createdAt(LocalDateTime.now())
                .build();
        
        book3 = Book.builder()
                .id(3L)
                .title("Recommended Book 2")
                .author("Author B")
                .genre("Non-Fiction")
                .averageRating(4.2)
                .createdAt(LocalDateTime.now())
                .build();
        
        userBook1 = UserBook.builder()
                .id(1L)
                .user(user)
                .book(book1)
                .rating(5)
                .readAt(LocalDateTime.now())
                .isFavorite(true)
                .build();
    }
    
    @Test
    void testGetRecommendationsWithPreferences() {
        when(userBookRepository.findFavoriteGenresByUserId(1L)).thenReturn(List.of("Fiction"));
        when(userBookRepository.findFavoriteAuthorsByUserId(1L)).thenReturn(List.of("Author A"));
        when(userBookRepository.findByUserId(1L)).thenReturn(List.of(userBook1));
        when(bookRepository.findByGenreIn(List.of("Fiction"))).thenReturn(List.of(book1, book2));
        when(bookRepository.findByAuthorIn(List.of("Author A"))).thenReturn(List.of(book1, book2));
        when(bookRepository.findAllById(List.of(2L))).thenReturn(List.of(book2));
        
        List<BookResponse> recommendations = recommendationService.getRecommendations(user);
        
        assertNotNull(recommendations);
        // Should recommend book2 (not yet read), but exclude book1 (already read)
        assertEquals(1, recommendations.size());
        assertEquals(book2.getId(), recommendations.get(0).getId());
        
        verify(userBookRepository).findFavoriteGenresByUserId(1L);
        verify(userBookRepository).findFavoriteAuthorsByUserId(1L);
        verify(bookRepository).findByGenreIn(List.of("Fiction"));
        verify(bookRepository).findByAuthorIn(List.of("Author A"));
        verify(bookRepository).findAllById(List.of(2L));
    }
    
    @Test
    void testGetRecommendationsNoPreferences() {
        when(userBookRepository.findFavoriteGenresByUserId(1L)).thenReturn(new ArrayList<>());
        when(userBookRepository.findFavoriteAuthorsByUserId(1L)).thenReturn(new ArrayList<>());
        when(userBookRepository.findByUserId(1L)).thenReturn(List.of(userBook1));
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2, book3));
        
        List<BookResponse> recommendations = recommendationService.getRecommendations(user);
        
        assertNotNull(recommendations);
        
        verify(userBookRepository).findFavoriteGenresByUserId(1L);
        verify(userBookRepository).findFavoriteAuthorsByUserId(1L);
        verify(bookRepository).findAll();
    }
    
    @Test
    void testGetRecommendationsExcludesReadBooks() {
        when(userBookRepository.findFavoriteGenresByUserId(1L)).thenReturn(List.of("Fiction"));
        when(userBookRepository.findFavoriteAuthorsByUserId(1L)).thenReturn(List.of("Author A"));
        when(userBookRepository.findByUserId(1L)).thenReturn(List.of(userBook1));
        when(bookRepository.findByGenreIn(List.of("Fiction"))).thenReturn(List.of(book1, book2));
        when(bookRepository.findByAuthorIn(List.of("Author A"))).thenReturn(List.of(book1, book2));
        
        List<BookResponse> recommendations = recommendationService.getRecommendations(user);
        
        assertNotNull(recommendations);
        // Should not contain book1 (id=1) as it's already read
        assertTrue(recommendations.stream().noneMatch(b -> b.getId().equals(1L)));
        
        verify(userBookRepository).findByUserId(1L);
    }
    
    @Test
    void testConvertToResponseIncludesAllFields() {
        // Create a book with coverImageUrl and publishYear
        Book bookWithAllFields = Book.builder()
                .id(4L)
                .title("Test Book")
                .author("Test Author")
                .isbn("1234567890")
                .genre("Fantasy")
                .description("A test description")
                .coverImageUrl("https://example.com/cover.jpg")
                .publishYear(2024)
                .averageRating(4.5)
                .createdAt(LocalDateTime.now())
                .build();
        
        when(userBookRepository.findFavoriteGenresByUserId(1L)).thenReturn(List.of("Fantasy"));
        when(userBookRepository.findFavoriteAuthorsByUserId(1L)).thenReturn(new ArrayList<>());
        when(userBookRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        when(bookRepository.findByGenreIn(List.of("Fantasy"))).thenReturn(List.of(bookWithAllFields));
        when(bookRepository.findAllById(List.of(4L))).thenReturn(List.of(bookWithAllFields));
        
        List<BookResponse> recommendations = recommendationService.getRecommendations(user);
        
        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
        
        BookResponse response = recommendations.get(0);
        assertEquals(bookWithAllFields.getId(), response.getId());
        assertEquals(bookWithAllFields.getTitle(), response.getTitle());
        assertEquals(bookWithAllFields.getAuthor(), response.getAuthor());
        assertEquals(bookWithAllFields.getIsbn(), response.getIsbn());
        assertEquals(bookWithAllFields.getGenre(), response.getGenre());
        assertEquals(bookWithAllFields.getDescription(), response.getDescription());
        assertEquals(bookWithAllFields.getCoverImageUrl(), response.getCoverImageUrl());
        assertEquals(bookWithAllFields.getPublishYear(), response.getPublishYear());
        assertEquals(bookWithAllFields.getAverageRating(), response.getAverageRating());
        assertEquals(bookWithAllFields.getCreatedAt(), response.getCreatedAt());
    }
}
