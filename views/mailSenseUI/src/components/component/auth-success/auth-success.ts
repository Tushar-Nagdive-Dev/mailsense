import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TokenService } from '../../services/token-service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-auth-success',
  imports: [],
  templateUrl: './auth-success.html',
  styleUrl: './auth-success.scss',
})
export class AuthSuccess implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private tokenService: TokenService,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(async params => {
      const accessToken = params['accessToken'];
      const refreshToken = params['refreshToken'];
      const userId = params['userId'];
      const email = params['email'];

      if (!accessToken) {
        // handle error or show message
        console.error('No accessToken in callback');
        this.router.navigate(['/']);
        return;
      }

      // store tokens (simple approach)
      const user = userId ? { id: userId, email } : null;
      this.tokenService.setTokens(accessToken, refreshToken, user);

      // optionally: fetch /api/auth/me to verify session
      // await this.http.get('/api/auth/me').toPromise();

      // navigate to app landing/dashboard
      this.router.navigate(['/']);
    });
  }
}
