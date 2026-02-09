import { NotificationService } from '@/core/services/notification.service';
import { Component, computed, inject, signal, ElementRef, HostListener } from '@angular/core';
import { NotificationDropdown } from './notification-dropdown/notification-dropdown';
import { CommonModule } from '@angular/common';
import { ZardIconComponent } from '@/shared/components/icon/icon.component';

@Component({
  selector: 'app-notification',
  imports: [CommonModule, NotificationDropdown, ZardIconComponent],
  templateUrl: './notification.html',
  styleUrl: './notification.css',
})
export class Notification {
  private notificationService = inject(NotificationService);
  private elementRef = inject(ElementRef);

  readonly unreadCount = this.notificationService.unreadCount;
  readonly connected = this.notificationService.connected;
  readonly isOpen = signal(false);

  readonly displayCount = computed(() => {
    const count = this.unreadCount();
    return count > 99 ? '99+' : count.toString();
  });

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target as Node)) {
      this.closeDropdown();
    }
  }

  toggleDropdown(): void {
    this.isOpen.update(open => !open);
  }

  closeDropdown(): void {
    this.isOpen.set(false);
  }
}
