package com.bitescout.app.authenticationservice.controller;

import com.bitescout.app.authenticationservice.config.TestSecurityConfig;
import com.bitescout.app.authenticationservice.dto.TokenDto;
import com.bitescout.app.authenticationservice.exc.WrongCredentialsException;
import com.bitescout.app.authenticationservice.request.LoginRequest;
import com.bitescout.app.authenticationservice.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)

class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        TokenDto tokenDto = TokenDto.builder().token("testToken").build();
        Mockito.when(authService.login(Mockito.any(LoginRequest.class))).thenReturn(tokenDto);

        // Act & Assert
        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testUser\",\"password\":\"testPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"testToken\"}"));
    }

    @Test
    void testLogin_Failure() throws Exception {
        // Arrange
        Mockito.when(authService.login(Mockito.any(LoginRequest.class))).thenThrow(new WrongCredentialsException("Wrong credentials"));

        // Act & Assert
        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testUser\",\"password\":\"wrongPassword\"}"))
                .andExpect(status().isUnauthorized());
    }
}