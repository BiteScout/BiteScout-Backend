package com.bitescout.app.notificationservice.notification;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "notifications")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String message;
    @Enumerated(value = EnumType.STRING)
    private NotificationType notificationType;
    private boolean isRead = false;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

}
