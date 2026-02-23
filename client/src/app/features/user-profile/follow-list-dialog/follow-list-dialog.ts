import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '@/core/services/user.service';
import { UserProfileData } from '@/shared/models/users/user-profile-data';
import { ZardDialogRef, Z_MODAL_DATA } from '@/shared/components/dialog';
import { ZardIconComponent } from '@/shared/components/icon/icon.component';

export interface FollowListDialogData {
  userId: string;
  type: 'followers' | 'following';
}

@Component({
  selector: 'app-follow-list-dialog',
  imports: [ZardIconComponent],
  templateUrl: './follow-list-dialog.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowListDialog implements OnInit {
  private readonly dialogRef = inject(ZardDialogRef<FollowListDialog>);
  private readonly data = inject<FollowListDialogData>(Z_MODAL_DATA);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  protected readonly users = signal<UserProfileData[]>([]);
  protected readonly isLoading = signal(true);
  protected readonly error = signal<string | null>(null);

  protected get type(): 'followers' | 'following' {
    return this.data.type;
  }

  ngOnInit(): void {
    const request =
      this.data.type === 'followers'
        ? this.userService.getFollowers(this.data.userId)
        : this.userService.getFollowingUsers(this.data.userId);

    request.subscribe({
      next: (list) => {
        this.users.set(list);
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set('Failed to load users.');
        this.isLoading.set(false);
      },
    });
  }

  navigateToProfile(userId: string): void {
    this.dialogRef.close();
    this.router.navigate(['/profile', userId]);
  }

  getInitials(user: UserProfileData): string {
    return `${user.firstName[0]}${user.lastName[0]}`.toUpperCase();
  }
}
