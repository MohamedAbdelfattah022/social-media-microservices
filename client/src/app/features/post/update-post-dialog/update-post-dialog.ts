import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ZardDialogRef, Z_MODAL_DATA } from '@/shared/components/dialog';
import { PostData } from '@/shared/models/posts/post-data.dto';
import { UpdatePostDto } from '@/shared/models/posts/update-post.dto';
import { Privacy } from '@/shared/models/posts/privacy.enum';

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

  protected readonly postData = signal<PostData | null>(null);
  protected readonly privacyOptions = [
    { value: Privacy.PUBLIC, label: 'Public' },
    { value: Privacy.FRIENDS_ONLY, label: 'Friends Only' },
    { value: Privacy.PRIVATE, label: 'Private' },
  ];

  protected form!: FormGroup<UpdatePostForm>;

  ngOnInit(): void {
    if (this.data) {
      this.postData.set(this.data);
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

  protected onSubmit(): void {
    if (this.form.valid) {
      const updateData: UpdatePostDto = {
        content: this.form.value.content,
        privacy: this.form.value.privacy,
      };

      this.dialogRef.close(updateData);
    }
  }

  protected onCancel(): void {
    this.dialogRef.close();
  }
}
