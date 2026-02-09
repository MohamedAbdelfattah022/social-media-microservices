using notification_service.Constants;
using notification_service.Models;
using notification_service.Services.Interfaces;

namespace notification_service.Handlers;

public class NotificationEventHandler(
    ILogger<NotificationEventHandler> logger,
    INotificationService notificationService,
    INotificationStreamService streamService) {
    public async Task Handle(NotificationEvent notificationEvent) {
        logger.LogInformation("Received notification event: EventId={EventId}, Type={EventType}",
            notificationEvent.EventId, notificationEvent.EventType);

        if (notificationEvent.ActorUserId == notificationEvent.TargetUserId) {
            logger.LogDebug("Skipping self-notification for user {UserId}", notificationEvent.ActorUserId);
            return;
        }

        if (await notificationService.ExistsByEventIdAsync(notificationEvent.EventId)) {
            logger.LogDebug("Duplicate event {EventId}, skipping", notificationEvent.EventId);
            return;
        }

        if (notificationEvent.EventType == NotificationEventType.PostCreated) {
            await notificationService.CreatePostCreatedNotificationsAsync(notificationEvent);
            return;
        }

        var notification = await notificationService.CreateNotificationAsync(notificationEvent);
        await streamService.PushNotificationAsync(notificationEvent.TargetUserId, notification);
    }
}