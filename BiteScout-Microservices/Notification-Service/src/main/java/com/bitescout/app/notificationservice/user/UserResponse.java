package com.bitescout.app.notificationservice.user;

import jakarta.validation.constraints.Email;
import org.apache.kafka.common.protocol.types.Field;

import java.util.List;

//might just copy user dto
public record UserResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        List<Long> favoritedRestaurants
) {
}
