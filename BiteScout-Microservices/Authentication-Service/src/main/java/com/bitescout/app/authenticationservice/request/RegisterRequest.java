package com.bitescout.app.authenticationservice.request;

import com.bitescout.app.authenticationservice.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 6, message = "Username must be at least 6 characters")
    private String username;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password must be at least 8 characters and contain at least one letter and one number")
    @NotNull(message = "Password is required")
    private String password;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    private Role role;
}