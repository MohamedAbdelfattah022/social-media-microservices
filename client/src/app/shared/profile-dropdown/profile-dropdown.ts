import { Component, inject, signal } from '@angular/core';
import { LucideAngularModule, Menu, LogOut, User } from 'lucide-angular';
import { AuthService } from '../../core/services/auth.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-profile-dropdown',
  imports: [LucideAngularModule, RouterLink],
  templateUrl: './profile-dropdown.html',
  styleUrl: './profile-dropdown.css',
})
export class ProfileDropdown {
  readonly menu = Menu;
  readonly logOutIcon = LogOut;
  readonly userIcon = User;

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
