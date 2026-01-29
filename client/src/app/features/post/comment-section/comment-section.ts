import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
  output,
  signal,
  viewChild,
} from '@angular/core';
import { CommentDto } from '@/shared/models/comments';
import { InteractionService } from '@/core/services/interaction.service';
import { ZardDialogService } from '@/shared/components/dialog';
import { toast } from 'ngx-sonner';
import { AddComment } from '../add-comment/add-comment';
import { CommentItem } from '../comment-item/comment-item';

@Component({
  selector: 'app-comment-section',
  imports: [AddComment, CommentItem],
  templateUrl: './comment-section.html',
  styleUrl: './comment-section.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CommentSection {
  private readonly interactionService = inject(InteractionService);
  private readonly dialogService = inject(ZardDialogService);

  postId = input.required<number>();
  commentCountUpdated = output<number>();

  protected readonly comments = signal<CommentDto[]>([]);
  protected readonly isLoading = signal<boolean>(false);
  protected readonly hasMore = signal<boolean>(false);
  protected readonly nextCursor = signal<string | null>(null);
  protected readonly pageSize = 10;

  private readonly addCommentComponent = viewChild(AddComment);

  ngOnInit() {
    this.loadComments();
  }

  loadComments(cursor?: string) {
    this.isLoading.set(true);

    this.interactionService
      .getPostComments(this.postId(), this.pageSize, cursor)
      .subscribe({
        next: (response) => {
          if (cursor) {
            // Append for pagination
            this.comments.set([...this.comments(), ...response.data]);
          } else {
            // Initial load or refresh
            this.comments.set(response.data);
          }
          this.hasMore.set(response.hasNext);
          this.nextCursor.set(response.nextCursor);
          this.isLoading.set(false);
        },
        error: (error) => {
          console.error('Error loading comments:', error);
          this.isLoading.set(false);
          toast.error('Failed to load comments', {
            description: 'Please try again.',
          });
        },
      });
  }

  loadMore() {
    const cursor = this.nextCursor();
    if (cursor && !this.isLoading()) {
      this.loadComments(cursor);
    }
  }

  handleCommentSubmitted(content: string) {
    this.interactionService
      .addCommentToPost(this.postId(), { content })
      .subscribe({
        next: () => {
          this.addCommentComponent()?.clearContent();
          // Reload comments to show the new one
          this.loadComments();
          // Update parent's comment count
          this.commentCountUpdated.emit(this.comments().length + 1);
          toast.success('Comment added!');
        },
        error: (error) => {
          console.error('Error adding comment:', error);
          this.addCommentComponent()?.setSubmitting(false);
          toast.error('Failed to add comment', {
            description: 'Please try again.',
          });
        },
      });
  }

  handleCommentDeleted(commentId: number) {
    const dialogRef = this.dialogService.create({
      zTitle: 'Delete Comment',
      zDescription:
        'Are you sure you want to delete this comment? This action cannot be undone.',
      zOkText: 'Delete',
      zOkDestructive: true,
      zCancelText: 'Cancel',
      zOnOk: () => {
        this.interactionService.deleteComment(commentId).subscribe({
          next: () => {
            // Remove from local state
            this.comments.set(
              this.comments().filter((c) => c.id !== commentId)
            );
            // Update parent's comment count
            this.commentCountUpdated.emit(this.comments().length);
            toast.success('Comment deleted!');
          },
          error: (error) => {
            console.error('Error deleting comment:', error);
            toast.error('Failed to delete comment', {
              description: 'Please try again.',
            });
          },
        });
      },
    });
  }

  handleReplyClicked(commentId: number) {
    // TODO: Implement reply functionality
    console.log('Reply to comment:', commentId);
    toast.info('Reply feature coming soon!');
  }

  handleViewRepliesClicked(commentId: number) {
    // TODO: Implement view replies functionality
    console.log('View replies for comment:', commentId);
    toast.info('View replies feature coming soon!');
  }
}
