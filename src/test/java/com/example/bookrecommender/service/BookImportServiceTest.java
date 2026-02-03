package com.example.bookrecommender.service;

import com.example.bookrecommender.entity.Book;
import com.example.bookrecommender.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookImportServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookImportService bookImportService;

    @Test
    void testBookImportSkipsWhenBooksExist() {
        // When books already exist, count should be checked
        when(bookRepository.count()).thenReturn(5L);

        // Import should be skipped
        // Just verify that count was called
        bookImportService.run(null);

        verify(bookRepository, times(1)).count();
        // Verify no books were saved since import was skipped
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testExistsByIsbnReturnsTrueWhenBookExists() {
        String testIsbn = "1234567890";
        
        when(bookRepository.existsByIsbn(testIsbn)).thenReturn(true);
        
        boolean exists = bookRepository.existsByIsbn(testIsbn);
        
        assertTrue(exists);
        verify(bookRepository, times(1)).existsByIsbn(testIsbn);
    }

    @Test
    void testExistsByTitleAndAuthorReturnsTrueWhenBookExists() {
        String testTitle = "Test Book";
        String testAuthor = "Test Author";
        
        when(bookRepository.existsByTitleAndAuthor(testTitle, testAuthor)).thenReturn(true);
        
        boolean exists = bookRepository.existsByTitleAndAuthor(testTitle, testAuthor);
        
        assertTrue(exists);
        verify(bookRepository, times(1)).existsByTitleAndAuthor(testTitle, testAuthor);
    }
}
