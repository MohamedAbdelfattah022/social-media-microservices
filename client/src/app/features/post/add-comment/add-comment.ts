import { ChangeDetectionStrategy, Component, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-add-comment',
  imports: [FormsModule],
  templateUrl: './add-comment.html',
  styleUrl: './add-comment.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddComment {
  postId = input.required<number>();
  parentCommentId = input<number | null>(null);
  placeholder = input<string>('Write a comment...');
  
  commentSubmitted = output<string>();

  protected readonly content = signal<string>('');
  protected readonly isSubmitting = signal<boolean>(false);

  protected readonly maxLength = 2000;

  handleSubmit() {
    const trimmedContent = this.content().trim();
    
    if (!trimmedContent || this.isSubmitting()) return;

    this.isSubmitting.set(true);
    this.commentSubmitted.emit(trimmedContent);
  }

  clearContent() {
    this.content.set('');
    this.isSubmitting.set(false);
  }

  setSubmitting(value: boolean) {
    this.isSubmitting.set(value);
  }
}
