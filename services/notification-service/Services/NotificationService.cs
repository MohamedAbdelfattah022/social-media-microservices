using notification_service.DTOs;
using notification_service.Entities;
using notification_service.Mappers;
using notification_service.Models;
using notification_service.Repositories;
using notification_service.Services.Interfaces;

namespace notification_service.Services;

public class NotificationService(
    INotificationRepository repository,
    INotificationMessageBuilder messageBuilder,
    ICursorPaginationService cursorPaginationService,
    ILogger<NotificationService> logger) : INotificationService {
    public async Task<bool> ExistsByEventIdAsync(string eventId) {
        return await repository.ExistsByEventIdAsync(eventId);
    }

    public async Task<Notification> CreateNotificationAsync(NotificationEvent notificationEvent) {
        var title = messageBuilder.BuildTitle(notificationEvent.EventType);
        var message = messageBuilder.BuildMessage(notificationEvent.EventType, notificationEvent.Metadata);
        var notification = notificationEvent.ToNotification(title, message);

        await repository.AddAsync(notification);

        logger.LogInformation("Created notification {NotificationId} for user {UserId}",
            notification.Id, notification.UserId);

        return notification;
    }

    public async Task CreatePostCreatedNotificationsAsync(NotificationEvent notificationEvent) {
        // TODO: integrate with user service to get follower list and broadcast the notification to them

        if (string.IsNullOrEmpty(notificationEvent.TargetUserId)) {
            logger.LogWarning("POST_CREATED event has no target user ID, skipping notification creation");
            return;
        }

        await CreateNotificationAsync(notificationEvent);
    }

    public async Task<CursorPageResponse<NotificationDto>> GetNotificationsAsync(
        string userId, string? cursor, int pageSize) {
        DateTime? cursorTimestamp = null;
        long? cursorId = null;

        if (!string.IsNullOrEmpty(cursor))
            (cursorTimestamp, cursorId) = cursorPaginationService.DecodeCursor(cursor);

        var notifications = await repository.GetByUserIdPaginatedAsync(
            userId,
            cursorTimestamp,
            cursorId,
            pageSize);

        var hasNext = notifications.Count > pageSize;
        if (hasNext) notifications = notifications.Take(pageSize).ToList();

        var data = notifications.ToDtoList();

        string? nextCursor = null;
        if (hasNext && data.Count > 0) {
            var lastNotification = data[^1];
            nextCursor = cursorPaginationService.EncodeCursor(
                lastNotification.CreatedAt,
                lastNotification.Id.ToString());
        }

        return new CursorPageResponse<NotificationDto> {
            Data = data,
            NextCursor = nextCursor,
            HasNext = hasNext,
            PageSize = pageSize
        };
    }

    public async Task<bool> MarkAsReadAsync(long notificationId, string userId) {
        var notification = await repository.GetByIdAndUserIdAsync(notificationId, userId);

        if (notification == null) return false;

        if (!notification.ReadAt.HasValue) {
            notification.ReadAt = DateTime.UtcNow;
            await repository.UpdateAsync(notification);
            logger.LogInformation("Marked notification {NotificationId} as read", notificationId);
        }

        return true;
    }

    public async Task<int> MarkAllAsReadAsync(string userId) {
        var unreadNotifications = await repository.GetUnreadByUserIdAsync(userId);

        if (unreadNotifications.Count == 0) return 0;

        var now = DateTime.UtcNow;
        foreach (var notification in unreadNotifications) 
            notification.ReadAt = now;

        await repository.UpdateRangeAsync(unreadNotifications);

        logger.LogInformation("Marked {Count} notifications as read for user {UserId}",
            unreadNotifications.Count, userId);

        return unreadNotifications.Count;
    }
}