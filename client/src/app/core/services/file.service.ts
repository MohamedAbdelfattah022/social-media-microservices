import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FileUploadResponse } from '@/shared/models/files/file-upload-response';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class FileService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl + '/files';

  uploadFile(file: File): Observable<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<FileUploadResponse>(`${this.baseUrl}/upload`, formData);
  }

  uploadFiles(files: File[]): Observable<FileUploadResponse[]> {
    const uploads = files.map(file => this.uploadFile(file));
    return new Observable(subscriber => {
      const results: FileUploadResponse[] = [];
      let completed = 0;
      let hasError = false;

      uploads.forEach((upload, index) => {
        upload.subscribe({
          next: (response) => {
            results[index] = response;
            completed++;
            if (completed === uploads.length && !hasError) {
              subscriber.next(results);
              subscriber.complete();
            }
          },
          error: (error) => {
            hasError = true;
            subscriber.error(error);
          }
        });
      });
    });
  }

  deleteFile(fileId: string): Observable<string> {
    return this.http.delete(`${this.baseUrl}/delete/${fileId}`, { responseType: 'text' });
  }
}
