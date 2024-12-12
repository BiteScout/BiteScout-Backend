package com.bitescout.app.notificationservice.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    public List<Notification> findByUserId(String userId);

    @Query("SELECT n FROM Notification n WHERE n.id = :notificationId AND n.userId = :userId")
    Optional<Notification> findByIdAndUserId(Long notificationId, String userId);
}
