import { Component, computed, effect, inject, signal } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { UserProfileData } from '../../shared/models/users/user-profile-data';
import { ActivatedRoute } from '@angular/router';
import { LucideAngularModule, Mail, Calendar } from 'lucide-angular';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { PostData } from '../../shared/models/post-data';
import { PostCard } from "../post/post-card";
import { LoadingSkeleton } from "../../shared/loading-skeleton/loading-skeleton";

@Component({
  selector: 'app-user-profile',
  imports: [LucideAngularModule, CommonModule, PostCard, LoadingSkeleton],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.css',
})
export class UserProfile {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  protected readonly posts = signal<PostData[]>(mockUserPosts);
  protected readonly isLoadingMore = signal(false);
  protected readonly hasNext = signal(true);

  protected readonly profile = signal<UserProfileData | null>(null);
  protected readonly isLoading = signal<boolean>(true);
  protected readonly error = signal<string | null>(null);
  protected readonly isFollowing = signal(false);
  protected readonly isMyProfile = signal(false);

  protected readonly Mail = Mail;
  protected readonly Calendar = Calendar;

  protected readonly hasNoPosts = computed(() => this.posts().length === 0);


  constructor() {
    const userId = this.route.snapshot.paramMap.get('userId');
    effect(() => {
      if (userId) this.loadUserProfile(userId);

      const currId = this.authService.currentUserId();
      if (currId) this.isMyProfile.set(userId === currId);
    });
  }

  loadUserProfile(userId: string) {
    this.userService.getUserProfile(userId).subscribe(
      {
        next: (data: UserProfileData) => {
          this.profile.set(data);
          this.isLoading.set(false);
        },
        error: (err) => {
          this.error.set('Failed to load user profile.');
          console.error(err);
          this.isLoading.set(false);
        }
      }
    );
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
    this.formatCount(this.profile()?.followingCount ?? 0)
  );

  protected readonly formattedFollowers = computed(() =>
    this.formatCount(this.profile()?.followerCount ?? 0)
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
    this.isFollowing.update(isFollowing => !isFollowing);
  }
}

const mockUserPosts: PostData[] = [
  {
    id: 1,
    userId: "5e35d9a3-37cd-4da9-ac98-a0f6be6e98e7",
    username: "mohamed-abdelfattah",
    firstName: "Mohamed",
    lastName: "Abdelfattah",
    profilePictureUrl: "https://loremflickr.com/400/400?lock=7826618384397895",
    content: "Just finished an amazing hike in the mountains! The views were absolutely breathtaking. Nature really has a way of putting everything into perspective. 🏔️",
    privacy: "public",
    mediaUrls: ["https://images.unsplash.com/photo-1617634667039-8e4cb277ab46?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080"],
    isEdited: false,
    likeCount: 324,
    commentCount: 28,
    createdAt: "3h ago",
    updatedAt: "2025-01-23T10:00:00Z",
  },
  {
    id: 2,
    userId: "5e35d9a3-37cd-4da9-ac98-a0f6be6e98e7",
    username: "mohamed-abdelfattah",
    firstName: "Mohamed",
    lastName: "Abdelfattah",
    profilePictureUrl: "https://loremflickr.com/400/400?lock=7826618384397895",
    content: "Morning walks in the forest are my favorite way to start the day. The fresh air and peaceful surroundings clear my mind like nothing else.",
    privacy: "public",
    mediaUrls: [
      "https://images.unsplash.com/photo-1511497584788-876760111969?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080",
      "https://images.unsplash.com/photo-1542273917363-3b1817f69a2d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080",
    ],
    isEdited: false,
    likeCount: 567,
    commentCount: 43,
    createdAt: "1d ago",
    updatedAt: "2025-01-22T08:00:00Z",
  },
  {
    id: 3,
    userId: "5e35d9a3-37cd-4da9-ac98-a0f6be6e98e7",
    username: "mohamed-abdelfattah",
    firstName: "Mohamed",
    lastName: "Abdelfattah",
    profilePictureUrl: "https://loremflickr.com/400/400?lock=7826618384397895",
    content: "Captured this stunning sunset during my evening hike yesterday. Sometimes you just need to stop and appreciate the beauty around you. 🌅",
    privacy: "public",
    mediaUrls: ["https://images.unsplash.com/photo-1506905925346-21bda4d32df4?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080"],
    isEdited: true,
    likeCount: 892,
    commentCount: 67,
    createdAt: "2d ago",
    updatedAt: "2025-01-21T18:30:00Z",
  },
  {
    id: 4,
    userId: "5e35d9a3-37cd-4da9-ac98-a0f6be6e98e7",
    username: "mohamed-abdelfattah",
    firstName: "Mohamed",
    lastName: "Abdelfattah",
    profilePictureUrl: "https://loremflickr.com/400/400?lock=7826618384397895",
    content: "Weekend camping trip was exactly what I needed! Disconnecting from technology and reconnecting with nature is so refreshing.",
    privacy: "friends",
    mediaUrls: [
      "https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080",
      "https://images.unsplash.com/photo-1478131143081-80f7f84ca84d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080",
      "https://images.unsplash.com/photo-1537905569824-f89f14cceb68?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080",
    ],
    isEdited: false,
    likeCount: 1243,
    commentCount: 89,
    createdAt: "3d ago",
    updatedAt: "2025-01-20T12:00:00Z",
  },
];
