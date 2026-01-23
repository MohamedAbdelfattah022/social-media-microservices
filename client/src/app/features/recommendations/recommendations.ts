import { Component } from '@angular/core';
import { UserProfileData } from '../../shared/models/users/user-profile-data';
import { LucideAngularModule, Users } from 'lucide-angular';

@Component({
  selector: 'app-recommendations',
  imports: [LucideAngularModule],
  templateUrl: './recommendations.html',
  styleUrl: './recommendations.css',
})
export class Recommendations {
  readonly Users = Users;
  suggestions: UserProfileData[] = [
    {
      id: '57c72aef-a970-42a4-83b3-75018c74dcd5',
      email: 'alex.rivera@example.com',
      username: 'arivera_dev',
      firstName: 'Alex',
      lastName: 'Rivera',
      bio: 'Building the future of web apps with Angular and Spring Boot. 🚀',
      profilePictureUrl: 'https://i.pravatar.cc/150?u=arivera',
      followerCount: 1250,
      followingCount: 430
    },
    {
      id: 'bc21918a-3e5d-4b14-9f7e-6f8123abc456',
      email: 'sarah.smith@example.com',
      username: 'sarah_design',
      firstName: 'Sarah',
      lastName: 'Smith',
      bio: 'UI/UX Designer | Nature lover | Coffee enthusiast ☕',
      profilePictureUrl: 'https://i.pravatar.cc/150?u=sarah',
      followerCount: 890,
      followingCount: 1200
    },
    {
      id: 'f4d8e9a2-c1b0-4d5e-8f2a-9e1d0c2b3a4f',
      email: 'j.doe@example.com',
      username: 'johndoe',
      firstName: 'John',
      lastName: 'Doe',
      profilePictureUrl: 'https://i.pravatar.cc/150?u=john',
      followerCount: 45,
      followingCount: 88
    },
    {
      id: 'a1b2c3d4-e5f6-4a5b-b6c7-d8e9f0a1b2c3',
      email: 'elena.rodriguez@example.com',
      username: 'elena_ro',
      firstName: 'Elena',
      lastName: 'Rodriguez',
      bio: 'Fullstack Engineer & Digital Nomad. Exploring the world one line of code at a time.',
      profilePictureUrl: 'https://i.pravatar.cc/150?u=elena',
      followerCount: 3400,
      followingCount: 150
    }
  ];
}
