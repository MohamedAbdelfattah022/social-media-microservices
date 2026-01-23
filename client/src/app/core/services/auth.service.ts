import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { environment } from '../../../environments/environment';
import { SignupRequest } from '../../shared/models/auth/signup-request';
import { Observable, tap } from 'rxjs';
import { LoginRequest } from '../../shared/models/auth/login-request';
import { AuthResponse } from '../../shared/models/auth/Auth-response';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;
  private readonly router = inject(Router);

  private readonly auth = {
    tokenUrl: environment.auth.tokenUrl,
    clientId: environment.auth.clientId
  };

  readonly isLoggedIn = signal(this.hasTokens());

  signup(request: SignupRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/signup`, request, { responseType: 'text' });
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    const body = new URLSearchParams();
    body.set('grant_type', 'password');
    body.set('client_id', this.auth.clientId);
    body.set('username', request.username);
    body.set('password', request.password);

    return this.http.post<AuthResponse>(this.auth.tokenUrl, body.toString(), {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    }).pipe(
      tap({
        next: response => {
          localStorage.setItem('access_token', response.access_token);
          localStorage.setItem('refresh_token', response.refresh_token);
          this.isLoggedIn.set(true);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    this.router.navigate(['/login']);
    this.isLoggedIn.set(false);
  }

  private hasTokens(): boolean {
    return !!localStorage.getItem('access_token') && !!localStorage.getItem('refresh_token');
  }

  getToken(): string | null {
    return localStorage.getItem('access_token');
  }

  decodeToken(): any {
    const token = localStorage.getItem('access_token');
    if (!token) return null;

    const payload = token.split('.')[1];
    const decodedPayload = atob(payload);
    return JSON.parse(decodedPayload);
  }


  currentUserId() {
    return this.decodeToken()?.sub || null;
  }
}
