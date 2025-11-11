import { Component } from '@angular/core';
import { OAuthService } from '../../services/oauth-service';

@Component({
  selector: 'app-login-button',
  imports: [],
  templateUrl: './login-button.html',
  styleUrl: './login-button.scss',
})
export class LoginButton {

  constructor(private oauth: OAuthService) {}

  login() {
    // optional: generate and store state for CSRF validation
    const state = Math.random().toString(36).slice(2);
    sessionStorage.setItem('oauth_state', state);
    this.oauth.startAuthRedirect('google', state);
  }
}
