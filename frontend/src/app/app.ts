import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { UserService } from './services/user';
import { UserResponse } from './models/types';
import Keycloak from 'keycloak-js';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  private userService = inject(UserService);
  private keycloak = inject(Keycloak);

  currentUser = signal<UserResponse | null>(null);

  ngOnInit() {
    this.userService.getCurrentUser().subscribe({
      next: (user) => this.currentUser.set(user),
      error: (err) => console.error('Failed to fetch user', err),
    });
  }

  logout() {
    this.keycloak.logout({ redirectUri: 'http://localhost:4200' });
  }
}
