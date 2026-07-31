package career_Navigator_parent.notification.controller;

import career_Navigator_parent.notification.dto.response.NotificationResponse;
import career_Navigator_parent.notification.dto.response.UnreadNotificationCountResponse;
import career_Navigator_parent.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final NotificationService
            notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>>
    getMyNotifications(
            @RequestParam(required = false)
            Boolean read,

            @PageableDefault(
                    size = 20,
                    sort = "createdAt"
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                notificationService
                        .getMyNotifications(
                                read,
                                pageable
                        )
        );
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse>
    getNotificationById(
            @PathVariable Long notificationId
    ) {

        return ResponseEntity.ok(
                notificationService
                        .getNotificationById(
                                notificationId
                        )
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<
            UnreadNotificationCountResponse>
    getUnreadNotificationCount() {

        return ResponseEntity.ok(
                notificationService
                        .getUnreadNotificationCount()
        );
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse>
    markAsRead(
            @PathVariable Long notificationId
    ) {

        return ResponseEntity.ok(
                notificationService
                        .markAsRead(notificationId)
        );
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void>
    markAllAsRead() {

        notificationService.markAllAsRead();

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void>
    deleteNotification(
            @PathVariable Long notificationId
    ) {

        notificationService.deleteNotification(
                notificationId
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear-read")
    public ResponseEntity<Map<String, Long>>
    clearReadNotifications() {

        long deletedCount =
                notificationService
                        .clearReadNotifications();

        return ResponseEntity.ok(
                Map.of(
                        "deletedCount",
                        deletedCount
                )
        );
    }

    @DeleteMapping("/clear-all")
    public ResponseEntity<Map<String, Long>>
    clearAllNotifications() {

        long deletedCount =
                notificationService
                        .clearAllNotifications();

        return ResponseEntity.ok(
                Map.of(
                        "deletedCount",
                        deletedCount
                )
        );
    }
}