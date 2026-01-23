import { HttpHandlerFn, HttpRequest } from "@angular/common/http";
import { inject } from "@angular/core";
import { AuthService } from "../services/auth.service";

export function AuthInterceptor(req: HttpRequest<any>, next: HttpHandlerFn) {
  const authService = inject(AuthService);

  const excludedUrls = ['/auth'];

  const isExcluded = excludedUrls.some(url => req.url.includes(url));

  if (!isExcluded) {
    const token = authService.getToken();
    if (token) {
      req = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
    }
  }

  return next(req);
}
