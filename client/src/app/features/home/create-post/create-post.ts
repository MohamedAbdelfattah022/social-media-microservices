import { PostService } from '@/core/services/post.service';
import { Privacy } from '@/shared/models/posts/privacy.enum';
import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { toast } from 'ngx-sonner';

@Component({
  selector: 'app-create-post',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-post.html',
  styleUrl: './create-post.css',
})
export class CreatePost {
  private readonly formBuilder = inject(FormBuilder);
  private readonly postService = inject(PostService);

  protected readonly isLoading = signal(false);
  protected readonly error = signal<string | null>(null);

  protected privacyOptions = Object.values(Privacy);

  postForm = this.formBuilder.group({
    content: ['', [Validators.required]],
    privacy: [Privacy.PUBLIC, [Validators.required]],
  });

  createPost(): void {
    if (this.postForm.invalid) {
      this.error.set('Please fill in all required fields');
      return;
    }

    this.isLoading.set(true);
    this.error.set(null);

    const formValue = this.postForm.value;
    const createPostDto = {
      content: formValue.content!,
      privacy: formValue.privacy! as Privacy,
      mediaUrls: []
    };

    this.postService.createPost(createPostDto)
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: () => {
          this.postForm.reset({
            content: '',
            privacy: Privacy.PUBLIC
          });
          toast.success('Post created successfully!', {
            description: 'Your post has been published and is now visible to others.'
          });
        },
        error: (err) => {
          this.error.set('Failed to create post');
          toast.error('Failed to create post', {
            description: 'There was an error publishing your post. Please try again.'
          });
          console.error(err);
        }
      });
  }

}
