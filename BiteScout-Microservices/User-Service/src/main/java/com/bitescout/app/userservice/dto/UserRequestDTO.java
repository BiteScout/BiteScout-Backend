package com.bitescout.app.userservice.dto;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String profilePicture;
    private String role;
    private Boolean isVerified;
    private String verificationToken;
    private String oauthProvider;
    private String oauthId;
}
