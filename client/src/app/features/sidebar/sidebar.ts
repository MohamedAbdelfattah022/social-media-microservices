import { Component } from '@angular/core';
import { Recommendations } from '../recommendations/recommendations';

@Component({
  selector: 'app-sidebar',
  imports: [Recommendations],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {

}
