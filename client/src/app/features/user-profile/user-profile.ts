import { Component, computed, effect, inject, signal } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { UserProfileData } from '../../shared/models/users/user-profile-data';
import { ActivatedRoute } from '@angular/router';
import { ZardIconComponent } from '../../shared/components/icon/icon.component';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { Feed } from '../feed/feed';

@Component({
  selector: 'app-user-profile',
  imports: [CommonModule, ZardIconComponent, Feed],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.css',
})
export class UserProfile {
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);

  protected readonly userId: string | null;

  protected readonly profile = signal<UserProfileData | null>(null);
  protected readonly isLoading = signal<boolean>(true);
  protected readonly error = signal<string | null>(null);
  protected readonly isFollowing = signal(false);
  protected readonly isMyProfile = computed(() => {
    const currentUserId = this.authService.currentUserId();
    return !!this.userId && !!currentUserId && this.userId === currentUserId;
  });

  constructor() {
    this.userId = this.route.snapshot.paramMap.get('userId');

    if (this.userId) {
      this.loadUserProfile(this.userId);
    } else {
      this.isLoading.set(false);
      this.error.set('User ID not found in route.');
    }
  }

  loadUserProfile(userId: string) {
    this.isLoading.set(true);
    this.userService.getUserProfile(userId).subscribe({
      next: (data: UserProfileData) => {
        this.profile.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load user profile.');
        console.error(err);
        this.isLoading.set(false);
      },
    });
  }

  protected readonly initials = computed(() => {
    const prof = this.profile();
    return prof ? `${prof.firstName[0]}${prof.lastName[0]}`.toUpperCase() : '';
  });

  protected readonly displayName = computed(() => {
    const prof = this.profile();
    return prof ? `${prof.firstName} ${prof.lastName}` : '';
  });

  protected readonly formattedFollowing = computed(() =>
    this.formatCount(this.profile()?.followingCount ?? 0),
  );

  protected readonly formattedFollowers = computed(() =>
    this.formatCount(this.profile()?.followerCount ?? 0),
  );

  private formatCount(count: number): string {
    if (count >= 1000000) {
      return `${(count / 1000000).toFixed(1)}M`;
    }
    if (count >= 1000) {
      return `${(count / 1000).toFixed(1)}K`;
    }
    return count.toString();
  }

  handleFollow(): void {
    this.isFollowing.update((isFollowing) => !isFollowing);
  }
}
