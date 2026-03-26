import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { ErrorService } from 'src/app/service/error.service';
import { TokenStorageService } from './token-storage.service';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SessionService } from 'src/app/service/session.service';
import { UserDTO } from '../user-management/user/dto/user-dto';
import { Role } from 'src/app/enums/role.enum';
const AUTH_API = '/api/lmi/';
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class AuthService implements CanActivate {

  constructor(private http: HttpClient, private error: ErrorService, private session: SessionService, private tokenStorageService: TokenStorageService, private snackBar: MatSnackBar, private route: Router) { }

  isrefreshing = false;

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    // const isAllowed = route?.data['roles']?.map((m:any)=>m.toLowerCase()).includes(this.tokenStorageService.getUserRole()?.toLowerCase());
    //console.log("log roles", route?.data['roles']);
    const isAllowed = this.hasPermission(route?.data['roles']);
    if (!isAllowed) {
      this.snackBar.open("Unautorized access", 'Close', {
        panelClass: ['snackbar-danger'],
        duration: 3000
      });
    }
    return isAllowed;

  }

  hasPermission(currentRoles: any) {
    return this.tokenStorageService.getUserRole()?.split(',').filter((f: string) => f)?.some((e: string) => currentRoles.includes(e));
  }

  login(username: string, password: string): Observable<any> {
    return this.http.post(AUTH_API + 'signin', {
      username,
      password
    }, httpOptions).pipe(
      catchError((err: HttpErrorResponse) => {
        this.error.httpError(err);
        return throwError(() => err);
      })
    );;
  }

  changePassword(currentPassword: string, password: string): Observable<any> {

    let username!: string;
    username = this.tokenStorageService.getUserName();
    return this.http.post(this.session.getUrlUsed() + '/user/changepassword', {
      username,
      currentPassword,
      password
    }, httpOptions).pipe(
      catchError((err: HttpErrorResponse) => {
        this.error.httpError(err);
        return throwError(() => err);
      })
    );;
  }

  resetPassword(userDTO: UserDTO): Observable<any> {
    console.error("zdfd",userDTO);
    return this.http.post(this.session.getUrlUsed() + '/user/resetpassword', userDTO, httpOptions).pipe(
      catchError((err: HttpErrorResponse) => {
        this.error.httpError(err);
        return throwError(() => err);
      })
    );;
  }


  refreshToken(username: string) {
    return this.http.post(AUTH_API + 'refreshtoken', {
      userName: username
    }, httpOptions).subscribe((token: any) => {
      this.isrefreshing = false;
      this.saveAccessTokenData(token);
    }, error => {
      this.isrefreshing = false;

    })
  }

  validateAccessToken(username: string) {
    if (Date.now() > this.tokenStorageService.getTimeout() && !this.isrefreshing) {
      this.isrefreshing = true;
      this.refreshToken(username);
    }
  }


  saveAccessTokenData(token: any) {
    this.tokenStorageService.saveToken(token.accessToken);
    const userData = JSON.parse(atob(token.accessToken.split('.')[1]));
    const expires = new Date((userData.exp * 1000));
    const timeout = expires.getTime() - (30 * 1000);   //get time 30 sec before token expiration time
    this.tokenStorageService.saveTimeout(timeout);
  }

  get isAuthenticated(): boolean {
    return this.tokenStorageService.getToken() != null;
  }

  get isAdmin(): boolean {
    return this.tokenStorageService.getUserRole()?.replaceAll(",","") == Role.ADMIN;
  }
  logout(): void {
    this.tokenStorageService.signOut();
    this.route.navigateByUrl("/login");
  }
}
