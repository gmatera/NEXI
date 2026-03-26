import { Injectable } from '@angular/core';
import {catchError, Observable, of, throwError} from 'rxjs';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpRequest } from '@angular/common/http';
import { SessionService } from './session.service';
import { ErrorService } from './error.service';



@Injectable({
  providedIn: 'root'
})
export class CommonService {

  API_URI = '/ui';

  constructor(private http: HttpClient, private session: SessionService, private error: ErrorService) {
  }

  private _buildHeaders(): HttpHeaders {
    return new HttpHeaders().set('Content-Type', 'application/json');
    //a = a.append('cid', environment.cid);
    // return newHeaders.append('Content-Type', 'application/json');
  }


  public get<T>(serviceUrl: string, params?: HttpParams, httpHeaders?: HttpHeaders): Observable<T> {


    // return this._refreshToken(serviceUrl).pipe(jwtBean => {

    //   if (jwtBean) {
    //     this.session.saveJwt(jwtBean);
    //   }

      if (httpHeaders) {
        let newHeaders = this._buildHeaders();
        
        httpHeaders.keys().forEach(key => {
          newHeaders = newHeaders.append(key, <string> httpHeaders.get(key));
        });
      }

      // if (haveCredentials) {
      //   httpOptions['withCredentials'] = true;
      // }
    //  console.log(this.session.getUrlUsed());
      
      return this.http.get<any>(this.session.getUrlUsed() + serviceUrl,  {headers: httpHeaders, params: params})
        .pipe(
          catchError((err: HttpErrorResponse) => {
            this.error.httpError(err);
            //this.loadingService.setIsSpinnerActive(false);
            return throwError(() => err);
          })
        );

    // });

  }

  public post<T>(serviceUrl: string, body?: any, httpHeaders?: HttpHeaders): Observable<T> {
    let newHeaders = this._buildHeaders();
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    }
    if (httpHeaders) {
      httpHeaders.keys().forEach(key => {
        newHeaders = newHeaders.append(key, <string> httpHeaders.get(key));
      });
    }
 
    
      return this.http.post<T>(this.session.getUrlUsed() + serviceUrl, body, httpOptions)
          .pipe(
            //delay(delayTime),
            catchError((err: HttpErrorResponse) => {
              
              this.error.httpError(err);
              //this.loadingService.setIsSpinnerActive(false);
              // if (err.status === 401 && serviceUrl.endsWith('preLogin')) {
              //   this.error.showAccessDenied();
              // } else {
              //   this.error.httpError(err);
              // }
              return throwError(() => err);
            })
          );
    

  }


  
      
  // private _refreshToken(serviceUrl: string): JwtBean {

  //   // if (this.appStored.getIsLogged() && serviceUrl !== 'hub/jwt/isTokenExpired' && this.appStored.getCustomerAccountKey()) {

  //   //   const now = new Date();
  //   //   const nowUTC = new Date(now.getTime() + (now.getTimezoneOffset() * 60000));
  //   //   const exp = new Date(this.appStored.getExpiresDate());

  //   //   console.log('now UTC', nowUTC);
  //   //   console.log('exp UTC', exp);

  //   //   const remaining = exp.getTime() - nowUTC.getTime();

  //   //   if (nowUTC.getTime() < exp.getTime() && remaining <= 10000) {
  //   //     let httpOptions = this._buildHttpOptions();

  //   //   const token: string = this.appStored.getAccessToken();
  //   //   httpOptions['params'] = { jwtToken: token };

  //   //   return this.http.get<any>(this.appStored.getUrlUsed() + this._API_URL_REFRESH_TOKEN, httpOptions)
  //   //     .pipe(
  //   //       catchError((err: HttpErrorResponse) => {
  //   //         this.error.httpError(err);
  //   //         this.loadingService.setIsSpinnerActive(false);
  //   //         return throwError(err);
  //   //       })
  //   //     ).toPromise();
  //   //   } else {
  //   //     return Promise.resolve();
  //   //   }

  //   // } else {
  //   //   return Promise.resolve();
  //   // }
  //   return new JwtBean("aaa");
  // }
}
