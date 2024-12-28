package com.bitescout.app.reviewservice.user;

//might just copy user dto
public record UserResponse(
        String id,
        String username,
        String email,
        boolean enabled,
        UserDetails userDetails
) {
}
