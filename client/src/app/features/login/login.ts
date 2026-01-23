import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { LoginRequest } from '../../shared/models/auth/login-request';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private authService = inject(AuthService);
  private router = inject(Router);

  loginData = signal<LoginRequest>({ username: '', password: '' });

  onSubmit() {
    this.authService.login(this.loginData()).subscribe({
      next: () => this.router.navigate(['/home']),
      error: (error) => console.error('Login failed:', error)
    });
  }
}
