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
  private _isAdmin = signal(false);

  isLoggedIn = this._isLoggedIn.asReadonly();
  userEmail = this._userEmail.asReadonly();
  userName = this._userName.asReadonly();
  userFirstName = this._firstName.asReadonly();
  userLastName = this._lastName.asReadonly();
  userPhone = this._phone.asReadonly();
  isAdmin = this._isAdmin.asReadonly();

  constructor(private router: Router) {
    this.checkLoginStatus();
  }

  private setAdmin(role: string | null | undefined): void {
    const admin = role === 'admin' || role === 'ADMIN';
    this._isAdmin.set(admin);
    console.log('🔑 role:', role, '→ isAdmin:', admin);
    // ⭐ Üres stringet NEM mentjük — csak valódi értéket
    if (role && role !== '') localStorage.setItem('userRole', role);
  }

  checkLoginStatus() {
    const token = localStorage.getItem('jwt');
    const email = localStorage.getItem('userEmail');
    const name = localStorage.getItem('userName');
    const firstName = localStorage.getItem('firstName');
    const lastName = localStorage.getItem('lastName');
    const phone = localStorage.getItem('phone');
    const isUserData = localStorage.getItem('isUserData');
    const savedRole = localStorage.getItem('userRole');

    this._isLoggedIn.set(!!(token || isUserData === 'true'));
    this.setAdmin(savedRole);

    if (email) this._userEmail.set(email);
    if (name) this._userName.set(name);
    if (firstName) this._firstName.set(firstName);
    if (lastName) this._lastName.set(lastName);
    if (phone) this._phone.set(phone);
  }

  refreshUserData() {
    const token = localStorage.getItem('jwt');
    const email = localStorage.getItem('userEmail');
    const name = localStorage.getItem('userName');
    const firstName = localStorage.getItem('firstName');
    const lastName = localStorage.getItem('lastName');
    const phone = localStorage.getItem('phone');
    const isUserData = localStorage.getItem('isUserData');
    const savedRole = localStorage.getItem('userRole');

    if (token || isUserData === 'true') this._isLoggedIn.set(true);
    this.setAdmin(savedRole);
    if (email) this._userEmail.set(email);
    if (name) this._userName.set(name);
    if (firstName) this._firstName.set(firstName);
    if (lastName) this._lastName.set(lastName);
    if (phone) this._phone.set(phone);
  }

  ValidateToken(): void {
    const token = localStorage.getItem('jwt');
    if (!token) {
      this.logout();
      return;
    }
    this.JWTValidatorService.ValidateJWT().subscribe({
      next: (res) => {
        if (res.statusCode === 200 && res.status === 'success') {
          console.log('✅ Token érvényes');
        } else {
          this.logout();
        }
      },
      error: (err) => {
        if (err.status === 401 || err.status === 403) this.logout();
      },
    });
  }

  setLoggedIn(
    email?: string,
    userName?: string,
    firstName?: string,
    lastName?: string,
    phone?: string,
    role?: string,
  ) {
    this._isLoggedIn.set(true);
    this.setAdmin(role);

    if (email) {
      this._userEmail.set(email);
      localStorage.setItem('userEmail', email);
    }
    if (userName && userName !== '') {
      this._userName.set(userName);
      localStorage.setItem('userName', userName);
    }
    if (firstName && firstName !== '') {
      this._firstName.set(firstName);
      localStorage.setItem('firstName', firstName);
    }
    if (lastName && lastName !== '') {
      this._lastName.set(lastName);
      localStorage.setItem('lastName', lastName);
    }
    if (phone && phone !== '') {
      this._phone.set(phone);
      localStorage.setItem('phone', phone);
    }

    localStorage.setItem('isUserData', 'true');
  }

  logout(navigate: boolean = true) {
    localStorage.removeItem('jwt');
    localStorage.removeItem('saved-login-form');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userName');
    localStorage.removeItem('isUserData');
    localStorage.removeItem('firstName');
    localStorage.removeItem('lastName');
    localStorage.removeItem('phone');
    localStorage.removeItem('userRole');

    this._isLoggedIn.set(false);
    this._userEmail.set('');
    this._userName.set('');
    this._firstName.set('');
    this._lastName.set('');
    this._phone.set('');
    this._isAdmin.set(false);

    if (navigate) this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return this._isLoggedIn();
  }
}
