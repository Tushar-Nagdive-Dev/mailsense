import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LoginButton } from '../components/component/login-button/login-button';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, LoginButton],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('mailSenseUI');
}
