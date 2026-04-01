import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  login(payload: LoginRequest): Observable<LoginResponse> {
    console.log('login payload', payload);

    return of({
      token: 'fake-token',
      tokenType: 'Bearer'
    });
  }

  logout(): void {
    localStorage.removeItem('taskmanager_token');
  }
}
