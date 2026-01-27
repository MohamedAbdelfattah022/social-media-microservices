import { Component, input, signal } from '@angular/core';
import { PostData } from '../../shared/models/posts/post-data.dto';
import { computePostDate } from '../../shared/utils/compute-date-util';
import { LucideAngularModule, Ellipsis, Heart, MessageCircle } from 'lucide-angular';

@Component({
  selector: 'app-post-card',
  imports: [LucideAngularModule],
  templateUrl: './post-card.html',
  styleUrl: './post-card.css',
})
export class PostCard {
  data = input.required<PostData>();
  readonly Ellipsis = Ellipsis;
  readonly Heart = Heart;
  readonly MessageCircle = MessageCircle;

  isLiked = signal<boolean>(false);

  get postDate(): string {
    return computePostDate(this.data().createdAt);
  }



  handleLike() {
    console.log('Like button clicked for post ID:', this.data().id);
    this.isLiked.set(!this.isLiked());
    this.data().likeCount += this.isLiked() ? 1 : -1;
  }
}
