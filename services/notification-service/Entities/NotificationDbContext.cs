using Microsoft.EntityFrameworkCore;

namespace notification_service.Entities;

public class NotificationDbContext(DbContextOptions<NotificationDbContext> options) : DbContext(options) {
    public DbSet<Notification> Notifications { get; set; } = null!;

    protected override void OnModelCreating(ModelBuilder modelBuilder) {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<Notification>(entity => {
            entity.HasIndex(e => e.EventId).IsUnique();
            entity.HasIndex(e => new { e.UserId, e.CreatedAt });
        });
    }
}