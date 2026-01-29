import { Component, input, signal, inject, output, computed, effect } from '@angular/core';
import { PostData } from '../../shared/models/posts/post-data.dto';
import { computePostDate } from '../../shared/utils/compute-date-util';
import { ZardIconComponent } from '@/shared/components/icon';
import { ZardMenuImports } from '@/shared/components/menu/menu.imports';
import { ZardDialogService } from '@/shared/components/dialog';
import { UpdatePostDialog } from './update-post-dialog/update-post-dialog';
import { UpdatePostDto } from '@/shared/models/posts/update-post.dto';
import { PostService } from '@/core/services/post.service';
import { AuthService } from '@/core/services/auth.service';
import { InteractionService } from '@/core/services/interaction.service';
import { CommentSection } from './comment-section/comment-section';
import { toast } from 'ngx-sonner';

@Component({
  selector: 'app-post-card',
  imports: [ZardIconComponent, ZardMenuImports, CommentSection],
  templateUrl: './post-card.html',
  styleUrl: './post-card.css',
})
export class PostCard {
  private readonly dialogService = inject(ZardDialogService);
  private readonly postService = inject(PostService);
  private readonly authService = inject(AuthService);
  private readonly interactionService = inject(InteractionService);

  data = input.required<PostData>();
  postDeleted = output<number>();

  protected readonly postContent = signal<string>('');
  protected readonly postPrivacy = signal<string>('');
  protected readonly postIsEdited = signal<boolean>(false);
  protected readonly showComments = signal<boolean>(false);

  isLiked = signal<boolean>(false);
  isLiking = signal<boolean>(false);
  isUpdating = signal<boolean>(false);
  isDeleting = signal<boolean>(false);

  protected readonly isOwnPost = computed(() => {
    const currentUserId = this.authService.currentUserId();
    return currentUserId === this.data().userId;
  });

  constructor() {
    effect(() => {
      this.postContent.set(this.data().content);
      this.postPrivacy.set(this.data().privacy);
      this.postIsEdited.set(this.data().isEdited);
    });
  }

  get postDate(): string {
    return computePostDate(this.data().createdAt);
  }

  handleLike() {
    if (this.isLiking()) return;

    const wasLiked = this.isLiked();
    const originalCount = this.data().likeCount;

    // Optimistic update
    this.isLiked.set(!wasLiked);
    this.data().likeCount += wasLiked ? -1 : 1;
    this.isLiking.set(true);

    const operation = wasLiked
      ? this.interactionService.unlikePost(this.data().id)
      : this.interactionService.likePost(this.data().id);

    operation.subscribe({
      next: () => {
        this.isLiking.set(false);
      },
      error: (error) => {
        console.error('Error toggling like:', error);
        // Rollback on error
        this.isLiked.set(wasLiked);
        this.data().likeCount = originalCount;
        this.isLiking.set(false);
        toast.error('Failed to update like', {
          description: 'Please try again.',
        });
      },
    });
  }

  handleUpdate() {
    const dialogRef = this.dialogService.create<UpdatePostDialog, PostData>({
      zTitle: 'Update Post',
      zContent: UpdatePostDialog,
      zData: this.data(),
      zWidth: '600px',
      zHideFooter: true,
      zMaskClosable: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const updateData = result as UpdatePostDto;
        this.isUpdating.set(true);

        this.postService.updatePost(this.data().id, updateData).subscribe({
          next: () => {
            if (updateData.content) {
              this.postContent.set(updateData.content);
            }
            if (updateData.privacy) {
              this.postPrivacy.set(updateData.privacy);
            }
            this.postIsEdited.set(true);
            this.isUpdating.set(false);
            toast.success('Post updated successfully!', {
              description: 'Your changes have been saved.'
            });
          },
          error: (error) => {
            console.error('Error updating post:', error);
            this.isUpdating.set(false);
            toast.error('Failed to update post', {
              description: 'There was an error updating your post. Please try again.'
            });
          }
        });
      }
    });
  }

  handleDelete() {
    const dialogRef = this.dialogService.create({
      zTitle: 'Delete Post',
      zDescription: 'Are you sure you want to delete this post? This action cannot be undone.',
      zOkText: 'Delete',
      zOkDestructive: true,
      zCancelText: 'Cancel',
      zOnOk: () => {
        this.isDeleting.set(true);

        this.postService.deletePost(this.data().id).subscribe({
          next: () => {
            this.isDeleting.set(false);
            this.postDeleted.emit(this.data().id);
            toast.success('Post deleted successfully!', {
              description: 'Your post has been removed.'
            });
          },
          error: (error) => {
            console.error('Error deleting post:', error);
            this.isDeleting.set(false);
            toast.error('Failed to delete post', {
              description: 'There was an error deleting your post. Please try again.'
            });
          }
        });
      }
    });
  }

  toggleComments() {
    this.showComments.set(!this.showComments());
  }

  handleCommentCountUpdated(newCount: number) {
    this.data().commentCount = newCount;
  }
}
