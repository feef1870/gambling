import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { UserService } from './services/user';
import Keycloak from 'keycloak-js';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  userService = inject(UserService);
  private keycloak = inject(Keycloak);

  ngOnInit() {
    this.userService.refreshUser();
  }

  logout() {
    this.keycloak.logout({ redirectUri: 'http://localhost:4200' });
  }

  isAdmin(): boolean {
    return this.keycloak.hasRealmRole('ADMIN') || this.keycloak.hasRealmRole('admin');
  }
}
