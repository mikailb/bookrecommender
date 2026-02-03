package com.example.bookrecommender.service;

import com.example.bookrecommender.entity.Book;
import com.example.bookrecommender.entity.User;
import com.example.bookrecommender.entity.UserBook;
import com.example.bookrecommender.repository.BookRepository;
import com.example.bookrecommender.repository.UserBookRepository;
import com.example.bookrecommender.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Order(3) // Run after BookImportService
public class DataSeeder implements ApplicationRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserBookRepository userBookRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private Random random = new Random();
    
    @Override
    public void run(ApplicationArguments args) {
        seedDemoData();
    }
    
    @Transactional
    public void seedDemoData() {
        // Define Norwegian demo users
        String[][] demoUsers = {
            {"Per Hansen", "per.hansen@example.com", "password123"},
            {"Ola Nordmann", "ola.nordmann@example.com", "password123"},
            {"Kari Larsen", "kari.larsen@example.com", "password123"},
            {"Emma Johansen", "emma.johansen@example.com", "password123"}
        };
        
        // Check if any demo users already exist
        boolean anyExists = false;
        for (String[] userData : demoUsers) {
            if (userRepository.existsByEmail(userData[1])) {
                anyExists = true;
                break;
            }
        }
        
        if (anyExists) {
            logger.info("Demo users already exist. Skipping data seeding.");
            return;
        }
        
        // Get all books for assigning to users
        List<Book> allBooks = bookRepository.findAll();
        if (allBooks.isEmpty()) {
            logger.warn("No books available for seeding user data. Skipping.");
            return;
        }
        
        logger.info("Starting demo data seeding...");
        
        // Define genre preferences for each user (for realistic recommendations)
        Map<String, List<String>> userGenrePreferences = new HashMap<>();
        userGenrePreferences.put("per.hansen@example.com", Arrays.asList("Fiction", "Mystery", "Thriller"));
        userGenrePreferences.put("ola.nordmann@example.com", Arrays.asList("Fantasy", "Science Fiction", "Fiction"));
        userGenrePreferences.put("kari.larsen@example.com", Arrays.asList("Romance", "Historical Fiction", "Fiction"));
        userGenrePreferences.put("emma.johansen@example.com", Arrays.asList("Horror", "Thriller", "Mystery"));
        
        for (String[] userData : demoUsers) {
            String name = userData[0];
            String email = userData[1];
            String password = userData[2];
            
            // Create user
            User user = User.builder()
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .build();
            user = userRepository.save(user);
            logger.info("Created demo user: {}", email);
            
            // Get books matching user's genre preferences
            List<String> preferredGenres = userGenrePreferences.get(email);
            List<Book> preferredBooks = bookRepository.findByGenreIn(preferredGenres);
            
            // If not enough books in preferred genres, add some random books
            List<Book> userBooks = new ArrayList<>();
            if (!preferredBooks.isEmpty()) {
                // Pick 6-8 books from preferred genres
                int numPreferredBooks = 6 + random.nextInt(3); // 6-8 books
                Collections.shuffle(preferredBooks, random);
                userBooks.addAll(preferredBooks.subList(0, Math.min(numPreferredBooks, preferredBooks.size())));
            }
            
            // Add 2-4 random books from other genres for variety
            List<Book> otherBooks = new ArrayList<>(allBooks);
            otherBooks.removeAll(userBooks);
            if (!otherBooks.isEmpty()) {
                Collections.shuffle(otherBooks, random);
                int numOtherBooks = 2 + random.nextInt(3); // 2-4 books
                userBooks.addAll(otherBooks.subList(0, Math.min(numOtherBooks, otherBooks.size())));
            }
            
            // Ensure we have 5-10 books per user
            Collections.shuffle(userBooks, random);
            int totalBooks = Math.min(userBooks.size(), 5 + random.nextInt(6)); // 5-10 books
            userBooks = userBooks.subList(0, totalBooks);
            
            // Track favorite books (2-4 per user)
            int numFavorites = 2 + random.nextInt(3); // 2-4 favorites
            Set<Integer> favoriteIndices = new HashSet<>();
            while (favoriteIndices.size() < Math.min(numFavorites, userBooks.size())) {
                favoriteIndices.add(random.nextInt(userBooks.size()));
            }
            
            // Add books to user's library with ratings
            int bookIndex = 0;
            for (Book book : userBooks) {
                boolean isFavorite = favoriteIndices.contains(bookIndex);
                
                // Most books get rated (80% chance)
                Integer rating = null;
                if (random.nextDouble() < 0.8) {
                    // Favorites get 4-5 stars, others get 2-5 stars
                    if (isFavorite) {
                        rating = 4 + random.nextInt(2); // 4-5 stars
                    } else {
                        rating = 2 + random.nextInt(4); // 2-5 stars
                    }
                }
                
                UserBook userBook = UserBook.builder()
                        .user(user)
                        .book(book)
                        .rating(rating)
                        .isFavorite(isFavorite)
                        .readAt(LocalDateTime.now().minusDays(random.nextInt(365))) // Random date in past year
                        .build();
                
                userBookRepository.save(userBook);
                bookIndex++;
            }
            
            logger.info("Added {} books to {}'s library ({} favorites, {} rated)", 
                    userBooks.size(), name, favoriteIndices.size(), 
                    userBooks.stream().filter(b -> random.nextDouble() < 0.8).count());
        }
        
        logger.info("Demo data seeding completed!");
    }
}
