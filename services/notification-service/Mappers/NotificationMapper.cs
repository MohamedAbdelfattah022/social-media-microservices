using notification_service.DTOs;
using notification_service.Entities;
using notification_service.Models;

namespace notification_service.Mappers;

public static class NotificationMapper {
    public static NotificationDto ToDto(this Notification notification) {
        return new NotificationDto {
            Id = notification.Id,
            EventId = notification.EventId,
            Type = notification.Type,
            Title = notification.Title,
            Message = notification.Message,
            ActorUserId = notification.ActorUserId,
            ResourceType = notification.ResourceType,
            ResourceId = notification.ResourceId,
            Metadata = notification.Metadata,
            CreatedAt = notification.CreatedAt,
            ReadAt = notification.ReadAt
        };
    }

    public static Notification ToNotification(this NotificationEvent notificationEvent, string title, string message) {
        return new Notification {
            EventId = notificationEvent.EventId,
            UserId = notificationEvent.TargetUserId,
            Type = notificationEvent.EventType,
            ActorUserId = notificationEvent.ActorUserId,
            ResourceType = notificationEvent.ResourceType,
            ResourceId = notificationEvent.ResourceId,
            Title = title,
            Message = message,
            Metadata = notificationEvent.Metadata,
            CreatedAt = DateTime.UtcNow
        };
    }

    public static List<NotificationDto> ToDtoList(this IEnumerable<Notification> notifications) {
        return notifications.Select(n => n.ToDto()).ToList();
    }
}