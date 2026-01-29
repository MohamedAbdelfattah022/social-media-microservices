import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CreateCommentDto, CommentDto } from '@/shared/models/comments';
import { CursorPaginationResponse } from '@/shared/models/cursor-pagination-response';

@Injectable({
  providedIn: 'root',
})
export class InteractionService {
  private readonly http = inject(HttpClient);
  private readonly commentsBaseUrl = environment.apiUrl + '/comments';
  private readonly likesBaseUrl = environment.apiUrl + '/likes';

  // Like methods for posts
  likePost(postId: number): Observable<void> {
    return this.http.post<void>(`${this.likesBaseUrl}/post/${postId}`, {});
  }

  unlikePost(postId: number): Observable<void> {
    return this.http.delete<void>(`${this.likesBaseUrl}/post/${postId}`);
  }

  getPostLikesCount(postId: number): Observable<number> {
    return this.http.get<number>(`${this.likesBaseUrl}/post/${postId}`);
  }

  // Like methods for comments
  likeComment(commentId: number): Observable<void> {
    return this.http.post<void>(`${this.likesBaseUrl}/comment/${commentId}`, {});
  }

  unlikeComment(commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.likesBaseUrl}/comment/${commentId}`);
  }

  getCommentLikesCount(commentId: number): Observable<number> {
    return this.http.get<number>(`${this.likesBaseUrl}/comment/${commentId}`);
  }

  // Comment CRUD methods
  addCommentToPost(postId: number, dto: CreateCommentDto): Observable<number> {
    return this.http.post<number>(`${this.commentsBaseUrl}/post/${postId}`, dto);
  }

  updateComment(commentId: number, dto: CreateCommentDto): Observable<void> {
    return this.http.put<void>(`${this.commentsBaseUrl}/${commentId}`, dto);
  }

  deleteComment(commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.commentsBaseUrl}/${commentId}`);
  }

  getPostComments(
    postId: number,
    pageSize: number,
    cursor?: string
  ): Observable<CursorPaginationResponse<CommentDto>> {
    let params = new HttpParams().set('pageSize', pageSize.toString());

    if (cursor) params = params.set('cursor', cursor);

    return this.http.get<CursorPaginationResponse<CommentDto>>(
      `${this.commentsBaseUrl}/post/${postId}`,
      { params }
    );
  }

  // Comment reply methods
  replyToComment(commentId: number, dto: CreateCommentDto): Observable<number> {
    return this.http.post<number>(`${this.commentsBaseUrl}/${commentId}/reply`, dto);
  }

  getCommentReplies(
    commentId: number,
    pageSize: number,
    cursor?: string
  ): Observable<CursorPaginationResponse<CommentDto>> {
    let params = new HttpParams().set('pageSize', pageSize.toString());

    if (cursor) params = params.set('cursor', cursor);

    return this.http.get<CursorPaginationResponse<CommentDto>>(
      `${this.commentsBaseUrl}/${commentId}/replies`,
      { params }
    );
  }
}
