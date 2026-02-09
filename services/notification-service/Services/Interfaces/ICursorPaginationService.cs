namespace notification_service.Services.Interfaces;

public interface ICursorPaginationService {
    string EncodeCursor(DateTime timestamp, string id);
    (DateTime? timestamp, long? id) DecodeCursor(string? cursor);
}