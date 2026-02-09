using Microsoft.EntityFrameworkCore;
using notification_service.Entities;

namespace notification_service.Repositories;

public class NotificationRepository(NotificationDbContext dbContext) : INotificationRepository {
    private readonly DbSet<Notification> _notifications = dbContext.Notifications;

    public async Task<bool> ExistsByEventIdAsync(string eventId) {
        return await _notifications.AnyAsync(n => n.EventId == eventId);
    }

    public async Task<Notification> AddAsync(Notification notification) {
        _notifications.Add(notification);
        await dbContext.SaveChangesAsync();
        return notification;
    }

    public async Task<List<Notification>> GetByUserIdPaginatedAsync(
        string userId,
        DateTime? cursorTimestamp,
        long? cursorId,
        int pageSize) {
        var query = _notifications.Where(n => n.UserId == userId);

        if (cursorTimestamp.HasValue && cursorId.HasValue)
            query = query.Where(n =>
                n.CreatedAt < cursorTimestamp.Value ||
                (n.CreatedAt == cursorTimestamp.Value && n.Id < cursorId.Value));

        return await query
            .OrderByDescending(n => n.CreatedAt)
            .ThenByDescending(n => n.Id)
            .Take(pageSize + 1)
            .ToListAsync();
    }

    public async Task<Notification?> GetByIdAndUserIdAsync(long id, string userId) {
        return await _notifications
            .FirstOrDefaultAsync(n => n.Id == id && n.UserId == userId);
    }

    public async Task<List<Notification>> GetUnreadByUserIdAsync(string userId) {
        return await _notifications
            .Where(n => n.UserId == userId && n.ReadAt == null)
            .ToListAsync();
    }

    public async Task UpdateAsync(Notification notification) {
        _notifications.Update(notification);
        await dbContext.SaveChangesAsync();
    }

    public async Task UpdateRangeAsync(List<Notification> notifications) {
        _notifications.UpdateRange(notifications);
        await dbContext.SaveChangesAsync();
    }
}