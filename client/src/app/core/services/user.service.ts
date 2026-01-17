import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { SignupRequest } from '../../shared/models/signup-request';
import { Observable } from 'rxjs';
import { LoginRequest } from '../../shared/models/login-request';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  private auth = {
    tokenUrl: environment.auth.tokenUrl,
    clientId: environment.auth.clientId
  };

  signup(request: SignupRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/signup`, request, { responseType: 'text' });
  }


  login(request: LoginRequest): Observable<any> {
    const body = new URLSearchParams();
    body.set('grant_type', 'password');
    body.set('client_id', this.auth.clientId);
    body.set('username', request.username);
    body.set('password', request.password);

    return this.http.post(this.auth.tokenUrl, body.toString(), {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    });

  }
}
