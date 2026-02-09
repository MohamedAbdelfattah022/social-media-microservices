namespace notification_service.Constants;

public static class NotificationConstants {
    public const string UserFollowedTitle = "New Follower";
    public const string UserFollowedMessage = "{actorUsername} started following you";

    public const string PostCreatedTitle = "New Post";
    public const string PostCreatedMessage = "{actorUsername} shared a new post";

    public const string PostLikedTitle = "New Like";
    public const string PostLikedMessage = "{actorUsername} liked your post";

    public const string PostCommentedTitle = "New Comment";
    public const string PostCommentedMessage = "{actorUsername} commented on your post: \"{commentContent}\"";

    public const string CommentRepliedTitle = "New Reply";
    public const string CommentRepliedMessage = "{actorUsername} replied to your comment: \"{commentContent}\"";

    public const string CommentLikedTitle = "New Like";
    public const string CommentLikedMessage = "{actorUsername} liked your comment";

    public const string ActorUsername = "actorUsername";
    public const string CommentContent = "commentContent";
    public const string FollowerCount = "followerCount";

    public const string UnknownUser = "Unknown User";
    public const string DefaultMessage = "You have a new notification";
}