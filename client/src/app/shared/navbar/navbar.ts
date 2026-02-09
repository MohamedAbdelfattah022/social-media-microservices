import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ProfileDropdown } from '../profile-dropdown/profile-dropdown';
import { Notification } from '@/features/notification/notification';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, ProfileDropdown, Notification],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  authService = inject(AuthService);
  isLoggedIn = this.authService.isLoggedIn;
}
