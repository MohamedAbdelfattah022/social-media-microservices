import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SignupRequest } from '../../shared/models/signup-request';
import { UserService } from '../../core/services/user.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-signup',
  imports: [FormsModule],
  templateUrl: './signup.html',
  styleUrl: './signup.css',
})
export class Signup {
  private userService = inject(UserService);
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

    this.userService.signup(this.signupData())
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
