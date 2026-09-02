import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserResponse } from '../models/types';
import { environment } from '../../environments/environment';
import { extractErrorMessage } from '../util/errors';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);

  private readonly _currentUser = signal<UserResponse | null>(null);
  readonly currentUser = this._currentUser.asReadonly();

  readonly loadError = signal<string | null>(null);

  refreshUser() {
    this.loadError.set(null);

    this.http.get<UserResponse>(`${environment.apiUrl}/api/users/me`).subscribe({
      next: (user) => this._currentUser.set(user),
      error: (err) =>
        this.loadError.set(extractErrorMessage(err, "Could not fetch user details")),
    });
  }

  claimLaborWage() {
    return this.http.post<void>(`${environment.apiUrl}/api/labor/claim`, null);
  }
}
