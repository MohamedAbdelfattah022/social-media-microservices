import { Directive, HostListener, output } from '@angular/core';

@Directive({
  selector: '[appInfiniteScroll]',
  standalone: true,
})
export class InfiniteScrollDirective {
  scrolled = output<void>();

  @HostListener('window:scroll')
  onWindowScroll() {
    const scrollPosition = window.scrollY + window.innerHeight;
    const documentHeight = document.documentElement.scrollHeight;

    if (documentHeight - scrollPosition < 200) {
      this.scrolled.emit();
    }
  }
}
