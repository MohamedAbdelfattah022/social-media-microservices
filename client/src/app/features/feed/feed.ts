import { Component, inject, signal, input, OnInit } from '@angular/core';
import { PostCard } from '../post/post-card';
import { PostData } from '../../shared/models/posts/post-data.dto';
import { FeedService } from '../../core/services/feed.service';
import { finalize, Observable } from 'rxjs';
import { LoadingSkeleton } from '../../shared/loading-skeleton/loading-skeleton';
import { PostService } from '@/core/services/post.service';
import { CursorPaginationResponse } from '@/shared/models/cursor-pagination-response';
import { InfiniteScrollDirective } from '@/shared/directives/infinite-scroll.directive';
import { ZardIconComponent } from '@/shared/components/icon';

@Component({
  selector: 'app-feed',
  imports: [PostCard, LoadingSkeleton, InfiniteScrollDirective, ZardIconComponent],
  templateUrl: './feed.html',
  styleUrl: './feed.css',
})
export class Feed implements OnInit {
  private readonly feedService = inject(FeedService);
  private readonly postService = inject(PostService);
  protected readonly PAGE_SIZE = 5;

  userId = input<string>();

  protected readonly posts = signal<PostData[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly nextCursor = signal<string | null>(null);
  protected readonly hasNext = signal(true);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.loadMore();
  }

  protected loadMore(): void {
    if (this.isLoading() || !this.hasNext()) {
      return;
    }

    this.isLoading.set(true);
    this.error.set(null);

    const cursor = this.nextCursor();
    const userId = this.userId();

    let posts$: Observable<CursorPaginationResponse<PostData>>;

    if (userId) {
      posts$ = this.postService.getUserPosts(
        userId,
        this.PAGE_SIZE,
        cursor ?? undefined,
      );
    } else {
      posts$ = this.feedService.getFeed(cursor, this.PAGE_SIZE);
    }

    posts$
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: (response) => {
          this.posts.update((current) => [...current, ...response.data]);
          this.nextCursor.set(response.nextCursor);
          this.hasNext.set(response.hasNext);
        },
        error: (err) => {
          this.error.set('Failed to load posts. Please try again.');
          console.error('Error loading posts:', err);
        },
      });
  }

  refresh() {
    this.posts.set([]);
    this.nextCursor.set(null);
    this.hasNext.set(true);
    this.error.set(null);
    this.loadMore();
  }

  protected onPostDeleted(postId: number): void {
    this.posts.update((currentPosts) =>
      currentPosts.filter((post) => post.id !== postId)
    );
  }
}
