package com.example.bookrecommender.service;

import com.example.bookrecommender.dto.BookRequest;
import com.example.bookrecommender.dto.BookResponse;
import com.example.bookrecommender.entity.Book;
import com.example.bookrecommender.exception.ResourceNotFoundException;
import com.example.bookrecommender.repository.BookRepository;
import com.example.bookrecommender.repository.UserBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    
    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private UserBookRepository userBookRepository;
    
    @InjectMocks
    private BookService bookService;
    
    private Book book;
    private BookRequest bookRequest;
    private Pageable pageable;
    
    @BeforeEach
    void setUp() {
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
        
        bookRequest = BookRequest.builder()
                .title("Test Book")
                .author("Test Author")
                .isbn("1234567890")
                .genre("Fiction")
                .description("Test description")
                .build();
        
        pageable = PageRequest.of(0, 10);
    }
    
    @Test
    void testGetAllBooks() {
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        
        Page<BookResponse> result = bookService.getAllBooks(pageable);
        
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Book", result.getContent().get(0).getTitle());
        
        verify(bookRepository).findAll(pageable);
    }
    
    @Test
    void testGetBookByIdSuccess() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userBookRepository.countFavoritesByBookId(1L)).thenReturn(0);
        when(userBookRepository.findReviewsByBookId(1L)).thenReturn(new ArrayList<>());
        
        BookResponse result = bookService.getBookById(1L);
        
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        assertNotNull(result.getFavoriteCount());
        assertNotNull(result.getReviews());
        
        verify(bookRepository).findById(1L);
        verify(userBookRepository).countFavoritesByBookId(1L);
        verify(userBookRepository).findReviewsByBookId(1L);
    }
    
    @Test
    void testGetBookByIdNotFound() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.getBookById(1L);
        });
        
        verify(bookRepository).findById(1L);
    }
    
    @Test
    void testCreateBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        
        BookResponse result = bookService.createBook(bookRequest);
        
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        
        verify(bookRepository).save(any(Book.class));
    }
    
    @Test
    void testUpdateBookSuccess() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        
        BookResponse result = bookService.updateBook(1L, bookRequest);
        
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }
    
    @Test
    void testUpdateBookNotFound() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.updateBook(1L, bookRequest);
        });
        
        verify(bookRepository).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }
    
    @Test
    void testDeleteBookSuccess() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);
        
        assertDoesNotThrow(() -> bookService.deleteBook(1L));
        
        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
    }
    
    @Test
    void testDeleteBookNotFound() {
        when(bookRepository.existsById(anyLong())).thenReturn(false);
        
        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.deleteBook(1L);
        });
        
        verify(bookRepository).existsById(1L);
        verify(bookRepository, never()).deleteById(anyLong());
    }
    
    @Test
    void testSearchBooks() {
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        
        when(bookRepository.searchBooks("Test", pageable)).thenReturn(bookPage);
        
        Page<BookResponse> result = bookService.searchBooks("Test", pageable);
        
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Book", result.getContent().get(0).getTitle());
        
        verify(bookRepository).searchBooks("Test", pageable);
    }
}
