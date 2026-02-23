import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { toast } from 'ngx-sonner';
import { ZardDialogRef, Z_MODAL_DATA } from '@/shared/components/dialog';
import { ZardIconComponent } from '@/shared/components/icon/icon.component';
import { ZardButtonComponent } from '@/shared/components/button/button.component';
import { UserService } from '@/core/services/user.service';
import { UserProfileData } from '@/shared/models/users/user-profile-data';

interface EditProfileForm {
  firstName: FormControl<string>;
  lastName: FormControl<string>;
  bio: FormControl<string>;
}

@Component({
  selector: 'app-edit-profile-dialog',
  imports: [ReactiveFormsModule, ZardIconComponent, ZardButtonComponent],
  templateUrl: './edit-profile-dialog.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditProfileDialog implements OnInit {
  private readonly dialogRef = inject(ZardDialogRef<EditProfileDialog>);
  private readonly profileData = inject<UserProfileData>(Z_MODAL_DATA);
  private readonly userService = inject(UserService);

  protected readonly isSaving = signal(false);
  protected readonly isUploadingPicture = signal(false);
  protected readonly isDeletingPicture = signal(false);
  protected readonly avatarPreview = signal<string | null>(null);
  protected readonly pendingFile = signal<File | null>(null);

  protected readonly currentAvatarUrl = computed(
    () => this.avatarPreview() ?? this.profileData?.profilePictureUrl ?? null,
  );

  protected readonly initials = computed(() => {
    const f = this.profileData?.firstName?.[0] ?? '';
    const l = this.profileData?.lastName?.[0] ?? '';
    return (f + l).toUpperCase();
  });

  protected readonly allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  protected readonly maxFileSize = 5 * 1024 * 1024; // 5 MB

  protected form!: FormGroup<EditProfileForm>;

  ngOnInit(): void {
    this.form = new FormGroup<EditProfileForm>({
      firstName: new FormControl(this.profileData?.firstName ?? '', {
        nonNullable: true,
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      lastName: new FormControl(this.profileData?.lastName ?? '', {
        nonNullable: true,
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      bio: new FormControl(this.profileData?.bio ?? '', {
        nonNullable: true,
        validators: [Validators.maxLength(300)],
      }),
    });
  }

  onAvatarFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    if (!this.allowedTypes.includes(file.type)) {
      toast.error('Invalid file type', { description: 'Only JPEG, PNG, GIF or WebP are allowed.' });
      input.value = '';
      return;
    }

    if (file.size > this.maxFileSize) {
      toast.error('File too large', { description: 'Maximum allowed size is 5 MB.' });
      input.value = '';
      return;
    }

    this.pendingFile.set(file);

    const reader = new FileReader();
    reader.onload = (e) => this.avatarPreview.set(e.target?.result as string);
    reader.readAsDataURL(file);
  }

  onRemoveAvatar(): void {
    if (this.pendingFile()) {
      // Just clear the pending file — no server call needed yet
      this.pendingFile.set(null);
      this.avatarPreview.set(null);
      return;
    }

    if (!this.profileData?.profilePictureUrl) return;

    this.isDeletingPicture.set(true);
    this.userService.deleteProfilePicture().subscribe({
      next: () => {
        this.avatarPreview.set(null);
        this.profileData.profilePictureUrl = '';
        this.isDeletingPicture.set(false);
        toast.success('Profile picture removed.');
      },
      error: () => {
        this.isDeletingPicture.set(false);
        toast.error('Failed to remove profile picture.');
      },
    });
  }

  onSave(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSaving.set(true);

    const { firstName, lastName, bio } = this.form.getRawValue();

    this.userService.updateUserProfile({ firstName, lastName, bio }).subscribe({
      next: () => {
        const file = this.pendingFile();
        if (file) {
          this.uploadPictureThenClose(file, { firstName, lastName, bio });
        } else {
          this.isSaving.set(false);
          toast.success('Profile updated successfully!');
          this.dialogRef.close({ firstName, lastName, bio, profilePictureUrl: this.profileData?.profilePictureUrl });
        }
      },
      error: () => {
        this.isSaving.set(false);
        toast.error('Failed to update profile.', { description: 'Please try again.' });
      },
    });
  }

  private uploadPictureThenClose(
    file: File,
    updatedFields: { firstName: string; lastName: string; bio: string; },
  ): void {
    this.userService.uploadProfilePicture(file).subscribe({
      next: (res) => {
        this.isSaving.set(false);
        toast.success('Profile updated successfully!');
        this.dialogRef.close({ ...updatedFields, profilePictureUrl: res.profilePictureUrl });
      },
      error: () => {
        this.isSaving.set(false);
        toast.error('Profile saved but picture upload failed.', {
          description: 'You can try re-uploading your picture.',
        });
        this.dialogRef.close({ ...updatedFields, profilePictureUrl: this.profileData?.profilePictureUrl });
      },
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
