package com.bitescout.app.notificationservice.security;

import com.bitescout.app.notificationservice.user.UserClient;
import com.bitescout.app.notificationservice.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private static final Logger log = LoggerFactory.getLogger(SecurityService.class);
    private final UserClient userClient;

    public boolean isOwner(String ownerId, String principal) {
        UserResponse user = userClient.getUser(ownerId).get();
        if (user != null) {
            System.out.println("Owner ID: " + ownerId);
            System.out.println("Principal: " + principal);
            System.out.println("Owner Username: " + user.username());
            return user.username().equals(principal);
        }
        log.info("Owner ID: {}, Principal: {}", ownerId, principal);

        return false;
    }
}