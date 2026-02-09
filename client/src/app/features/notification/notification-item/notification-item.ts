import { NotificationType } from '@/shared/models/notification/notification-type.enum';
import { NotificationDto } from '@/shared/models/notification/notification.dto';
import { ZardIconComponent, type ZardIcon } from '@/shared/components/icon';
import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';

@Component({
  selector: 'app-notification-item',
  imports: [CommonModule, ZardIconComponent],
  templateUrl: './notification-item.html',
  styleUrl: './notification-item.css',
})
export class NotificationItem {
  readonly notification = input.required<NotificationDto>();

  getIconBgClass(): string {
    switch (this.notification().type) {
      case NotificationType.PostLiked:
      case NotificationType.CommentLiked:
        return 'bg-red-100 dark:bg-red-900/20';
      case NotificationType.PostCommented:
      case NotificationType.CommentReplied:
        return 'bg-blue-100 dark:bg-blue-900/20';
      case NotificationType.UserFollowed:
        return 'bg-green-100 dark:bg-green-900/20';
      case NotificationType.PostCreated:
        return 'bg-purple-100 dark:bg-purple-900/20';
      default:
        return 'bg-gray-100 dark:bg-gray-700';
    }
  }

  getIconColorClass(): string {
    switch (this.notification().type) {
      case NotificationType.PostLiked:
      case NotificationType.CommentLiked:
        return 'text-red-600 dark:text-red-400';
      case NotificationType.PostCommented:
      case NotificationType.CommentReplied:
        return 'text-blue-600 dark:text-blue-400';
      case NotificationType.UserFollowed:
        return 'text-green-600 dark:text-green-400';
      case NotificationType.PostCreated:
        return 'text-purple-600 dark:text-purple-400';
      default:
        return 'text-gray-600 dark:text-gray-400';
    }
  }

  getIconType(): ZardIcon {
    switch (this.notification().type) {
      case NotificationType.PostLiked:
      case NotificationType.CommentLiked:
        return 'heart';
      case NotificationType.PostCommented:
      case NotificationType.CommentReplied:
        return 'message-circle';
      case NotificationType.UserFollowed:
        return 'user-plus';
      case NotificationType.PostCreated:
        return 'file-text';
      default:
        return 'bell';
    }
  }

  getRelativeTime(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);

    if (diffInSeconds < 60) {
      return 'just now';
    }

    const diffInMinutes = Math.floor(diffInSeconds / 60);
    if (diffInMinutes < 60) {
      return `${diffInMinutes}m ago`;
    }

    const diffInHours = Math.floor(diffInMinutes / 60);
    if (diffInHours < 24) {
      return `${diffInHours}h ago`;
    }

    const diffInDays = Math.floor(diffInHours / 24);
    if (diffInDays < 7) {
      return `${diffInDays}d ago`;
    }

    const diffInWeeks = Math.floor(diffInDays / 7);
    if (diffInWeeks < 4) {
      return `${diffInWeeks}w ago`;
    }

    return date.toLocaleDateString();
  }
}
