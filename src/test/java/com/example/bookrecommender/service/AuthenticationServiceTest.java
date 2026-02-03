package com.example.bookrecommender.service;

import com.example.bookrecommender.dto.AuthResponse;
import com.example.bookrecommender.dto.LoginRequest;
import com.example.bookrecommender.dto.RegisterRequest;
import com.example.bookrecommender.entity.User;
import com.example.bookrecommender.exception.DuplicateResourceException;
import com.example.bookrecommender.repository.UserRepository;
import com.example.bookrecommender.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @InjectMocks
    private AuthenticationService authenticationService;
    
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    
    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();
        
        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();
        
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
    }
    
    @Test
    void testRegisterSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("token");
        when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
        
        AuthResponse response = authenticationService.register(registerRequest);
        
        assertNotNull(response);
        assertEquals("token", response.getToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("Bearer", response.getType());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getName());
        
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }
    
    @Test
    void testRegisterDuplicateEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        
        assertThrows(DuplicateResourceException.class, () -> {
            authenticationService.register(registerRequest);
        });
        
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testLoginSuccess() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("token");
        when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
        
        AuthResponse response = authenticationService.login(loginRequest);
        
        assertNotNull(response);
        assertEquals("token", response.getToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("Bearer", response.getType());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("test@example.com");
    }
    
    @Test
    void testRefreshTokenSuccess() {
        when(jwtUtil.extractUsername("oldRefreshToken")).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtUtil.validateToken("oldRefreshToken", user)).thenReturn(true);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("newToken");
        when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn("newRefreshToken");
        
        AuthResponse response = authenticationService.refresh("oldRefreshToken");
        
        assertNotNull(response);
        assertEquals("newToken", response.getToken());
        assertEquals("newRefreshToken", response.getRefreshToken());
        
        verify(jwtUtil).extractUsername("oldRefreshToken");
        verify(jwtUtil).validateToken("oldRefreshToken", user);
    }
    
    @Test
    void testRefreshTokenInvalid() {
        when(jwtUtil.extractUsername("invalidToken")).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtUtil.validateToken("invalidToken", user)).thenReturn(false);
        
        assertThrows(com.example.bookrecommender.exception.InvalidTokenException.class, () -> {
            authenticationService.refresh("invalidToken");
        });
    }
}
