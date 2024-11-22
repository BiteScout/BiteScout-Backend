package com.bitescout.app.authservice.service;

import com.bitescout.app.authservice.dto.TokenDto;
import com.bitescout.app.authservice.exc.WrongCredentialsException;
import com.bitescout.app.authservice.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService CustomUserDetailsService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        // Arrange
        LoginRequest request = new LoginRequest("testUser", "testPassword");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken("testUser")).thenReturn("testToken");

        // Act
        TokenDto result = authService.login(request);

        // Assert
        assertNotNull(result);
        assertEquals("testToken", result.getToken());
    }

    @Test
    void testLogin_Failure() {
        // Arrange
        LoginRequest request = new LoginRequest("testUser", "wrongPassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new WrongCredentialsException("Wrong credentials"));

        // Act & Assert
        assertThrows(WrongCredentialsException.class, () -> authService.login(request));
    }
}