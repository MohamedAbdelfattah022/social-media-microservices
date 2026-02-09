using System.Globalization;
using System.Text;
using Microsoft.IdentityModel.Tokens;
using notification_service.Services.Interfaces;

namespace notification_service.Services;

public class CursorPaginationService : ICursorPaginationService {
    public string EncodeCursor(DateTime timestamp, string id) {
        var cursor = $"{timestamp:O},{id}";
        var bytes = Encoding.UTF8.GetBytes(cursor);
        return Base64UrlEncoder.Encode(bytes);
    }

    public (DateTime? timestamp, long? id) DecodeCursor(string? cursor) {
        if (string.IsNullOrEmpty(cursor)) return (null, null);

        try {
            var content = Base64UrlEncoder.Decode(cursor);
            var parts = content.Split(',');

            var timestamp = DateTime.Parse(parts[0], CultureInfo.InvariantCulture, DateTimeStyles.RoundtripKind);
            var id = long.Parse(parts[1]);

            return (timestamp, id);
        }
        catch {
            return (null, null);
        }
    }
}