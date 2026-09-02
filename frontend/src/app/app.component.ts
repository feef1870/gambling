import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { UserService } from './services/user.service';
import Keycloak from 'keycloak-js';
import { environment } from '../environments/environment';
import { hasAdminRole } from './util/auth';
import { ToastService } from './services/toast.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  private readonly keycloak = inject(Keycloak);
  readonly userService = inject(UserService);
  readonly toastService = inject(ToastService);

  readonly isAdmin = hasAdminRole(this.keycloak);

  ngOnInit() {
    this.userService.refreshUser();
  }

  logout() {
    this.keycloak.logout({ redirectUri: environment.redirectUri });
  }
}
