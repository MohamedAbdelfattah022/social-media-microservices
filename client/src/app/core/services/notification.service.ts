import { NotificationDto } from '@/shared/models/notification/notification.dto';
import { AuthService } from './auth.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { effect, inject, Injectable, OnDestroy, signal } from '@angular/core';
import { catchError, Observable, of, Subject, takeUntil, timer } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CursorPaginationResponse } from '@/shared/models/cursor-pagination-response';

@Injectable({
  providedIn: 'root',
})
export class NotificationService implements OnDestroy {
  private readonly http = inject(HttpClient);
  private readonly AuthService = inject(AuthService);
  private readonly apiUrl = environment.apiUrl + '/notifications';

  private eventSource: EventSource | null = null;
  private reconnectDelay = 3000;
  private maxReconnectDelay = 30000;
  private currentReconnectDelay = this.reconnectDelay;
  private destroy$ = new Subject<void>();

  readonly notifications = signal<NotificationDto[]>([]);
  readonly unreadCount = signal<number>(0);
  readonly loading = signal<boolean>(false);
  readonly connected = signal<boolean>(false);
  readonly nextCursor = signal<string | null>(null);
  readonly hasMore = signal<boolean>(true);

  private newNotification$ = new Subject<NotificationDto>();

  constructor() {
    effect(() => {
      if (this.AuthService.isLoggedIn()) {
        this.connectSSE();
        this.loadInitialNotifications();
      } else {
        this.disconnectSSE();
        this.notifications.set([]);
        this.unreadCount.set(0);
      }
    });
  }

  connectSSE() {
    if (this.eventSource) return;

    const token = this.AuthService.getToken();
    if (!token) {
      console.warn('Cannot connect to notifications SSE: No auth token found');
      return;
    }

    const url = `${this.apiUrl}/stream?access_token=${token}`;
    this.eventSource = new EventSource(url);

    this.eventSource.onopen = () => {
      console.log('Connected to notifications SSE');
      this.connected.set(true);
      this.currentReconnectDelay = this.reconnectDelay;
    };

    this.eventSource.onmessage = (event) => {
      try {
        const notification: NotificationDto = JSON.parse(event.data);
        this.handleNewNotification(notification);
      } catch (error) {
        console.error('Error parsing notification:', error);
      }
    };

    this.eventSource.onerror = (error) => {
      console.error('Notifications SSE error:', error);
      this.connected.set(false);
      this.eventSource?.close();
      this.eventSource = null;

      this.scheduleReconnect();
    };
  }

  scheduleReconnect() {
    if (!this.AuthService.isLoggedIn()) return;

    console.log(`Reconnecting to notifications SSE in ${this.currentReconnectDelay / 1000} seconds...`);

    timer(this.currentReconnectDelay)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.connectSSE();
        this.currentReconnectDelay = Math.min(this.currentReconnectDelay * 2, this.maxReconnectDelay);
      });
  }

  disconnectSSE() {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
      this.connected.set(false);
    }
  }

  handleNewNotification(notification: NotificationDto) {
    const normalized = this.normalizeNotification(notification);
    this.notifications.update((current) => [normalized, ...current]);
    if (!normalized.read)
      this.unreadCount.update((count) => count + 1);

    this.newNotification$.next(normalized);
  }

  private normalizeNotification(n: NotificationDto): NotificationDto {
    return {
      ...n,
      read: n.readAt != null
    };
  }

  loadInitialNotifications() {
    this.loading.set(true);
    this.getNotifications(15).subscribe({
      next: (response) => {
        const normalized = response.data.map(n => this.normalizeNotification(n));
        this.notifications.set(normalized);
        this.unreadCount.set(normalized.filter((n) => !n.read).length);
        this.nextCursor.set(response.nextCursor);
        this.hasMore.set(response.hasNext);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error loading notifications:', error);
        this.loading.set(false);
      },
    });
  }

  getNotifications(pageSize: number, cursor?: string): Observable<CursorPaginationResponse<NotificationDto>> {
    let params = new HttpParams().set('pageSize', pageSize.toString());
    if (cursor) params = params.set('cursor', cursor);

    return this.http.get<CursorPaginationResponse<NotificationDto>>(this.apiUrl, { params });
  }

  markAsRead(notificationId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${notificationId}/read`, {})
      .pipe(
        catchError(error => {
          console.error('Failed to mark notification as read:', error);
          return of(void 0);
        })
      );
  }

  markAllAsRead(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/read-all`, {}).pipe(
      catchError(error => {
        console.error('Failed to mark all notifications as read:', error);
        return of(void 0);
      })
    );
  }


  updateNotificationReadStatus(notificationId: string, read: boolean): void {
    this.notifications.update(notifications =>
      notifications.map(n =>
        n.id === notificationId ? { ...n, read, readAt: read ? (n.readAt || new Date().toISOString()) : null } : n
      )
    );

    const unread = this.notifications().filter(n => !n.read).length;
    this.unreadCount.set(unread);
  }

  updateAllNotificationsReadStatus(): void {
    const now = new Date().toISOString();
    this.notifications.update(notifications =>
      notifications.map(n => ({ ...n, read: true, readAt: n.readAt || now }))
    );
    this.unreadCount.set(0);
  }

  onNewNotification(): Observable<NotificationDto> {
    return this.newNotification$.asObservable();
  }

  loadMore(cursor: string): Observable<CursorPaginationResponse<NotificationDto>> {
    return this.getNotifications(15, cursor);
  }

  appendNormalizedNotifications(response: CursorPaginationResponse<NotificationDto>): void {
    const normalized = response.data.map(n => this.normalizeNotification(n));
    this.notifications.update(current => [...current, ...normalized]);
    this.nextCursor.set(response.nextCursor);
    this.hasMore.set(response.hasNext);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    this.disconnectSSE();
  }
}
