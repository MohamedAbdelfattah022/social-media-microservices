namespace notification_service.Services.Interfaces;

public interface INotificationMessageBuilder {
    string BuildTitle(string eventType);
    string BuildMessage(string eventType, Dictionary<string, object>? metadata);
}