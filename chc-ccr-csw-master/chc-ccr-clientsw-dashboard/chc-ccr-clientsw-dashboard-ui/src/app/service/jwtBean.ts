export class JwtBean {
  access_token: string;
  refreshToken: string;
  issuedAtDate: string;
  expiresDate: string;
  expires_in: number;
  clientId: string;
  sessionId: string;

  constructor(
    access_token: string,
    refreshToken: string,
    issuedAtDate: string,
    expiresDate: string,
    expires_in: number,
    clientId: string,
    sessionId: string
) {
    this.access_token = access_token;
    this.refreshToken = refreshToken;
    this.issuedAtDate = issuedAtDate;
    this.expiresDate = expiresDate;
    this.expires_in = expires_in;
    this.clientId = clientId;
    this.sessionId = sessionId;
  }
}

export enum JwtProp {
  access_token = 'access_token',
  refreshToken = 'refreshToken',
  issuedAtDate = 'issuedAtDate',
  expiresDate = 'expiresDate',
  expires_in = 'expires_in',
  clientId = 'clientId',
  sessionId = 'sessionId'
}
