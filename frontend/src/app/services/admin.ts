import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page, User } from '../models/types';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/users';

  getUsers(search: string = '', page: number = 0, size: number = 10): Observable<Page<User>> {
    let params = new HttpParams()
      .set('search', search)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<User>>(this.apiUrl, { params });
  }

  addBalance(userId: string, amount: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${userId}/add-balance`, { amount });
  }
}
