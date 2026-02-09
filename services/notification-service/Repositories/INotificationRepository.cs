using notification_service.Entities;

namespace notification_service.Repositories;

public interface INotificationRepository {
    Task<bool> ExistsByEventIdAsync(string eventId);
    Task<Notification> AddAsync(Notification notification);

    Task<List<Notification>> GetByUserIdPaginatedAsync(string userId, DateTime? cursorTimestamp, long? cursorId,
        int pageSize);

    Task<Notification?> GetByIdAndUserIdAsync(long id, string userId);
    Task<List<Notification>> GetUnreadByUserIdAsync(string userId);
    Task UpdateAsync(Notification notification);
    Task UpdateRangeAsync(List<Notification> notifications);
}