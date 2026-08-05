import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserResponse } from '../models/types';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/users';

  currentUser = signal<UserResponse | null>(null);

  refreshUser() {
    this.http.get<UserResponse>(`${this.apiUrl}/me`).subscribe({
      next: (user) => this.currentUser.set(user),
      error: (err) => console.error('Failed to fetch user', err),
    });
  }

  claimLaborWage() {
    return this.http.post<void>('http://localhost:8080/api/labor/claim', {});
  }
}
