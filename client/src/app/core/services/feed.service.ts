import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { CursorPaginationResponse } from '../../shared/models/cursor-pagination-response';
import { PostData } from '../../shared/models/post-data';

@Injectable({
  providedIn: 'root',
})
export class FeedService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  getFeed(cursor: string | null, pageSize: number): Observable<CursorPaginationResponse<PostData>> {
    const params: Record<string, string> = { pageSize: pageSize.toString() };
    if (cursor !== null) {
      params['cursor'] = cursor;
    }

    return this.http.get<CursorPaginationResponse<PostData>>(this.apiUrl + '/feed', {
      params
    });
  }
}
