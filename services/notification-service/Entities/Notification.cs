using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace notification_service.Entities;

public class Notification {
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public long Id { get; set; }

    [Required] [MaxLength(36)] public string EventId { get; set; } = null!;
    [Required] [MaxLength(36)] public string UserId { get; set; } = null!;
    [Required] [MaxLength(50)] public string Type { get; set; } = null!;
    [Required] [MaxLength(36)] public string ActorUserId { get; set; } = null!;
    [Required] [MaxLength(20)] public string ResourceType { get; set; } = null!;
    [Required] [MaxLength(50)] public string ResourceId { get; set; } = null!;
    [Required] [MaxLength(255)] public string Title { get; set; } = null!;
    [Required] public string Message { get; set; } = null!;
    [Column(TypeName = "jsonb")] public Dictionary<string, object>? Metadata { get; set; }
    [Required] public DateTime CreatedAt { get; set; }
    public DateTime? ReadAt { get; set; }
}