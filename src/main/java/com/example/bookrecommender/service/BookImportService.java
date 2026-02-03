package com.example.bookrecommender.service;

import com.example.bookrecommender.dto.openlibrary.OpenLibraryBook;
import com.example.bookrecommender.dto.openlibrary.OpenLibrarySearchResponse;
import com.example.bookrecommender.entity.Book;
import com.example.bookrecommender.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Order(2) // Run after UserSeederService
public class BookImportService implements ApplicationRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(BookImportService.class);
    private static final String OPEN_LIBRARY_API_URL = "https://openlibrary.org/search.json";
    private static final int BOOKS_PER_GENRE = 15;
    
    @Autowired
    private BookRepository bookRepository;
    
    private final RestTemplate restTemplate;
    
    public BookImportService() {
        this.restTemplate = new RestTemplate();
    }
    
    @Override
    public void run(ApplicationArguments args) {
        // Only import if database is empty
        if (bookRepository.count() == 0) {
            logger.info("Starting book import from Open Library API...");
            importBooks();
            logger.info("Book import completed!");
        } else {
            logger.info("Books already exist in database. Skipping import.");
        }
    }
    
    @Transactional
    public void importBooks() {
        List<String> genres = Arrays.asList(
            "fiction", "fantasy", "science fiction", "romance", 
            "mystery", "thriller", "horror", "historical fiction"
        );
        
        int totalImported = 0;
        
        for (String genre : genres) {
            try {
                logger.info("Fetching books for genre: {}", genre);
                int imported = importBooksForGenre(genre);
                totalImported += imported;
                logger.info("Imported {} books for genre: {}", imported, genre);
                
                // Rate limiting - wait between requests
                Thread.sleep(1000);
            } catch (Exception e) {
                logger.error("Error importing books for genre: {}", genre, e);
            }
        }
        
        logger.info("Total books imported: {}", totalImported);
    }
    
    private int importBooksForGenre(String genre) {
        String url = UriComponentsBuilder.fromHttpUrl(OPEN_LIBRARY_API_URL)
                .queryParam("q", "subject:" + genre)
                .queryParam("limit", BOOKS_PER_GENRE)
                .queryParam("sort", "rating")
                .toUriString();
        
        try {
            OpenLibrarySearchResponse response = restTemplate.getForObject(url, OpenLibrarySearchResponse.class);
            
            if (response == null || response.getDocs() == null) {
                logger.warn("No response or docs for genre: {}", genre);
                return 0;
            }
            
            int imported = 0;
            for (OpenLibraryBook openLibraryBook : response.getDocs()) {
                if (importBook(openLibraryBook, genre)) {
                    imported++;
                }
            }
            
            return imported;
        } catch (Exception e) {
            logger.error("Error fetching books for genre: {}", genre, e);
            return 0;
        }
    }
    
    private boolean importBook(OpenLibraryBook openLibraryBook, String genre) {
        try {
            // Validate required fields
            if (openLibraryBook.getTitle() == null || openLibraryBook.getTitle().trim().isEmpty()) {
                return false;
            }
            
            if (openLibraryBook.getAuthorName() == null || openLibraryBook.getAuthorName().isEmpty()) {
                return false;
            }
            
            String title = openLibraryBook.getTitle().trim();
            String author = openLibraryBook.getAuthorName().get(0).trim();
            
            // Check for duplicates
            String isbn = null;
            if (openLibraryBook.getIsbn() != null && !openLibraryBook.getIsbn().isEmpty()) {
                isbn = openLibraryBook.getIsbn().get(0);
                if (bookRepository.existsByIsbn(isbn)) {
                    return false;
                }
            }
            
            if (bookRepository.existsByTitleAndAuthor(title, author)) {
                return false;
            }
            
            // Build description from first sentence or subject
            String description = buildDescription(openLibraryBook, genre);
            
            // Build cover image URL
            String coverImageUrl = null;
            if (openLibraryBook.getCoverId() != null) {
                coverImageUrl = String.format("https://covers.openlibrary.org/b/id/%d-M.jpg", 
                    openLibraryBook.getCoverId());
            }
            
            // Create and save book
            Book book = Book.builder()
                    .title(title)
                    .author(author)
                    .isbn(isbn)
                    .genre(capitalizeGenre(genre))
                    .description(description)
                    .coverImageUrl(coverImageUrl)
                    .publishYear(openLibraryBook.getFirstPublishYear())
                    .build();
            
            bookRepository.save(book);
            logger.debug("Imported book: {} by {}", title, author);
            return true;
            
        } catch (Exception e) {
            logger.error("Error importing book: {}", openLibraryBook.getTitle(), e);
            return false;
        }
    }
    
    private String buildDescription(OpenLibraryBook book, String genre) {
        StringBuilder description = new StringBuilder();
        
        // Add first sentence if available
        if (book.getFirstSentence() != null && !book.getFirstSentence().isEmpty()) {
            description.append(book.getFirstSentence().get(0));
        }
        
        // Add genre info if description is empty
        if (description.length() == 0) {
            description.append("A ").append(genre).append(" book");
            if (book.getSubject() != null && !book.getSubject().isEmpty()) {
                description.append(" about ").append(String.join(", ", 
                    book.getSubject().subList(0, Math.min(3, book.getSubject().size()))));
            }
            description.append(".");
        }
        
        // Limit description length
        String result = description.toString();
        if (result.length() > 2000) {
            result = result.substring(0, 1997) + "...";
        }
        
        return result;
    }
    
    private String capitalizeGenre(String genre) {
        if (genre == null || genre.isEmpty()) {
            return genre;
        }
        
        String[] words = genre.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase());
        }
        
        return result.toString();
    }
}
