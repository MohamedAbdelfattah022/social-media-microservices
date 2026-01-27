import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PostData } from '@/shared/models/posts/post-data.dto';
import { CreatePostDto } from '@/shared/models/posts/create-post.dto';
import { CursorPaginationResponse } from '@/shared/models/cursor-pagination-response';
import { UpdatePostDto } from '@/shared/models/posts/update-post.dto';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PostService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl + '/posts';

  createPost(createPostDto: CreatePostDto): Observable<void> {
    return this.http.post<void>(this.baseUrl, createPostDto);
  }

  getPost(postId: number): Observable<PostData> {
    return this.http.get<PostData>(`${this.baseUrl}/${postId}`);
  }


  updatePost(postId: number, updatePostDto: UpdatePostDto): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/${postId}`, updatePostDto);
  }

  deletePost(postId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${postId}`);
  }

  getUserPosts(
    userId: string,
    pageSize: number,
    cursor?: string
  ): Observable<CursorPaginationResponse<PostData>> {
    let params = new HttpParams().set('pageSize', pageSize.toString());

    if (cursor) params = params.set('cursor', cursor);


    return this.http.get<CursorPaginationResponse<PostData>>(
      `${this.baseUrl}/user/${userId}`,
      { params }
    );
  }
}
