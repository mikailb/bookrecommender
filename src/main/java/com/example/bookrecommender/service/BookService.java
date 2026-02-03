package com.example.bookrecommender.service;

import com.example.bookrecommender.dto.BookRequest;
import com.example.bookrecommender.dto.BookResponse;
import com.example.bookrecommender.dto.ReviewDTO;
import com.example.bookrecommender.entity.Book;
import com.example.bookrecommender.entity.UserBook;
import com.example.bookrecommender.exception.ResourceNotFoundException;
import com.example.bookrecommender.repository.BookRepository;
import com.example.bookrecommender.repository.UserBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserBookRepository userBookRepository;
    
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(this::convertToResponse);
    }
    
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        
        // Get favorite count
        Integer favoriteCount = userBookRepository.countFavoritesByBookId(id);
        
        // Get reviews
        List<UserBook> userBooks = userBookRepository.findReviewsByBookId(id);
        List<ReviewDTO> reviews = userBooks.stream()
                .map(ub -> ReviewDTO.builder()
                        .id(ub.getId())
                        .userName(ub.getUser().getName())
                        .rating(ub.getRating())
                        .ratedAt(ub.getReadAt())
                        .build())
                .collect(Collectors.toList());
        
        BookResponse response = convertToResponse(book);
        response.setFavoriteCount(favoriteCount);
        response.setReviews(reviews);
        
        return response;
    }
    
    @Transactional
    public BookResponse createBook(BookRequest request) {
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .genre(request.getGenre())
                .description(request.getDescription())
                .coverImageUrl(request.getCoverImageUrl())
                .publishYear(request.getPublishYear())
                .build();
        
        Book savedBook = bookRepository.save(book);
        return convertToResponse(savedBook);
    }
    
    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setGenre(request.getGenre());
        book.setDescription(request.getDescription());
        book.setCoverImageUrl(request.getCoverImageUrl());
        book.setPublishYear(request.getPublishYear());
        
        Book updatedBook = bookRepository.save(book);
        return convertToResponse(updatedBook);
    }
    
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
    
    public Page<BookResponse> searchBooks(String query, Pageable pageable) {
        return bookRepository.searchBooks(query, pageable)
                .map(this::convertToResponse);
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
