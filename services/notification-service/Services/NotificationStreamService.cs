using System.Collections.Concurrent;
using System.Text.Json;
using notification_service.Entities;
using notification_service.Mappers;
using notification_service.Services.Interfaces;

namespace notification_service.Services;

public class NotificationStreamService(ILogger<NotificationStreamService> logger) : INotificationStreamService {
    private readonly ConcurrentDictionary<string, StreamWriter> _connections = new();

    public void RegisterConnection(string userId, StreamWriter writer) {
        _connections[userId] = writer;
        logger.LogInformation("SSE connection registered for user {UserId}", userId);
    }

    public void UnregisterConnection(string userId) {
        _connections.TryRemove(userId, out _);
        logger.LogInformation("SSE connection unregistered for user {UserId}", userId);
    }

    public async Task PushNotificationAsync(string userId, Notification notification) {
        if (!_connections.TryGetValue(userId, out var writer)) {
            logger.LogDebug("No active SSE connection for user {UserId}", userId);
            return;
        }

        try {
            var dto = notification.ToDto();
            var json = JsonSerializer.Serialize(dto);

            await writer.WriteLineAsync($"data: {json}");
            await writer.WriteLineAsync();
            await writer.FlushAsync();

            logger.LogInformation("Pushed notification {NotificationId} to user {UserId} via SSE",
                notification.Id, userId);
        }
        catch (Exception ex) {
            logger.LogError(ex, "Failed to push notification to user {UserId}", userId);
            UnregisterConnection(userId);
        }
    }
}