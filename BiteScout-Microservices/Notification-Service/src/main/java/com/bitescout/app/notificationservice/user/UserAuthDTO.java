package com.bitescout.app.notificationservice.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDTO {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private Role role;
    private boolean enabled;
}