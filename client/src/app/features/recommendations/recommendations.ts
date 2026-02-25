import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserProfileData } from '../../shared/models/users/user-profile-data';
import { LucideAngularModule, Users } from 'lucide-angular';
import { UserService } from '../../core/services/user.service';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-recommendations',
  imports: [LucideAngularModule, NgClass],
  templateUrl: './recommendations.html',
  styleUrl: './recommendations.css',
})
export class Recommendations implements OnInit {
  readonly Users = Users;
  suggestions: UserProfileData[] = [];
  followedUsers = new Set<string>();
  hoveredUser: string | null = null;

  private userService = inject(UserService);
  private router = inject(Router);

  ngOnInit(): void {
    this.loadSuggestions();
  }

  private loadSuggestions(): void {
    this.userService.getSuggestions(5).subscribe({
      next: (users) => (this.suggestions = users),
    });
  }

  onFollow(userId: string): void {
    this.userService.followUser(userId).subscribe({
      next: () => this.followedUsers.add(userId),
    });
  }

  onUnfollow(userId: string): void {
    this.userService.unfollowUser(userId).subscribe({
      next: () => this.followedUsers.delete(userId),
    });
  }

  toggleFollow(userId: string): void {
    if (this.followedUsers.has(userId)) {
      this.onUnfollow(userId);
    } else {
      this.onFollow(userId);
    }
  }

  navigateToProfile(userId: string): void {
    this.router.navigate(['/profile', userId]);
  }

  isFollowed(userId: string): boolean {
    return this.followedUsers.has(userId);
  }

  getButtonLabel(userId: string): string {
    if (!this.isFollowed(userId)) return 'Follow';
    return this.hoveredUser === userId ? 'Unfollow' : 'Following';
  }

  getInitials(user: UserProfileData): string {
    return `${user.firstName[0]}${user.lastName[0]}`.toUpperCase();
  }
}
