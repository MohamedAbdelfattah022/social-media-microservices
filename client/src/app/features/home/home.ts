import { Component } from '@angular/core';
import { Feed } from '../feed/feed';
import { Sidebar } from '../sidebar/sidebar';

@Component({
  selector: 'app-home',
  imports: [Feed, Sidebar],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {

}
