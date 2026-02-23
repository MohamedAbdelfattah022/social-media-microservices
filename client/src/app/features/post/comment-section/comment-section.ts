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

type ReplyPage = { comments: CommentDto[]; hasMore: boolean; nextCursor: string | null; };

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

  protected readonly activeReplyCommentId = signal<number | null>(null);
  protected readonly expandedReplies = signal<Set<number>>(new Set());
  protected readonly repliesMap = signal<Map<number, ReplyPage>>(new Map());
  protected readonly loadingReplies = signal<Set<number>>(new Set());

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
            this.comments.set([...this.comments(), ...response.data]);
          } else {
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
          this.loadComments();
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
            this.comments.set(
              this.comments().filter((c) => c.id !== commentId)
            );
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
    this.activeReplyCommentId.update((current) => (current === commentId ? null : commentId));
  }

  handleCancelReply() {
    this.activeReplyCommentId.set(null);
  }

  handleReplySubmitted(parentCommentId: number, content: string) {
    this.interactionService.replyToComment(parentCommentId, { content }).subscribe({
      next: () => {
        this.activeReplyCommentId.set(null);
        this.comments.update((list) =>
          list.map((c) =>
            c.id === parentCommentId ? { ...c, replyCount: c.replyCount + 1 } : c
          )
        );
        if (this.expandedReplies().has(parentCommentId)) {
          this.loadReplies(parentCommentId);
        } else {
          this.handleViewRepliesClicked(parentCommentId);
        }
        toast.success('Reply posted!');
      },
      error: () => {
        toast.error('Failed to post reply', { description: 'Please try again.' });
      },
    });
  }

  handleViewRepliesClicked(commentId: number) {
    const expanded = new Set(this.expandedReplies());
    if (expanded.has(commentId)) {
      expanded.delete(commentId);
      this.expandedReplies.set(expanded);
      const map = new Map(this.repliesMap());
      map.delete(commentId);
      this.repliesMap.set(map);
      return;
    }
    this.loadReplies(commentId);
  }

  loadMoreReplies(commentId: number) {
    const page = this.repliesMap().get(commentId);
    if (!page?.hasMore || !page.nextCursor || this.loadingReplies().has(commentId)) return;
    this.loadReplies(commentId, page.nextCursor);
  }

  private loadReplies(commentId: number, cursor?: string) {
    const loading = new Set(this.loadingReplies());
    loading.add(commentId);
    this.loadingReplies.set(loading);

    this.interactionService.getCommentReplies(commentId, this.pageSize, cursor).subscribe({
      next: (response) => {
        const existing = this.repliesMap().get(commentId);
        const merged = cursor && existing ? [...existing.comments, ...response.data] : response.data;
        const map = new Map(this.repliesMap());
        map.set(commentId, { comments: merged, hasMore: response.hasNext, nextCursor: response.nextCursor });
        this.repliesMap.set(map);

        const expanded = new Set(this.expandedReplies());
        expanded.add(commentId);
        this.expandedReplies.set(expanded);

        const loading = new Set(this.loadingReplies());
        loading.delete(commentId);
        this.loadingReplies.set(loading);
      },
      error: () => {
        const loading = new Set(this.loadingReplies());
        loading.delete(commentId);
        this.loadingReplies.set(loading);
        toast.error('Failed to load replies', { description: 'Please try again.' });
      },
    });
  }
}
