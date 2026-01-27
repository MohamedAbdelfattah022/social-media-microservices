import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './shared/navbar/navbar';
import { ZardToastComponent } from './shared/components/toast';

@Component({
  selector: 'app-root',
  imports: [Navbar, RouterOutlet, ZardToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('Social Mesh');
}
