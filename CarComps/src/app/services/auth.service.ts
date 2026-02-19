import { inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { JWTValidateService } from './jwtvalidate.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  JWTValidatorService = inject(JWTValidateService);

  private _isLoggedIn = signal(false);
  private _userEmail = signal('');
  private _userName = signal('');
  private _lastName = signal('');
  private _firstName = signal('');
  private _phone = signal('');

  isLoggedIn = this._isLoggedIn.asReadonly();
  userEmail = this._userEmail.asReadonly();
  userName = this._userName.asReadonly();
  userFirstName = this._firstName.asReadonly();
  userLastName = this._lastName.asReadonly();
  userPhone = this._phone.asReadonly();

  constructor(private router: Router) {
    this.checkLoginStatus();
  }

  checkLoginStatus() {
    const token = localStorage.getItem('jwt');
    const email = localStorage.getItem('userEmail');
    const name = localStorage.getItem('userName');
    const isUserData = localStorage.getItem('isUserData');
    const firstName = localStorage.getItem('firstName');
    const lastName = localStorage.getItem('lastName');
    const phone = localStorage.getItem('phone');

    if (token || isUserData === 'true') {
      this._isLoggedIn.set(true);
    } else {
      this._isLoggedIn.set(false);
    }

    if (email) this._userEmail.set(email);
    if (name) this._userName.set(name);
    if (firstName) this._firstName.set(firstName);
    if (lastName) this._lastName.set(lastName);
    if (phone) this._phone.set(phone);
  }

  refreshUserData() {
    const email = localStorage.getItem('userEmail');
    const name = localStorage.getItem('userName');
    const token = localStorage.getItem('jwt');
    const isUserData = localStorage.getItem('isUserData');
    const firstName = localStorage.getItem('firstName');
    const lastName = localStorage.getItem('lastName');
    const phone = localStorage.getItem('phone');

    if (token || isUserData === 'true') {
      this._isLoggedIn.set(true);
    }

    if (email) this._userEmail.set(email);
    if (name) this._userName.set(name);
    if (firstName) this._firstName.set(firstName);
    if (lastName) this._lastName.set(lastName);
    if (phone) this._phone.set(phone);
  }

  // ⭐ Token validálás
  ValidateToken(): void {
    const token = localStorage.getItem('jwt');

    if (!token) {
      console.warn('⚠️ Nincs JWT token');
      this.logout();
      return;
    }

    console.log('🔍 Token validálás...');

    this.JWTValidatorService.ValidateJWT().subscribe({
      next: (res) => {
        if (res.statusCode === 200 && res.status === 'success') {
          console.log('✅ Token érvényes');
          // Token OK - marad bejelentkezve
        } else {
          console.warn('❌ Token invalid:', res.errors);
          this.logout();
        }
      },
      error: (err) => {
        console.error('❌ Token validálási hiba:', err);
        if (err.status === 401 || err.status === 403) {
          this.logout();
        }
      },
    });
  }

  setLoggedIn(
    email?: string,
    userName?: string,
    firstName?: string,
    lastName?: string,
    phone?: string,
  ) {
    this._isLoggedIn.set(true);

    if (email) {
      this._userEmail.set(email);
      localStorage.setItem('userEmail', email);
    }

    if (userName !== undefined && userName !== '') {
      this._userName.set(userName);
      localStorage.setItem('userName', userName);
    }

    if (firstName !== undefined && firstName !== '') {
      this._firstName.set(firstName);
      localStorage.setItem('firstName', firstName);
    }

    if (lastName !== undefined && lastName !== '') {
      this._lastName.set(lastName);
      localStorage.setItem('lastName', lastName);
    }

    if (phone !== undefined && phone !== '') {
      this._phone.set(phone);
      localStorage.setItem('phone', phone);
    }

    localStorage.setItem('isUserData', 'true');
  }

  logout() {
    localStorage.removeItem('jwt');
    localStorage.removeItem('saved-login-form');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userName');
    localStorage.removeItem('isUserData');
    localStorage.removeItem('firstName');
    localStorage.removeItem('lastName');
    localStorage.removeItem('phone');

    this._isLoggedIn.set(false);
    this._userEmail.set('');
    this._userName.set('');
    this._firstName.set('');
    this._lastName.set('');
    this._phone.set('');

    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return this._isLoggedIn();
  }
}
