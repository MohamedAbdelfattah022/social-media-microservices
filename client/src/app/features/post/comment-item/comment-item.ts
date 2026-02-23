import { ChangeDetectionStrategy, Component, computed, inject, input, output, signal } from '@angular/core';
import { CommentDto } from '@/shared/models/comments';
import { ZardIconComponent } from '@/shared/components/icon';
import { ZardMenuImports } from '@/shared/components/menu/menu.imports';
import { AuthService } from '@/core/services/auth.service';
import { computePostDate } from '@/shared/utils/compute-date-util';
import { InteractionService } from '@/core/services/interaction.service';
import { toast } from 'ngx-sonner';
import { Router } from '@angular/router';

@Component({
  selector: 'app-comment-item',
  imports: [ZardIconComponent, ZardMenuImports],
  templateUrl: './comment-item.html',
  styleUrl: './comment-item.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CommentItem {
  private readonly authService = inject(AuthService);
  private readonly interactionService = inject(InteractionService);
  private readonly router = inject(Router);

  comment = input.required<CommentDto>();

  commentUpdated = output<CommentDto>();
  commentDeleted = output<number>();
  replyClicked = output<number>();
  viewRepliesClicked = output<number>();

  protected readonly isLiked = signal<boolean>(false);
  protected readonly isLiking = signal<boolean>(false);

  protected readonly isOwnComment = computed(() => {
    const currentUserId = this.authService.currentUserId();
    return currentUserId === this.comment().userId;
  });

  get commentDate(): string {
    return computePostDate(this.comment().createdAt);
  }

  handleLike() {
    if (this.isLiking()) return;

    const wasLiked = this.isLiked();
    const originalCount = this.comment().likeCount;

    this.isLiked.set(!wasLiked);
    this.comment().likeCount += wasLiked ? -1 : 1;
    this.isLiking.set(true);

    const operation = wasLiked
      ? this.interactionService.unlikeComment(this.comment().id)
      : this.interactionService.likeComment(this.comment().id);

    operation.subscribe({
      next: () => {
        this.isLiking.set(false);
      },
      error: (error) => {
        console.error('Error toggling comment like:', error);
        this.isLiked.set(wasLiked);
        this.comment().likeCount = originalCount;
        this.isLiking.set(false);
        toast.error('Failed to update like', {
          description: 'Please try again.',
        });
      },
    });
  }

  handleEdit() {
    console.log('Edit comment:', this.comment().id);
  }

  handleDelete() {
    this.commentDeleted.emit(this.comment().id);
  }

  handleReply() {
    this.replyClicked.emit(this.comment().id);
  }

  handleViewReplies() {
    this.viewRepliesClicked.emit(this.comment().id);
  }

  navigateToProfile() {
    this.router.navigate(['/profile', this.comment().userId]);
  }
}
