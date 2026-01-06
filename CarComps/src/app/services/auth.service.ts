import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private _isLoggedIn = signal(false);

  isLoggedIn = this._isLoggedIn.asReadonly();

  constructor(private router: Router) {
    this.checkLoginStatus();
  }

  checkLoginStatus() {
    const token = localStorage.getItem('jwt');

    if (token) {
      this._isLoggedIn.set(true);
    } else {
      this._isLoggedIn.set(false);
    }
  }

  setLoggedIn() {
    this._isLoggedIn.set(true);
  }

  logout() {
    localStorage.removeItem('jwt');
    localStorage.removeItem('saved-login-form');

    this._isLoggedIn.set(false);

    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return this._isLoggedIn();
  }
}
