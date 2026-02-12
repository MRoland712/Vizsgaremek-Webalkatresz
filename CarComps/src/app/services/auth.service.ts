import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { first } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
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

    console.log('🔐 AuthService checkLoginStatus:');
    console.log('  token:', token);
    console.log('  email:', email);
    console.log('  name:', name);
    console.log('  isUserData:', isUserData);

    // Ha van token vagy van userData flag, akkor bejelentkezett
    if (token || isUserData === 'true') {
      this._isLoggedIn.set(true);
    } else {
      this._isLoggedIn.set(false);
    }

    // Email és userName beállítása függetlenül attól, hogy van-e token
    if (email) {
      this._userEmail.set(email);
    }
    if (name) {
      this._userName.set(name);
    }
    if (firstName) {
      this._firstName.set(firstName);
    }
    if (lastName) {
      this._lastName.set(lastName);
    }
    if (phone) {
      this._phone.set(phone);
    }

    console.log('  _isLoggedIn:', this._isLoggedIn());
    console.log('  _userEmail:', this._userEmail());
    console.log('  _userName:', this._userName());
    console.log('  userFirstName:', this.userFirstName());
    console.log('  userLastName:', this.userLastName());
    console.log('  userPhone:', this.userPhone());
  }

  // ⭐ ÚJ METÓDUS: Signal-ok frissítése localStorage-ból
  refreshUserData() {
    const email = localStorage.getItem('userEmail');
    const name = localStorage.getItem('userName');
    const token = localStorage.getItem('jwt');
    const isUserData = localStorage.getItem('isUserData');
    const firstName = localStorage.getItem('firstName');
    const lastName = localStorage.getItem('lastName');
    const phone = localStorage.getItem('phone');

    console.log('🔄 refreshUserData meghívva:');
    console.log('  email:', email);
    console.log('  name:', name);

    // Login státusz frissítése
    if (token || isUserData === 'true') {
      this._isLoggedIn.set(true);
    }

    // Email frissítése
    if (email) {
      this._userEmail.set(email);
      console.log('✅ Email signal frissítve:', email);
    }

    // UserName frissítése
    if (name) {
      this._userName.set(name);
      console.log('✅ UserName signal frissítve:', name);
    }

    if (firstName) {
      this._firstName.set(firstName);
    }
    if (lastName) {
      this._lastName.set(lastName);
    }
    if (phone) {
      this._phone.set(phone);
    }

    console.log('🔄 Signals után:');
    console.log('  userName():', this._userName());
    console.log('  userEmail():', this._userEmail());
    console.log('  userFirstName():', this.userFirstName());
    console.log('  userLastName():', this.userLastName());
    console.log('  userPhone():', this.userPhone());
  }

  setLoggedIn(
    email?: string,
    userName?: string,
    firstName?: string,
    lastName?: string,
    phone?: string,
  ) {
    this._isLoggedIn.set(true);

    // Email mentése (mindig van)
    if (email) {
      this._userEmail.set(email);
      localStorage.setItem('userEmail', email);
    }

    // ⭐ JAVÍTÁS: userName mentése (csak ha van - nem undefined)
    // Ha undefined, akkor NEM írjuk felül a meglévőt!
    if (userName !== undefined && userName !== '') {
      this._userName.set(userName);
      localStorage.setItem('userName', userName);
      console.log('✅ userName mentve:', userName);
    }

    // FirstName mentése
    if (firstName !== undefined && firstName !== '') {
      this._firstName.set(firstName);
      localStorage.setItem('firstName', firstName);
    }

    // LastName mentése
    if (lastName !== undefined && lastName !== '') {
      this._lastName.set(lastName);
      localStorage.setItem('lastName', lastName);
    }

    // Phone mentése
    if (phone !== undefined && phone !== '') {
      this._phone.set(phone);
      localStorage.setItem('phone', phone);
    }

    // Az oldal újratöltés után is tudja, hogy van adat
    localStorage.setItem('isUserData', 'true');

    console.log('✅ setLoggedIn meghívva:');
    console.log('  email:', email);
    console.log('  userName:', userName);
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

    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return this._isLoggedIn();
  }
}
