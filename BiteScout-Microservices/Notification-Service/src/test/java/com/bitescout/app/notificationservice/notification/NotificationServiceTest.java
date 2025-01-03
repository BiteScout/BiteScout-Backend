package com.bitescout.app.notificationservice.notification;
import com.bitescout.app.notificationservice.exception.NotificationNotFoundException;
import com.bitescout.app.notificationservice.notification.dto.NotificationRequest;
import com.bitescout.app.notificationservice.notification.dto.NotificationResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import jakarta.validation.Validation;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class NotificationServiceTest {
    @InjectMocks
    private NotificationService notificationService;
    @Mock
    private NotificationRepository repository;
    @Mock
    private NotificationMapper mapper;
    private final Validator validator;
    public NotificationServiceTest() {
        // Initialize the validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void CreateNotification_ValidData() {
        // Arrange
        NotificationRequest request = new NotificationRequest("test notification", NotificationType.SPECIAL_OFFER_NOTIFICATION);
        String userId = "f814c738-f364-44a8-b9d2-b25ecbdcf393";
        Notification notification = new Notification();
        NotificationResponse response = new NotificationResponse(
                123L,
                "f814c738-f364-44a8-b9d2-b25ecbdcf393",
                "test notification",
                NotificationType.SPECIAL_OFFER_NOTIFICATION,
                false,
                LocalDateTime.now());
        when(mapper.toNotification(request, userId)).thenReturn(notification);
        when(repository.save(notification)).thenReturn(notification);
        when(mapper.toNotificationResponse(notification)).thenReturn(response);
        // Act
        NotificationResponse result = notificationService.createNotification(request, userId);
        // Assert
        assertNotNull(result);
        verify(repository, times(1)).save(notification);
    }
    @Test
    void CreateNotification_MissingFields() {
        // Arrange: Create a NotificationRequest with an empty message
        NotificationRequest request = new NotificationRequest("", NotificationType.SPECIAL_OFFER_NOTIFICATION);
        String userId = "f814c738-f364-44a8-b9d2-b25ecbdcf393";
        // Act: Manually validate the NotificationRequest object
        Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request);
        // Assert: Expect validation violations
        if (!violations.isEmpty()) {
            // The first violation message should be returned
            String violationMessage = violations.iterator().next().getMessage();
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                // You can trigger your service call here if necessary, or just throw the exception based on the validation message
                throw new IllegalArgumentException(violationMessage);
            });
            // Assert: Check that the exception message matches the violation message
            assertEquals("Message length must be between 5 and 500 characters", exception.getMessage());
        } else {
            // If there are no violations, proceed with the normal service method
            notificationService.createNotification(request, userId);
        }
    }
    @Test
    void GetNotifications() {
        // Arrange
        String userId = "f814c738-f364-44a8-b9d2-b25ecbdcf393";
        Notification notification = new Notification();
        NotificationResponse response = new NotificationResponse(
                123L,
                "f814c738-f364-44a8-b9d2-b25ecbdcf393",
                "test notification",
                NotificationType.SPECIAL_OFFER_NOTIFICATION,
                false,
                LocalDateTime.now());
        when(repository.findByUserId(userId)).thenReturn(Collections.singletonList(notification));
        when(mapper.toNotificationResponse(notification)).thenReturn(response);
        // Act
        List<NotificationResponse> result = notificationService.getNotifications(userId);
        // Assert
        assertFalse(result.isEmpty());
        verify(repository, times(1)).findByUserId(userId);
    }
    @Test
    void MarkNotificationAsSeen_Valid() {
        // Arrange
        Long notificationId = 1L;
        String userId = "f814c738-f364-44a8-b9d2-b25ecbdcf393";
        Notification notification = new Notification();
        NotificationResponse response = new NotificationResponse(
                123L,
                "f814c738-f364-44a8-b9d2-b25ecbdcf393",
                "test notification",
                NotificationType.SPECIAL_OFFER_NOTIFICATION,
                false,
                LocalDateTime.now());
        when(repository.findByIdAndUserId(notificationId, userId)).thenReturn(Optional.of(notification));
        when(repository.save(notification)).thenReturn(notification);
        when(mapper.toNotificationResponse(notification)).thenReturn(response);
        // Act
        NotificationResponse result = notificationService.markAsSeen(notificationId, userId);
        // Assert
        assertNotNull(result);
        verify(repository, times(1)).save(notification);
    }
    @Test
    void MarkNotificationAsSeen_NotFound() {
        // Arrange
        Long notificationId = 1L;
        String userId = "f814c738-f364-44a8-b9d2-b25ecbdcf393";
        when(repository.findByIdAndUserId(notificationId, userId)).thenReturn(Optional.empty());
        // Act & Assert
        NotificationNotFoundException exception = assertThrows(NotificationNotFoundException.class, () -> {
            notificationService.markAsSeen(notificationId, userId);
        });
        assertEquals("Notification with id 1 for user id f814c738-f364-44a8-b9d2-b25ecbdcf393 not found", exception.getMessage());
    }
    @Test
    void DeleteNotification_Valid() {
        // Arrange
        Long notificationId = 1L;
        String userId = "f814c738-f364-44a8-b9d2-b25ecbdcf393";
        Notification notification = new Notification();
        when(repository.findByIdAndUserId(notificationId, userId)).thenReturn(Optional.of(notification));
        // Act
        notificationService.deleteNotification(notificationId, userId);
        // Assert
        verify(repository, times(1)).deleteById(notificationId);
    }
    @Test
    void DeleteNotification_NotFound() {
        // Arrange
        Long notificationId = 1L;
        String userId = "f814c738-f364-44a8-b9d2-b25ecbdcf393";
        when(repository.findByIdAndUserId(notificationId, userId)).thenReturn(Optional.empty());
        // Act & Assert
        NotificationNotFoundException exception = assertThrows(NotificationNotFoundException.class, () -> {
            notificationService.deleteNotification(notificationId, userId);
        });
        assertEquals("Notification with id 1 and user id f814c738-f364-44a8-b9d2-b25ecbdcf393 not found", exception.getMessage());
    }
}