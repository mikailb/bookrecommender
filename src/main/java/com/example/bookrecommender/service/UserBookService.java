package com.example.bookrecommender.service;

import com.example.bookrecommender.dto.BookResponse;
import com.example.bookrecommender.dto.RatingRequest;
import com.example.bookrecommender.dto.UserBookResponse;
import com.example.bookrecommender.entity.Book;
import com.example.bookrecommender.entity.User;
import com.example.bookrecommender.entity.UserBook;
import com.example.bookrecommender.exception.DuplicateResourceException;
import com.example.bookrecommender.exception.ResourceNotFoundException;
import com.example.bookrecommender.repository.BookRepository;
import com.example.bookrecommender.repository.UserBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserBookService {
    
    @Autowired
    private UserBookRepository userBookRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    public List<UserBookResponse> getUserBooks(User user) {
        return userBookRepository.findByUserId(user.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public UserBookResponse addBookToUser(User user, Long bookId) {
        if (userBookRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            throw new DuplicateResourceException("Book already in user's list");
        }
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        
        UserBook userBook = UserBook.builder()
                .user(user)
                .book(book)
                .build();
        
        UserBook savedUserBook = userBookRepository.save(userBook);
        return convertToResponse(savedUserBook);
    }
    
    @Transactional
    public UserBookResponse rateBook(User user, Long bookId, RatingRequest request) {
        // Find existing UserBook - book must be in user's list to rate it
        UserBook userBook = userBookRepository.findByUserIdAndBookId(user.getId(), bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not in user's reading list. Add it first before rating."));
        
        userBook.setRating(request.getRating());
        UserBook savedUserBook = userBookRepository.save(userBook);
        
        // Update book's average rating
        updateBookAverageRating(bookId);
        
        return convertToResponse(savedUserBook);
    }
    
    @Transactional
    public void removeBookFromUser(User user, Long bookId) {
        UserBook userBook = userBookRepository.findByUserIdAndBookId(user.getId(), bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not in user's reading list"));
        
        userBookRepository.delete(userBook);
        
        // Update book's average rating after removal
        updateBookAverageRating(bookId);
    }
    
    @Transactional
    public UserBookResponse removeRating(User user, Long bookId) {
        UserBook userBook = userBookRepository.findByUserIdAndBookId(user.getId(), bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not in user's reading list"));
        
        userBook.setRating(null);
        UserBook savedUserBook = userBookRepository.save(userBook);
        
        // Update book's average rating
        updateBookAverageRating(bookId);
        
        return convertToResponse(savedUserBook);
    }
    
    @Transactional
    public UserBookResponse toggleFavorite(User user, Long bookId) {
        UserBook userBook = userBookRepository.findByUserIdAndBookId(user.getId(), bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not in user's reading list. Add it first before marking as favorite."));
        
        userBook.setIsFavorite(!userBook.getIsFavorite());
        UserBook savedUserBook = userBookRepository.save(userBook);
        
        return convertToResponse(savedUserBook);
    }
    
    private void updateBookAverageRating(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        
        // Query all user books for this book directly from repository to get fresh data.
        // This is necessary because after deleting a UserBook entity, accessing book.getUserBooks()
        // would cause a Hibernate ObjectDeletedException as it tries to merge the deleted entity.
        // By querying the repository directly, we get the current state without deleted entities.
        List<UserBook> userBooks = userBookRepository.findByBookId(bookId);
        
        if (userBooks != null && !userBooks.isEmpty()) {
            double average = userBooks.stream()
                    .filter(ub -> ub.getRating() != null)
                    .mapToInt(UserBook::getRating)
                    .average()
                    .orElse(0.0);
            book.setAverageRating(average);
        } else {
            book.setAverageRating(0.0);
        }
        bookRepository.save(book);
    }
    
    private UserBookResponse convertToResponse(UserBook userBook) {
        BookResponse bookResponse = BookResponse.builder()
                .id(userBook.getBook().getId())
                .title(userBook.getBook().getTitle())
                .author(userBook.getBook().getAuthor())
                .isbn(userBook.getBook().getIsbn())
                .genre(userBook.getBook().getGenre())
                .description(userBook.getBook().getDescription())
                .coverImageUrl(userBook.getBook().getCoverImageUrl())
                .publishYear(userBook.getBook().getPublishYear())
                .averageRating(userBook.getBook().getAverageRating())
                .createdAt(userBook.getBook().getCreatedAt())
                .build();
        
        return UserBookResponse.builder()
                .id(userBook.getId())
                .book(bookResponse)
                .rating(userBook.getRating())
                .readAt(userBook.getReadAt())
                .isFavorite(userBook.getIsFavorite())
                .build();
    }
}
