import { inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { JWTValidateService } from './jwtvalidate.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  JWTValidatorService = inject(JWTValidateService);

  private _isLoggedIn = signal(false);
  private _userId = signal<number>(0); // ⭐ ÚJ
  private _userEmail = signal('');
  private _userName = signal('');
  private _lastName = signal('');
  private _firstName = signal('');
  private _phone = signal('');
  private _isAdmin = signal(false);

  isLoggedIn = this._isLoggedIn.asReadonly();
  userId = this._userId.asReadonly(); // ⭐ ÚJ
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
    const userId = localStorage.getItem('userId'); // ⭐ ÚJ

    this._isLoggedIn.set(!!(token || isUserData === 'true'));
    this.setAdmin(savedRole);

    if (email) this._userEmail.set(email);
    if (name) this._userName.set(name);
    if (firstName) this._firstName.set(firstName);
    if (lastName) this._lastName.set(lastName);
    if (phone) this._phone.set(phone);
    if (userId) this._userId.set(Number(userId)); // ⭐ ÚJ
  }

  refreshUserData() {
    this.checkLoginStatus();
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
    userId?: number, // ⭐ ÚJ
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
    if (userId && userId > 0) {
      // ⭐ ÚJ
      this._userId.set(userId);
      localStorage.setItem('userId', String(userId));
    }

    localStorage.setItem('isUserData', 'true');
  }

  // ⭐ ÚJ — profil betöltés után hívjuk (getUserById response alapján)
  setUserProfile(profile: {
    id: number;
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    phone: string;
    role?: string;
  }) {
    this._userId.set(profile.id);
    this._firstName.set(profile.firstName);
    this._lastName.set(profile.lastName);
    this._userName.set(profile.username);
    this._userEmail.set(profile.email);
    this._phone.set(profile.phone);
    if (profile.role) this.setAdmin(profile.role);

    localStorage.setItem('userId', String(profile.id));
    localStorage.setItem('firstName', profile.firstName);
    localStorage.setItem('lastName', profile.lastName);
    localStorage.setItem('userName', profile.username);
    localStorage.setItem('userEmail', profile.email);
    localStorage.setItem('phone', profile.phone);
  }

  logout(navigate: boolean = true) {
    [
      'jwt',
      'saved-login-form',
      'userEmail',
      'userName',
      'isUserData',
      'firstName',
      'lastName',
      'phone',
      'userRole',
      'userId',
    ].forEach((k) => localStorage.removeItem(k));
    this._isLoggedIn.set(false);
    this._userId.set(0);
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
