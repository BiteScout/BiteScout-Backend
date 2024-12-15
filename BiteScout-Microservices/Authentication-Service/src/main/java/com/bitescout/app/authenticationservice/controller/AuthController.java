package com.bitescout.app.authenticationservice.controller;

import com.bitescout.app.authenticationservice.dto.TokenDto;
import com.bitescout.app.authenticationservice.request.LoginRequest;
import com.bitescout.app.authenticationservice.request.RegisterRequest;
import com.bitescout.app.authenticationservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    @GetMapping ("/login")
    public ResponseEntity<TokenDto> loginPage() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<TokenDto> register(@Validated @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // user to verify email address. After clicking on the link in the email,
    // the user will be redirected to this endpoint to verify the email address
    // and enable the user in the database
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyToken(token);
        return ResponseEntity.ok("Email verified successfully");
    }


}
