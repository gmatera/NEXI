import { Injectable } from '@angular/core';
import { JwtBean, JwtProp } from './jwtBean';


@Injectable({
  providedIn: 'root'
})

export class SessionService {


  constructor() {}

  public clearAllStored() {
    
    sessionStorage.clear();
  }

  /** url used */
  public getUrlUsed(): string {
    return '/api/lmi';
  }


  /** jwt */
  public saveJwt(jwt: JwtBean) {
    //Object.keys(jwt).map(key => sessionStorage.setItem(key, jwt[key]));
    console.log("saveJwt aaa ");
  }

  public getAccessToken(): any {
    return sessionStorage.getItem(JwtProp.access_token);
  }

  public getIsLogged(): boolean {
    return sessionStorage.hasOwnProperty(JwtProp.access_token);
  }

  public getSessionId(): any {
    return sessionStorage.getItem(JwtProp.sessionId);
  }

  public getExpiresDate(): any {
    return sessionStorage.getItem(JwtProp.expiresDate);
  }

}
