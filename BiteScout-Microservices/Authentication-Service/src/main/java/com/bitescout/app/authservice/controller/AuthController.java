package com.bitescout.app.authservice.controller;

import com.bitescout.app.authservice.dto.RegisterDto;
import com.bitescout.app.authservice.dto.TokenDto;
import com.bitescout.app.authservice.request.LoginRequest;
import com.bitescout.app.authservice.request.RegisterRequest;
import com.bitescout.app.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping ("/login")
    public ResponseEntity<TokenDto> loginPage() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterDto> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
