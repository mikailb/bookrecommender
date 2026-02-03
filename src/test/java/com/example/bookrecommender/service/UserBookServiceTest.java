package com.example.bookrecommender.service;

import com.example.bookrecommender.dto.RatingRequest;
import com.example.bookrecommender.dto.UserBookResponse;
import com.example.bookrecommender.entity.Book;
import com.example.bookrecommender.entity.User;
import com.example.bookrecommender.entity.UserBook;
import com.example.bookrecommender.exception.DuplicateResourceException;
import com.example.bookrecommender.exception.ResourceNotFoundException;
import com.example.bookrecommender.repository.BookRepository;
import com.example.bookrecommender.repository.UserBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserBookServiceTest {
    
    @Mock
    private UserBookRepository userBookRepository;
    
    @Mock
    private BookRepository bookRepository;
    
    @InjectMocks
    private UserBookService userBookService;
    
    private User user;
    private Book book;
    private UserBook userBook;
    
    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build();
        
        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .isbn("1234567890")
                .genre("Fiction")
                .description("Test description")
                .averageRating(4.5)
                .createdAt(LocalDateTime.now())
                .build();
        
        userBook = UserBook.builder()
                .id(1L)
                .user(user)
                .book(book)
                .rating(4)
                .readAt(LocalDateTime.now())
                .isFavorite(false)
                .build();
    }
    
    @Test
    void testGetUserBooks() {
        when(userBookRepository.findByUserId(1L)).thenReturn(List.of(userBook));
        
        List<UserBookResponse> result = userBookService.getUserBooks(user);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getBook().getTitle());
        
        verify(userBookRepository).findByUserId(1L);
    }
    
    @Test
    void testAddBookToUserSuccess() {
        when(userBookRepository.existsByUserIdAndBookId(1L, 1L)).thenReturn(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userBookRepository.save(any(UserBook.class))).thenReturn(userBook);
        
        UserBookResponse result = userBookService.addBookToUser(user, 1L);
        
        assertNotNull(result);
        assertEquals("Test Book", result.getBook().getTitle());
        
        verify(userBookRepository).existsByUserIdAndBookId(1L, 1L);
        verify(bookRepository).findById(1L);
        verify(userBookRepository).save(any(UserBook.class));
    }
    
    @Test
    void testAddBookToUserDuplicate() {
        when(userBookRepository.existsByUserIdAndBookId(1L, 1L)).thenReturn(true);
        
        assertThrows(DuplicateResourceException.class, () -> {
            userBookService.addBookToUser(user, 1L);
        });
        
        verify(userBookRepository).existsByUserIdAndBookId(1L, 1L);
        verify(bookRepository, never()).findById(anyLong());
        verify(userBookRepository, never()).save(any(UserBook.class));
    }
    
    @Test
    void testAddBookToUserBookNotFound() {
        when(userBookRepository.existsByUserIdAndBookId(1L, 1L)).thenReturn(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            userBookService.addBookToUser(user, 1L);
        });
        
        verify(bookRepository).findById(1L);
        verify(userBookRepository, never()).save(any(UserBook.class));
    }
    
    @Test
    void testRateBookSuccess() {
        RatingRequest ratingRequest = RatingRequest.builder().rating(5).build();
        book.setUserBooks(List.of(userBook));
        
        when(userBookRepository.findByUserIdAndBookId(1L, 1L)).thenReturn(Optional.of(userBook));
        when(userBookRepository.save(any(UserBook.class))).thenReturn(userBook);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        
        UserBookResponse result = userBookService.rateBook(user, 1L, ratingRequest);
        
        assertNotNull(result);
        assertEquals("Test Book", result.getBook().getTitle());
        
        verify(userBookRepository).findByUserIdAndBookId(1L, 1L);
        verify(userBookRepository).save(any(UserBook.class));
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }
    
    @Test
    void testRateBookNotInUserList() {
        RatingRequest ratingRequest = RatingRequest.builder().rating(5).build();
        
        when(userBookRepository.findByUserIdAndBookId(1L, 1L)).thenReturn(Optional.empty());
        
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userBookService.rateBook(user, 1L, ratingRequest);
        });
        
        assertTrue(exception.getMessage().contains("not in user's reading list"));
        
        verify(userBookRepository).findByUserIdAndBookId(1L, 1L);
        verify(userBookRepository, never()).save(any(UserBook.class));
    }
    
    @Test
    void testRemoveBookFromUserSuccess() {
        when(userBookRepository.findByUserIdAndBookId(1L, 1L)).thenReturn(Optional.of(userBook));
        book.setUserBooks(List.of());
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        
        userBookService.removeBookFromUser(user, 1L);
        
        verify(userBookRepository).findByUserIdAndBookId(1L, 1L);
        verify(userBookRepository).delete(userBook);
        verify(bookRepository).findById(1L);
    }
    
    @Test
    void testRemoveBookFromUserNotFound() {
        when(userBookRepository.findByUserIdAndBookId(1L, 1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            userBookService.removeBookFromUser(user, 1L);
        });
        
        verify(userBookRepository).findByUserIdAndBookId(1L, 1L);
        verify(userBookRepository, never()).delete(any(UserBook.class));
    }
    
    @Test
    void testRemoveRatingSuccess() {
        RatingRequest ratingRequest = RatingRequest.builder().rating(null).build();
        book.setUserBooks(List.of(userBook));
        
        when(userBookRepository.findByUserIdAndBookId(1L, 1L)).thenReturn(Optional.of(userBook));
        when(userBookRepository.save(any(UserBook.class))).thenReturn(userBook);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        
        UserBookResponse result = userBookService.removeRating(user, 1L);
        
        assertNotNull(result);
        verify(userBookRepository).findByUserIdAndBookId(1L, 1L);
        verify(userBookRepository).save(any(UserBook.class));
    }
    
    @Test
    void testRemoveRatingNotFound() {
        when(userBookRepository.findByUserIdAndBookId(1L, 1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            userBookService.removeRating(user, 1L);
        });
        
        verify(userBookRepository).findByUserIdAndBookId(1L, 1L);
        verify(userBookRepository, never()).save(any(UserBook.class));
    }
}
