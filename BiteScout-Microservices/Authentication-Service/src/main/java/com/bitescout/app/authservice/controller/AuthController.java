package com.bitescout.app.authservice.controller;

import com.bitescout.app.authservice.dto.RegisterDto;
import com.bitescout.app.authservice.dto.TokenDto;
import com.bitescout.app.authservice.request.LoginRequest;
import com.bitescout.app.authservice.request.RegisterRequest;
import com.bitescout.app.authservice.service.AuthService;
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
    public ResponseEntity<RegisterDto> register(@Validated @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
