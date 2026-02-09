using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using notification_service.Config;
using notification_service.DTOs;
using notification_service.Services.Interfaces;

namespace notification_service.Controllers;

[Authorize]
[ApiController]
[Route("api/notifications")]
public class NotificationController(
    INotificationService notificationService,
    INotificationStreamService streamService) : ControllerBase {
    [HttpGet]
    public async Task<ActionResult<CursorPageResponse<NotificationDto>>> GetNotifications(
        [FromQuery] string? cursor = null,
        [FromQuery] int pageSize = 10) {
        var userId = User.GetUserIdFromClaims();
        if (string.IsNullOrEmpty(userId)) return Unauthorized();

        var response = await notificationService.GetNotificationsAsync(userId, cursor, pageSize);
        return Ok(response);
    }

    [HttpPost("{id:long}/read")]
    public async Task<IActionResult> MarkAsRead(long id) {
        var userId = User.GetUserIdFromClaims();
        if (string.IsNullOrEmpty(userId)) return Unauthorized();

        var success = await notificationService.MarkAsReadAsync(id, userId);
        if (!success) return NotFound();

        return NoContent();
    }

    [HttpPost("read-all")]
    public async Task<IActionResult> MarkAllAsRead() {
        var userId = User.GetUserIdFromClaims();
        if (string.IsNullOrEmpty(userId))
            return Unauthorized();

        await notificationService.MarkAllAsReadAsync(userId);
        return NoContent();
    }

    [HttpGet("stream")]
    public async Task StreamNotifications() {
        var userId = User.GetUserIdFromClaims();
        if (string.IsNullOrEmpty(userId)) {
            Response.StatusCode = 401;
            return;
        }

        Response.Headers.Append("Content-Type", "text/event-stream");
        Response.Headers.Append("Cache-Control", "no-cache");
        Response.Headers.Append("Connection", "keep-alive");

        var writer = new StreamWriter(Response.Body);
        streamService.RegisterConnection(userId, writer);

        try {
            await writer.WriteLineAsync(": keepalive");
            await writer.FlushAsync();

            var tcs = new TaskCompletionSource();
            await using var registration = HttpContext.RequestAborted.Register(() => tcs.SetResult());
            await tcs.Task;
        }
        finally {
            streamService.UnregisterConnection(userId);
        }
    }
}