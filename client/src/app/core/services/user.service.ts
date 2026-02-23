import { UpdateUserProfileRequest } from './../../shared/models/users/update-user-profile-request';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { UserProfileData } from '../../shared/models/users/user-profile-data';
import { FileUploadResponse } from '@/shared/models/files/file-upload-response';

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

  updateUserProfile(request: UpdateUserProfileRequest): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/users/me`, request);
  }

  uploadProfilePicture(file: File): Observable<{ profilePictureUrl: string; }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ profilePictureUrl: string; }>(
      `${this.apiUrl}/users/me/profile-picture`,
      formData,
    );
  }

  deleteProfilePicture(): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/me/profile-picture`);
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
