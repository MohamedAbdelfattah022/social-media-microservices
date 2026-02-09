import { NotificationDto } from '@/shared/models/notification/notification.dto';
import { NotificationService } from './../../../core/services/notification.service';
import { Component, inject, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationItem } from '../notification-item/notification-item';
import { ZardIconComponent } from '@/shared/components/icon/icon.component';

@Component({
  selector: 'app-notification-dropdown',
  imports: [CommonModule, NotificationItem, ZardIconComponent],
  templateUrl: './notification-dropdown.html',
  styleUrl: './notification-dropdown.css',
})
export class NotificationDropdown {
  private notificationService = inject(NotificationService);

  readonly close = output<void>();

  readonly notifications = this.notificationService.notifications;
  readonly unreadCount = this.notificationService.unreadCount;
  readonly loading = this.notificationService.loading;
  readonly hasMore = this.notificationService.hasMore;
  readonly loadingMore = signal(false);

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.notificationService.updateAllNotificationsReadStatus();
      }
    });
  }

  handleNotificationClick(notification: NotificationDto): void {
    if (!notification.read) {
      this.notificationService.markAsRead(notification.id).subscribe({
        next: () => {
          this.notificationService.updateNotificationReadStatus(notification.id, true);
        }
      });
    }

    this.navigateToEntity(notification);
    this.close.emit();
  }

  loadMore(): void {
    const cursor = this.notificationService.nextCursor();
    if (!cursor || this.loadingMore()) {
      return;
    }

    this.loadingMore.set(true);

    this.notificationService.loadMore(cursor).subscribe({
      next: (response) => {
        this.notificationService.appendNormalizedNotifications(response);
        this.loadingMore.set(false);
      },
      error: (error) => {
        console.error('Failed to load more notifications:', error);
        this.loadingMore.set(false);
      }
    });
  }

  private navigateToEntity(notification: NotificationDto): void {
    switch (notification.resourceType) {
      case 'POST':
        window.location.href = `/posts/${notification.resourceId}`;
        break;
      case 'USER':
        window.location.href = `/users/${notification.resourceId}`;
        break;
      case 'COMMENT':
        const postId = notification.metadata?.['postId'];
        if (postId) {
          window.location.href = `/posts/${postId}#comment-${notification.resourceId}`;
        }
        break;
      default:
        console.warn('Unknown resource type:', notification.resourceType);
    }
  }
}
