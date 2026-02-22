import { UpdateUserProfileRequest } from './../../shared/models/users/update-user-profile-request';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { UserProfileData } from '../../shared/models/users/user-profile-data';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  getUserProfile(userId: string): Observable<UserProfileData> {
    return this.http.get<UserProfileData>(`${this.apiUrl}/users/${userId}`);
  }

  getFollowingUsers(userId: string): Observable<UserProfileData[]> {
    return this.http.get<UserProfileData[]>(`${this.apiUrl}/users/${userId}/following`);
  }

  getFollowers(userId: string): Observable<UserProfileData[]> {
    return this.http.get<UserProfileData[]>(`${this.apiUrl}/users/${userId}/followers`);
  }

  followUser(followeeId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/users/follow/${followeeId}`, {});
  }

  unfollowUser(followeeId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/follow/${followeeId}`);
  }

  updateUserProfile(request: UpdateUserProfileRequest): Observable<UserProfileData> {
    return this.http.patch<UserProfileData>(`${this.apiUrl}/users/profile`, request);
  }

  getSuggestions(limit: number = 5): Observable<UserProfileData[]> {
    return this.http.get<UserProfileData[]>(
      `${this.apiUrl}/users/suggestions`, { params: { limit } }
    );
  }

  searchUsers(query: string, limit: number = 10): Observable<UserProfileData[]> {
    return this.http.get<UserProfileData[]>(`${this.apiUrl}/users/search`, {
      params: { query, limit: limit.toString() }
    });
  }
}
