using notification_service.DTOs;
using notification_service.Entities;
using notification_service.Models;

namespace notification_service.Services.Interfaces;

public interface INotificationService {
    Task<bool> ExistsByEventIdAsync(string eventId);
    Task<Notification> CreateNotificationAsync(NotificationEvent notificationEvent);
    Task CreatePostCreatedNotificationsAsync(NotificationEvent notificationEvent);
    Task<CursorPageResponse<NotificationDto>> GetNotificationsAsync(string userId, string? cursor, int pageSize);
    Task<bool> MarkAsReadAsync(long notificationId, string userId);
    Task<int> MarkAllAsReadAsync(string userId);
}