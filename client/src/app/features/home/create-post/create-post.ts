import { PostService } from '@/core/services/post.service';
import { FileService } from '@/core/services/file.service';
import { Privacy } from '@/shared/models/posts/privacy.enum';
import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { switchMap, finalize } from 'rxjs/operators';
import { toast } from 'ngx-sonner';

interface SelectedFile {
  file: File;
  preview: string;
}

@Component({
  selector: 'app-create-post',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-post.html',
  styleUrl: './create-post.css',
})
export class CreatePost {
  private readonly formBuilder = inject(FormBuilder);
  private readonly postService = inject(PostService);
  private readonly fileService = inject(FileService);

  protected readonly isLoading = signal(false);
  protected readonly isUploading = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly selectedFiles = signal<SelectedFile[]>([]);

  protected privacyOptions = Object.values(Privacy);
  protected readonly maxFiles = 4;
  protected readonly maxFileSize = 25 * 1024 * 1024;
  protected readonly allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];

  postForm = this.formBuilder.group({
    content: ['', [Validators.required]],
    privacy: [Privacy.PUBLIC, [Validators.required]],
  });

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;

    const currentFiles = this.selectedFiles();
    const remainingSlots = this.maxFiles - currentFiles.length;

    if (remainingSlots <= 0) {
      toast.error('Maximum files reached', {
        description: `You can only upload up to ${this.maxFiles} files.`,
      });
      return;
    }

    const newFiles: SelectedFile[] = [];

    for (let i = 0; i < Math.min(input.files.length, remainingSlots); i++) {
      const file = input.files[i];

      if (!this.allowedTypes.includes(file.type)) {
        toast.error('Invalid file type', {
          description: `${file.name} is not a supported image format.`,
        });
        continue;
      }

      if (file.size > this.maxFileSize) {
        toast.error('File too large', {
          description: `${file.name} exceeds the 25MB limit.`,
        });
        continue;
      }

      const preview = URL.createObjectURL(file);
      newFiles.push({ file, preview });
    }

    this.selectedFiles.set([...currentFiles, ...newFiles]);
    input.value = '';
  }

  removeFile(index: number): void {
    const files = this.selectedFiles();
    URL.revokeObjectURL(files[index].preview);
    this.selectedFiles.set(files.filter((_, i) => i !== index));
  }

  createPost(): void {
    if (this.postForm.invalid) {
      this.error.set('Please fill in all required fields');
      return;
    }

    this.isLoading.set(true);
    this.error.set(null);

    const formValue = this.postForm.value;
    const files = this.selectedFiles().map((f) => f.file);

    const uploadFiles$ =
      files.length > 0
        ? forkJoin(files.map((file) => this.fileService.uploadFile(file)))
        : of([]);

    uploadFiles$
      .pipe(
        switchMap((uploadResponses) => {
          const fileIds = uploadResponses.map((r) => r.id);
          const createPostDto = {
            content: formValue.content!,
            privacy: formValue.privacy! as Privacy,
            fileIds: fileIds.length > 0 ? fileIds : undefined,
          };
          return this.postService.createPost(createPostDto);
        }),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe({
        next: () => {
          this.selectedFiles().forEach((f) => URL.revokeObjectURL(f.preview));
          this.selectedFiles.set([]);
          this.postForm.reset({
            content: '',
            privacy: Privacy.PUBLIC,
          });
          toast.success('Post created successfully!', {
            description: 'Your post has been published and is now visible to others.',
          });
        },
        error: (err) => {
          this.error.set('Failed to create post');
          toast.error('Failed to create post', {
            description: 'There was an error publishing your post. Please try again.',
          });
          console.error(err);
        },
      });
  }
}

