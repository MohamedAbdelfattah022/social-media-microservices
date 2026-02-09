using notification_service.Entities;

namespace notification_service.Services.Interfaces;

public interface INotificationStreamService {
    void RegisterConnection(string userId, StreamWriter writer);
    void UnregisterConnection(string userId);
    Task PushNotificationAsync(string userId, Notification notification);
}