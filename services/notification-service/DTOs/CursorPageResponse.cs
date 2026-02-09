namespace notification_service.DTOs;

public class CursorPageResponse<T> {
    public List<T> Data { get; set; } = [];
    public string? NextCursor { get; set; }
    public bool HasNext { get; set; }
    public int PageSize { get; set; }
}