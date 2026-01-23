import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SignupRequest } from '../../shared/models/auth/signup-request';
import { AuthService } from '../../core/services/auth.service';
import { Router, RouterLink } from '@angular/router';


@Component({
  selector: 'app-signup',
  imports: [FormsModule, RouterLink],
  templateUrl: './signup.html',
  styleUrl: './signup.css',
})
export class Signup {
  private authService = inject(AuthService);
  private router = inject(Router);

  signupData = signal<SignupRequest>({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
  });

  isLoading = signal(false);

  onSubmit() {
    if (this.isLoading()) return;

    console.log('Sign Up Data:', this.signupData());
    this.isLoading.set(true);

    this.authService.signup(this.signupData())
      .subscribe({
        next: (response) => {
          console.log('Signup successful:', response);
          this.isLoading.set(false);
          this.router.navigate(['/login']);
        },
        error: (error) => {
          console.log('Signup failed:', error);
          this.isLoading.set(false);
        }
      });
  }
}
