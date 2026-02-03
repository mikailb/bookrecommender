package com.example.bookrecommender.controller;

import com.example.bookrecommender.dto.RatingRequest;
import com.example.bookrecommender.dto.UserBookResponse;
import com.example.bookrecommender.dto.UserProfileResponse;
import com.example.bookrecommender.entity.User;
import com.example.bookrecommender.service.UserBookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserBookService userBookService;
    
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal User user) {
        UserProfileResponse response = UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .createdAt(user.getCreatedAt())
                .build();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/books")
    public ResponseEntity<List<UserBookResponse>> getUserBooks(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userBookService.getUserBooks(user));
    }
    
    @PostMapping("/books/{bookId}")
    public ResponseEntity<UserBookResponse> addBookToUser(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userBookService.addBookToUser(user, bookId));
    }
    
    @PostMapping("/books/{bookId}/rate")
    public ResponseEntity<UserBookResponse> rateBook(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookId,
            @Valid @RequestBody RatingRequest request
    ) {
        return ResponseEntity.ok(userBookService.rateBook(user, bookId, request));
    }
    
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> removeBookFromUser(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookId
    ) {
        userBookService.removeBookFromUser(user, bookId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/books/{bookId}/rate")
    public ResponseEntity<UserBookResponse> removeRating(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookId
    ) {
        return ResponseEntity.ok(userBookService.removeRating(user, bookId));
    }
    
    @PostMapping("/books/{bookId}/favorite")
    public ResponseEntity<UserBookResponse> toggleFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookId
    ) {
        return ResponseEntity.ok(userBookService.toggleFavorite(user, bookId));
    }
}
