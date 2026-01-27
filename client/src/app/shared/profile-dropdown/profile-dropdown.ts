import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { RouterLink } from '@angular/router';
import { ZardIconComponent } from "../components/icon";

@Component({
  selector: 'app-profile-dropdown',
  imports: [RouterLink, ZardIconComponent],
  templateUrl: './profile-dropdown.html',
  styleUrl: './profile-dropdown.css',
})
export class ProfileDropdown {
  authService = inject(AuthService);
  isMenuOpen = signal(false);
  userId: string = this.authService.decodeToken()?.sub;

  toggleMenu() {
    this.isMenuOpen.update((isOpen) => !isOpen);
  }

  closeMenu() {
    this.isMenuOpen.set(false);
  }

  logout() {
    this.authService.logout();
    this.isMenuOpen.set(false);
  }
}
