import { Component, effect, inject, signal } from '@angular/core';
import { PostCard } from '../post/post-card';
import { PostData } from '../../shared/models/post-data';
import { FeedService } from '../../core/services/feed.service';
import { finalize } from 'rxjs';
import { LoadingSkeleton } from '../../shared/loading-skeleton/loading-skeleton';

@Component({
  selector: 'app-feed',
  imports: [PostCard, LoadingSkeleton],
  templateUrl: './feed.html',
  styleUrl: './feed.css',
  host: {
    '(window:scroll)': 'onScroll()'
  }
})
export class Feed {
  private readonly feedService = inject(FeedService);
  protected readonly PAGE_SIZE = 5;

  protected readonly posts = signal<PostData[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly nextCursor = signal<string | null>(null);
  protected readonly hasNext = signal(true);
  protected readonly error = signal<string | null>(null);

  constructor() {
    this.loadMore();
  }

  protected loadMore(): void {
    if (this.isLoading() || !this.hasNext()) {
      return;
    }

    this.isLoading.set(true);
    this.error.set(null);

    this.feedService.getFeed(this.nextCursor(), this.PAGE_SIZE)
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: (response) => {
          this.posts.update(current => [...current, ...response.data]);
          this.nextCursor.set(response.nextCursor);
          this.hasNext.set(response.hasNext);
        },
        error: (err) => {
          this.error.set('Failed to load feed. Please try again.');
          console.error('Error loading feed:', err);
        }
      });
  }

  protected onScroll() {
    const scrollPosition = window.scrollY + window.innerHeight;
    const documentHeight = document.documentElement.scrollHeight;
    const threshold = 200;
    const atBottom = documentHeight - scrollPosition < threshold;

    if (atBottom) this.loadMore();
  }
}
