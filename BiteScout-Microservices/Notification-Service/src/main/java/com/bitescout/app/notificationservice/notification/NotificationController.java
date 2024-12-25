package com.bitescout.app.notificationservice.notification;

import com.bitescout.app.notificationservice.notification.dto.NotificationRequest;
import com.bitescout.app.notificationservice.notification.dto.NotificationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    //when the project progresses notifications will only be created via NotificationsConsumer and this
    //endpoint will be deleted, for now it's good for testing purposes.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwner(#userId, principal)")
    public ResponseEntity<NotificationResponse> createNotification(
            @RequestBody @Valid NotificationRequest request,
            @RequestHeader(value = "User-Id") String userId
    ){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createNotification(request, userId));
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwner(#userId, principal)")
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @RequestHeader(value = "User-Id") String userId
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getNotifications(userId));
    }

    @PutMapping("/{notification-id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwner(#userId, principal)")
    public ResponseEntity<NotificationResponse> markAsSeen(
            @PathVariable("notification-id") Long notificationId,
            @RequestHeader(value = "User-Id") String userId
    ) {
        return ResponseEntity.ok(service.markAsSeen(notificationId, userId));
    }


    @DeleteMapping("/{notification-id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwner(#userId, principal)")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteNotification(
            @PathVariable("notification-id") Long notificationId,
            @RequestHeader(value = "User-Id") String userId
    ){
        service.deleteNotification(notificationId, userId);
    }


}
