package com.example.bookrecommender.service;

import com.example.bookrecommender.entity.User;
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

import java.util.Arrays;
import java.util.List;

@Service
@Order(1) // Run before BookImportService
public class UserSeederService implements ApplicationRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(UserSeederService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(ApplicationArguments args) {
        seedUsers();
    }
    
    @Transactional
    public void seedUsers() {
        // Only seed test users in development/test environments
        // In production, these users should be created through proper user registration
        // Define test users - including Norwegian demo users
        List<UserData> testUsers = Arrays.asList(
            new UserData("test1@example.com", "password123", "Test User 1"),
            new UserData("test2@example.com", "password123", "Test User 2"),
            new UserData("test3@example.com", "password123", "Test User 3"),
            // Norwegian demo users (will be populated with data by DataSeeder)
            new UserData("per.hansen@example.com", "password123", "Per Hansen"),
            new UserData("ola.nordmann@example.com", "password123", "Ola Nordmann"),
            new UserData("kari.larsen@example.com", "password123", "Kari Larsen"),
            new UserData("emma.johansen@example.com", "password123", "Emma Johansen")
        );
        
        int createdCount = 0;
        for (UserData userData : testUsers) {
            if (!userRepository.existsByEmail(userData.email)) {
                User user = User.builder()
                        .email(userData.email)
                        .password(passwordEncoder.encode(userData.password))
                        .name(userData.name)
                        .build();
                
                userRepository.save(user);
                createdCount++;
                logger.info("Created test user: {}", userData.email);
            } else {
                logger.info("Test user already exists: {}", userData.email);
            }
        }
        
        if (createdCount > 0) {
            logger.info("User seeding completed. Created {} test users.", createdCount);
        } else {
            logger.info("All test users already exist. Skipping seeding.");
        }
    }
    
    private static class UserData {
        String email;
        String password;
        String name;
        
        UserData(String email, String password, String name) {
            this.email = email;
            this.password = password;
            this.name = name;
        }
    }
}
