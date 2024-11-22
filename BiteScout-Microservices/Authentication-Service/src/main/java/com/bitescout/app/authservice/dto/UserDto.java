package com.bitescout.app.authservice.dto;

import com.bitescout.app.authservice.enums.Role;
import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String username;
    private String password;
    private Role role;
}
