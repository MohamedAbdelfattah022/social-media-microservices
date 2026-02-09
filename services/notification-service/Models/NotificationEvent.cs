namespace notification_service.Models;

public class NotificationEvent {
    public string EventId { get; set; } = null!;
    public string EventType { get; set; } = null!;
    public string SourceService { get; set; } = null!;
    public double Timestamp { get; set; }
    public string ActorUserId { get; set; } = null!;
    public string TargetUserId { get; set; } = null!;
    public string ResourceType { get; set; } = null!;
    public string ResourceId { get; set; } = null!;
    public Dictionary<string, object>? Metadata { get; set; }
}