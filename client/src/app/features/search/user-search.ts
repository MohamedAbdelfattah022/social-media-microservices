import {
  Component,
  ChangeDetectionStrategy,
  signal,
  inject,
  OnDestroy,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import {
  debounceTime,
  distinctUntilChanged,
  Subject,
  switchMap,
  Subscription,
} from "rxjs";
import { UserService } from "@/core/services/user.service";
import { UserProfileData } from "@/shared/models/users/user-profile-data";
import { ZardIconComponent } from "@/shared/components/icon/icon.component";

@Component({
  selector: "app-user-search",
  imports: [CommonModule, FormsModule, ZardIconComponent],
  templateUrl: "./user-search.html",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserSearch implements OnDestroy {
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  protected readonly searchQuery = signal("");
  protected readonly searchResults = signal<UserProfileData[]>([]);
  protected readonly isSearching = signal(false);
  protected readonly showResults = signal(false);

  private readonly searchSubject = new Subject<string>();
  private readonly subscription: Subscription;

  constructor() {
    this.subscription = this.searchSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((query) => {
          if (query.trim().length < 2) {
            this.searchResults.set([]);
            this.isSearching.set(false);
            this.showResults.set(false);
            return [];
          }

          this.isSearching.set(true);
          return this.userService.searchUsers(query);
        })
      )
      .subscribe({
        next: (results) => {
          this.searchResults.set(results);
          this.isSearching.set(false);
          this.showResults.set(true);
        },
        error: () => {
          this.isSearching.set(false);
          this.searchResults.set([]);
        },
      });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  onSearchInput(event: Event) {
    const query = (event.target as HTMLInputElement).value;
    this.searchQuery.set(query);
    this.searchSubject.next(query);
  }

  navigateToProfile(userId: string) {
    this.router.navigate(["/profile", userId]);
    this.clearSearch();
  }

  clearSearch() {
    this.searchQuery.set("");
    this.searchResults.set([]);
    this.showResults.set(false);
  }
}
