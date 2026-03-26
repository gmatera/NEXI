import { HTTP_INTERCEPTORS, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';
import { TokenStorageService } from '../_services/token-storage.service';
import { catchError, map, Observable, throwError } from 'rxjs';
import { EventBusService } from '../_shared/event-bus.service';
import { EventData } from '../_shared/event.class';
import { AuthService } from '../_services/auth.service';

const TOKEN_HEADER_KEY = 'Authorization';      
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private tokenStorageService: TokenStorageService,private eventBusService: EventBusService,private auth: AuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let authReq = req;
    const token = this.tokenStorageService.getToken();

    if (token != null) {
    const userData = JSON.parse(atob(token.split('.')[1]));
    this.auth.validateAccessToken(userData.sub);
    authReq = this.addTokenHeader(req,token)  
    }

    return next.handle(authReq).pipe(catchError(error => {
      if (error instanceof HttpErrorResponse && !authReq.url.includes('api/lmi/signin') && error.status === 401) {
        this.eventBusService.emit(new EventData('logout', null));
      }
      return throwError(() =>error);
    }));  
  }

  private addTokenHeader(req: HttpRequest<any>, token: string) {
   return req.clone({ headers: req.headers.set(TOKEN_HEADER_KEY, 'Bearer ' + token) });
  }


}
export const authInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
];
