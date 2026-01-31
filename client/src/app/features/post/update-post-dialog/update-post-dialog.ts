import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ZardDialogRef, Z_MODAL_DATA } from '@/shared/components/dialog';
import { PostData } from '@/shared/models/posts/post-data.dto';
import { UpdatePostDto } from '@/shared/models/posts/update-post.dto';
import { Privacy } from '@/shared/models/posts/privacy.enum';
import { FileService } from '@/core/services/file.service';
import { forkJoin, of } from 'rxjs';
import { toast } from 'ngx-sonner';

interface SelectedFile {
  file: File;
  preview: string;
}

interface UpdatePostForm {
  content: FormControl<string>;
  privacy: FormControl<Privacy>;
}

@Component({
  selector: 'app-update-post-dialog',
  imports: [ReactiveFormsModule],
  templateUrl: './update-post-dialog.html',
  styleUrl: './update-post-dialog.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdatePostDialog implements OnInit {
  private readonly dialogRef = inject(ZardDialogRef<UpdatePostDialog>);
  private readonly data = inject<PostData>(Z_MODAL_DATA);
  private readonly fileService = inject(FileService);

  protected readonly postData = signal<PostData | null>(null);
  protected readonly existingMediaUrls = signal<string[]>([]);
  protected readonly selectedFiles = signal<SelectedFile[]>([]);
  protected readonly isUploading = signal(false);
  protected readonly maxFiles = 4;
  protected readonly allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  protected readonly maxFileSize = 25 * 1024 * 1024;

  protected readonly privacyOptions = [
    { value: Privacy.PUBLIC, label: 'Public' },
    { value: Privacy.FRIENDS_ONLY, label: 'Friends Only' },
    { value: Privacy.PRIVATE, label: 'Private' },
  ];

  protected form!: FormGroup<UpdatePostForm>;

  ngOnInit(): void {
    if (this.data) {
      this.postData.set(this.data);
      this.existingMediaUrls.set(this.data.mediaUrls || []);
      this.initializeForm(this.data);
    }
  }

  private initializeForm(data: PostData): void {
    this.form = new FormGroup<UpdatePostForm>({
      content: new FormControl(data.content, {
        nonNullable: true,
        validators: [Validators.required, Validators.minLength(1)],
      }),
      privacy: new FormControl(data.privacy as Privacy, {
        nonNullable: true,
        validators: [Validators.required],
      }),
    });
  }

  protected get totalMediaCount(): number {
    return this.existingMediaUrls().length + this.selectedFiles().length;
  }

  protected onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;

    const remainingSlots = this.maxFiles - this.totalMediaCount;

    if (remainingSlots <= 0) {
      toast.error('Maximum files reached', {
        description: `You can only have up to ${this.maxFiles} files.`,
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

    this.selectedFiles.set([...this.selectedFiles(), ...newFiles]);
    input.value = '';
  }

  protected removeExistingMedia(index: number): void {
    this.existingMediaUrls.set(this.existingMediaUrls().filter((_, i) => i !== index));
  }

  protected removeNewFile(index: number): void {
    const files = this.selectedFiles();
    URL.revokeObjectURL(files[index].preview);
    this.selectedFiles.set(files.filter((_, i) => i !== index));
  }

  protected onSubmit(): void {
    if (this.form.valid) {
      const files = this.selectedFiles().map((f) => f.file);

      if (files.length > 0) {
        this.isUploading.set(true);
        forkJoin(files.map((file) => this.fileService.uploadFile(file))).subscribe({
          next: (uploadResponses) => {
            const newFileIds = uploadResponses.map((r) => r.id);
            this.closeWithData(newFileIds);
          },
          error: (err) => {
            this.isUploading.set(false);
            toast.error('Failed to upload files', {
              description: 'Please try again.',
            });
            console.error(err);
          },
        });
      } else {
        this.closeWithData([]);
      }
    }
  }

  private closeWithData(newFileIds: string[]): void {
    this.isUploading.set(false);
    this.selectedFiles().forEach((f) => URL.revokeObjectURL(f.preview));

    const updateData: UpdatePostDto = {
      content: this.form.value.content,
      privacy: this.form.value.privacy,
    };

    const mediaWasModified =
      this.selectedFiles().length > 0 ||
      (this.data.mediaUrls?.length || 0) !== this.existingMediaUrls().length;

    if (mediaWasModified) {
      updateData.fileIds = newFileIds;
    }

    this.dialogRef.close(updateData);
  }

  protected onCancel(): void {
    this.selectedFiles().forEach((f) => URL.revokeObjectURL(f.preview));
    this.dialogRef.close();
  }
}
