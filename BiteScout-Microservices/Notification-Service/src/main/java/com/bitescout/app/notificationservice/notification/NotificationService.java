package com.bitescout.app.notificationservice.notification;

import com.bitescout.app.notificationservice.exception.NotificationNotFoundException;
import com.bitescout.app.notificationservice.notification.dto.NotificationRequest;
import com.bitescout.app.notificationservice.notification.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    public NotificationResponse createNotification(NotificationRequest request, String userId) {
        var notification = mapper.toNotification(request, userId);
        repository.save(notification);
        return mapper.toNotificationResponse(notification);
    }

    public List<NotificationResponse> getNotifications(String userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(mapper::toNotificationResponse)
                .sorted(Comparator.comparing(NotificationResponse::createdAt).reversed())
                .collect(Collectors.toList());
    }

    public NotificationResponse markAsSeen(Long notificationId, String userId){
        var notification = repository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(()->new NotificationNotFoundException(String.format(
                        "Notification with id %d for user id %s not found", notificationId, userId)
                ));
        notification.setRead(true);
        repository.save(notification);
        return mapper.toNotificationResponse(notification);
    }

    public void deleteNotification(Long notificationId, String userId) {
        var notification = repository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(()->new NotificationNotFoundException(String.format(
                        "Notification with id %d and user id %s not found", notificationId, userId
                )));
        repository.deleteById(notificationId);
    }
}
