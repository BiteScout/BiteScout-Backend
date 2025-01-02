package com.bitescout.app.authenticationservice.service;

import com.bitescout.app.authenticationservice.client.UserServiceClient;
import com.bitescout.app.authenticationservice.dto.TokenDto;
import com.bitescout.app.authenticationservice.dto.UserDto;
import com.bitescout.app.authenticationservice.email.EmailService;
import com.bitescout.app.authenticationservice.entity.VerificationToken;
import com.bitescout.app.authenticationservice.exc.WrongCredentialsException;
import com.bitescout.app.authenticationservice.repository.VerificationTokenRepository;
import com.bitescout.app.authenticationservice.request.LoginRequest;
import com.bitescout.app.authenticationservice.request.RegisterRequest;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserServiceClient userServiceClient;
    private final JwtService jwtService;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    public TokenDto login(LoginRequest request) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        if (authenticate.isAuthenticated() ) // && userServiceClient.getUserByUsername(request.getUsername()).getBody().isEnabled()
            return TokenDto // şimdilik enable kontrolü yapmıyorum loginden token alabilmek için
                    .builder()
                    .token(jwtService.generateToken(request.getUsername()))
                    .build();
        else throw new WrongCredentialsException("Wrong credentials");
    }

    public TokenDto register(RegisterRequest request) {
        UserDto userDto = userServiceClient.save(request).getBody();
        if(userDto == null)
            throw new RuntimeException("Failed to save user");
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .username(userDto.getUsername())
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();
        tokenRepository.save(verificationToken);

        try {
            emailService.sendVerificationEmail(userDto.getEmail(), token);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
        return TokenDto.builder()
                .token(jwtService.generateToken(userDto.getUsername()))
                .build();
    }


    public List<VerificationToken> getAllTokens() {
        return tokenRepository.findAll();
    }

    public void verifyToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        UserDto user = userServiceClient.getUserByUsername(verificationToken.getUsername()).getBody();
        assert user != null;
        user.setEnabled(true);
        userServiceClient.update(user.getId());
    }
}