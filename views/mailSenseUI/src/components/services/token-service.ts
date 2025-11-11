import { Injectable } from '@angular/core';

const ACCESS_KEY = 'mailsense_access_token';
const REFRESH_KEY = 'mailsense_refresh_token';
const USER_KEY = 'mailsense_user';

@Injectable({
  providedIn: 'root',
})
export class TokenService {

  setTokens(accessToken: string, refreshToken?: string, user?: any) {
    if (accessToken) localStorage.setItem(ACCESS_KEY, accessToken);
    if (refreshToken) localStorage.setItem(REFRESH_KEY, refreshToken);
    if (user) localStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_KEY);
  }

  getUser(): any | null {
    const v = localStorage.getItem(USER_KEY);
    return v ? JSON.parse(v) : null;
  }

  clear() {
    localStorage.removeItem(ACCESS_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
  }
  
}
