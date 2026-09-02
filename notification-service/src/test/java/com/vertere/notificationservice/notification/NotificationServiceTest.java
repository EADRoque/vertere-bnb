package com.vertere.notificationservice.notification;

import com.vertere.notificationservice.notification.dto.CreateNotificationRequest;
import com.vertere.notificationservice.notification.dto.NotificationResponse;
import com.vertere.notificationservice.notification.exception.NotificationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void createNotification_savesUnreadNotification() {
        CreateNotificationRequest request = new CreateNotificationRequest(userId, "NEW_MESSAGE", "{\"conversationId\":\"abc\"}");

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResponse response = notificationService.createNotification(request);

        assertEquals("NEW_MESSAGE", response.type());
        assertFalse(response.read());
    }

    @Test
    void markAsRead_setsReadTrue_whenNotificationExists() {
        Notification existing = new Notification(userId, "NEW_REVIEW", "{}");
        UUID notificationId = UUID.randomUUID();

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(existing));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.markAsRead(notificationId);

        assertTrue(existing.isRead());
        verify(notificationRepository).save(existing);
    }

    @Test
    void markAsRead_throwsException_whenNotificationNotFound() {
        UUID notificationId = UUID.randomUUID();
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> notificationService.markAsRead(notificationId));
    }

    @Test
    void getMyNotifications_returnsNotifications_forThatUser() {
        Notification n1 = new Notification(userId, "BOOKING_CONFIRMED", "{}");
        Notification n2 = new Notification(userId, "NEW_MESSAGE", "{}");

        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(n1, n2));

        List<NotificationResponse> responses = notificationService.getMyNotifications(userId);

        assertEquals(2, responses.size());
    }

}