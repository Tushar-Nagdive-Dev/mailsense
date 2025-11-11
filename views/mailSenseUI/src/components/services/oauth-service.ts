import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class OAuthService {
  private backendBaseUrl = 'http://localhost:8080';

  /**
   * Start OAuth2 login by redirecting the browser to the backend authorize endpoint.
   * The backend will 302 -> Google and Google will call the backend callback which
   * should redirect back to /auth/success on this frontend.
   */
  startAuthRedirect(provider: string, state?: string) {
    const redirectUri = encodeURIComponent(`${this.backendBaseUrl}/api/auth/oauth2/callback`);
    const params = new URLSearchParams();
    params.set('provider', provider);
    params.set('redirectUri', `${this.backendBaseUrl}/api/auth/oauth2/callback`);
    if (state) params.set('state', state);

    const url = `${this.backendBaseUrl}/api/auth/oauth2/authorize?${params.toString()}`;
    // use full-page navigation so browser follows backend redirects
    window.location.href = url;
  }
}
