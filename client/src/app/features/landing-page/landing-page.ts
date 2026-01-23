import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LucideAngularModule, Twitter, Github } from 'lucide-angular';

@Component({
  selector: 'app-landing-page',
  imports: [RouterLink, LucideAngularModule],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.css',
})
export class LandingPage {
  readonly twitter = Twitter;
  readonly github = Github;
}
