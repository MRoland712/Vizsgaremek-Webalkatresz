import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private _isLoggedIn = signal(false);
  private _userEmail = signal('');
  private _userName = signal('');

  isLoggedIn = this._isLoggedIn.asReadonly();
  userEmail = this._userEmail.asReadonly();
  userName = this._userName.asReadonly();

  constructor(private router: Router) {
    this.checkLoginStatus();
  }

  checkLoginStatus() {
    const token = localStorage.getItem('jwt');
    const email = localStorage.getItem('userEmail');
    const name = localStorage.getItem('userName');
    const isUserData = localStorage.getItem('isUserData');

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

    console.log('  _isLoggedIn:', this._isLoggedIn());
    console.log('  _userEmail:', this._userEmail());
    console.log('  _userName:', this._userName());
  }

  setLoggedIn(email?: string, userName?: string) {
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

    this._isLoggedIn.set(false);
    this._userEmail.set('');
    this._userName.set('');

    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return this._isLoggedIn();
  }
}
