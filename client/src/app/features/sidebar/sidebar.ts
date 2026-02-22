import { Component } from '@angular/core';
import { Recommendations } from '../recommendations/recommendations';
import { UserSearch } from '../search/user-search';

@Component({
  selector: 'app-sidebar',
  imports: [Recommendations, UserSearch],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {

}
