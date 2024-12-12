package com.bitescout.app.notificationservice.user;

import jakarta.validation.constraints.Email;
import org.apache.kafka.common.protocol.types.Field;

import java.util.List;
import java.util.UUID;

//might just copy user dto
public record UserResponse(
        String id,
        String username,
        String email,
        boolean enabled,
        UserDetails userDetails
) {
}
