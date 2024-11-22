package com.bitescout.app.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.bitescout.app.userservice.entity.Role;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 6, message = "Username must be at least 6 characters")
    private String username;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password must be at least 8 characters and contain at least one letter and one number")
    @NotNull(message = "Password is required")
    private String password;
    @Email(message = "Email should be valid")
    private String email;
    private Role role;
}