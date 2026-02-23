import { Component, computed, inject, OnDestroy, signal } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { UserProfileData } from '../../shared/models/users/user-profile-data';
import { ActivatedRoute } from '@angular/router';
import { ZardIconComponent } from '../../shared/components/icon/icon.component';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { Feed } from '../feed/feed';
import { ZardDialogService } from '@/shared/components/dialog';
import { EditProfileDialog } from '@/features/edit-profile';
import { ZardButtonComponent } from '@/shared/components/button/button.component';
import {
  FollowListDialog,
  FollowListDialogData,
} from './follow-list-dialog/follow-list-dialog';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-user-profile',
  imports: [CommonModule, ZardIconComponent, Feed, ZardButtonComponent],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.css',
})
export class UserProfile implements OnDestroy {
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private readonly dialogService = inject(ZardDialogService);

  protected readonly userId = signal<string | null>(null);

  protected readonly profile = signal<UserProfileData | null>(null);
  protected readonly isLoading = signal<boolean>(true);
  protected readonly error = signal<string | null>(null);
  protected readonly isFollowing = signal(false);
  protected readonly isFollowLoading = signal(false);
  protected readonly isMyProfile = computed(() => {
    const currentUserId = this.authService.currentUserId();
    const uid = this.userId();
    return !!uid && !!currentUserId && uid === currentUserId;
  });

  private readonly routeSub: Subscription;

  constructor() {
    this.routeSub = this.route.paramMap.subscribe((params) => {
      const uid = params.get('userId');
      this.userId.set(uid);
      this.isFollowing.set(false);

      if (uid) {
        this.loadUserProfile(uid);
      } else {
        this.isLoading.set(false);
        this.error.set('User ID not found in route.');
      }
    });
  }

  ngOnDestroy(): void {
    this.routeSub.unsubscribe();
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
    const uid = this.userId();
    if (!uid || this.isFollowLoading()) return;

    this.isFollowLoading.set(true);

    const followRequest = this.isFollowing()
      ? this.userService.unfollowUser(uid)
      : this.userService.followUser(uid);

    followRequest.subscribe({
      next: () => {
        this.isFollowing.update((isFollowing) => !isFollowing);
        this.isFollowLoading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to update follow status.');
        console.error(err);
        this.isFollowLoading.set(false);
      },
    });
  }

  openFollowListDialog(type: 'followers' | 'following'): void {
    const uid = this.userId();
    if (!uid) return;

    const title = type === 'followers' ? 'Followers' : 'Following';
    this.dialogService.create<FollowListDialog, FollowListDialogData>({
      zTitle: title,
      zContent: FollowListDialog,
      zData: { userId: uid, type },
      zHideFooter: true,
      zWidth: '420px',
    });
  }

  openEditProfileDialog(): void {
    const ref = this.dialogService.create<EditProfileDialog, UserProfileData>({
      zTitle: 'Edit Profile',
      zContent: EditProfileDialog,
      zData: this.profile()!,
      zHideFooter: true,
      zWidth: '480px',
    });

    ref.afterClosed().subscribe((result) => {
      if (result) {
        this.profile.update((prev) =>
          prev
            ? {
              ...prev,
              firstName: result.firstName ?? prev.firstName,
              lastName: result.lastName ?? prev.lastName,
              bio: result.bio ?? prev.bio,
              profilePictureUrl: result.profilePictureUrl ?? prev.profilePictureUrl,
            }
            : prev,
        );
      }
    });
  }
}
