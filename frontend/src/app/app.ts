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
  userService = inject(UserService);
  private keycloak = inject(Keycloak);

  ngOnInit() {
    this.userService.refreshUser();
  }

  logout() {
    this.keycloak.logout({ redirectUri: 'http://localhost:4200' });
  }
}
