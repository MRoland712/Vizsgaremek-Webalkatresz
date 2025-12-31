// src/app/services/auth.service.ts

import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // Signal - reaktív state (be van-e jelentkezve)
  private _isLoggedIn = signal(false);

  // Getter - csak olvasható kívülről
  isLoggedIn = this._isLoggedIn.asReadonly();

  constructor(private router: Router) {
    // Ellenőrizzük localStorage-ban van-e JWT token
    this.checkLoginStatus();
  }

  // Ellenőrzi hogy van-e JWT token
  checkLoginStatus() {
    const token = localStorage.getItem('jwt');

    if (token) {
      // Van token → be van jelentkezve
      this._isLoggedIn.set(true);
    } else {
      // Nincs token → nincs bejelentkezve
      this._isLoggedIn.set(false);
    }
  }

  // Bejelentkezés után hívd meg!
  setLoggedIn() {
    this._isLoggedIn.set(true);
  }

  // Kijelentkezés
  logout() {
    // Töröljük a JWT tokent
    localStorage.removeItem('jwt');
    localStorage.removeItem('saved-login-form');

    // Frissítjük a state-et
    this._isLoggedIn.set(false);

    // Átirányítás login-ra
    this.router.navigate(['/login']);
  }

  // Egyszerű getter - true/false
  isAuthenticated(): boolean {
    return this._isLoggedIn();
  }
}
