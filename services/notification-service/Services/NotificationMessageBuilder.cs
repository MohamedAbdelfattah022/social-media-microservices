using notification_service.Constants;
using notification_service.Services.Interfaces;

namespace notification_service.Services;

public class NotificationMessageBuilder : INotificationMessageBuilder {
    public string BuildTitle(string eventType) {
        return eventType switch {
            NotificationEventType.UserFollowed => NotificationConstants.UserFollowedTitle,
            NotificationEventType.PostCreated => NotificationConstants.PostCreatedTitle,
            NotificationEventType.PostLiked => NotificationConstants.PostLikedTitle,
            NotificationEventType.PostCommented => NotificationConstants.PostCommentedTitle,
            NotificationEventType.CommentReplied => NotificationConstants.CommentRepliedTitle,
            NotificationEventType.CommentLiked => NotificationConstants.CommentLikedTitle,
            _ => "Notification"
        };
    }

    public string BuildMessage(string eventType, Dictionary<string, object>? metadata) {
        var template = GetMessageTemplate(eventType);
        return ReplacePlaceholders(template, metadata);
    }

    private string GetMessageTemplate(string eventType) {
        return eventType switch {
            NotificationEventType.UserFollowed => NotificationConstants.UserFollowedMessage,
            NotificationEventType.PostCreated => NotificationConstants.PostCreatedMessage,
            NotificationEventType.PostLiked => NotificationConstants.PostLikedMessage,
            NotificationEventType.PostCommented => NotificationConstants.PostCommentedMessage,
            NotificationEventType.CommentReplied => NotificationConstants.CommentRepliedMessage,
            NotificationEventType.CommentLiked => NotificationConstants.CommentLikedMessage,
            _ => NotificationConstants.DefaultMessage
        };
    }

    private string ReplacePlaceholders(string template, Dictionary<string, object>? metadata) {
        if (metadata == null) return template;

        var result = template;

        foreach (var (key, value) in metadata) {
            var placeholder = $"{{{key}}}";
            result = result.Replace(placeholder, value?.ToString() ?? string.Empty);
        }

        result = result.Replace($"{{{NotificationConstants.ActorUsername}}}", NotificationConstants.UnknownUser);
        result = result.Replace($"{{{NotificationConstants.CommentContent}}}", string.Empty);

        return result;
    }
}