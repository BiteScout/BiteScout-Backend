package com.bitescout.app.reservationservice.user;

//might just copy user dto
public record UserResponse(
        String id,
        String username,
        String email,
        boolean enabled,
        UserDetails userDetails
) {
}
