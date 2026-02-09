using System.Text.Json.Serialization;

namespace notification_service.DTOs;

public class NotificationDto {
    [JsonPropertyName("id")] public long Id { get; set; }
    [JsonPropertyName("eventId")] public string EventId { get; set; } = null!;
    [JsonPropertyName("type")] public string Type { get; set; } = null!;
    [JsonPropertyName("title")] public string Title { get; set; } = null!;
    [JsonPropertyName("message")] public string Message { get; set; } = null!;
    [JsonPropertyName("actorUserId")] public string ActorUserId { get; set; } = null!;
    [JsonPropertyName("resourceType")] public string ResourceType { get; set; } = null!;
    [JsonPropertyName("resourceId")] public string ResourceId { get; set; } = null!;
    [JsonPropertyName("metadata")] public Dictionary<string, object>? Metadata { get; set; }
    [JsonPropertyName("createdAt")] public DateTime CreatedAt { get; set; }
    [JsonPropertyName("readAt")] public DateTime? ReadAt { get; set; }
}