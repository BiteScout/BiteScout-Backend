package com.bitescout.app.notificationservice.notification;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @RequestBody @Valid NotificationRequest request,
            @RequestHeader(value = "User-Id") Long userId
    ){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createNotification(request, userId));
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @RequestHeader(value = "User-Id") Long userId
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getNotifications(userId));
    }

    @PutMapping("/{notification-id}")
    public ResponseEntity<NotificationResponse> markAsSeen(
            @PathVariable("notification-id") Long notificationId,
            @RequestHeader(value = "User-Id") Long userId
    ) {
        return ResponseEntity.ok(service.markAsSeen(notificationId, userId));
    }


    @DeleteMapping("/{notification-id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteNotification(
            @PathVariable("notification-id") Long notificationId,
            @RequestHeader(value = "User-Id") Long userId
    ){
        service.deleteNotification(notificationId, userId);
    }


}
