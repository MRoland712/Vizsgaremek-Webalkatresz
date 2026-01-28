import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { debounceTime } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { LoginService } from './login.service';
import { AuthService } from '../services/auth.service';

// Initial values localStorage-ból
let initialUsernameValue = '';
let initialEmailValue = '';
let initialPasswordValue = '';

const savedForm = window.localStorage.getItem('saved-login-form');
if (savedForm) {
  const loadedForm = JSON.parse(savedForm);
  initialUsernameValue = loadedForm.username || '';
  initialEmailValue = loadedForm.email || '';
  initialPasswordValue = loadedForm.password || '';
}

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private router = inject(Router);
  private authService = inject(AuthService);

  fb = inject(FormBuilder);
  loginService = inject(LoginService);

  // Login failed signal
  loginFailed = signal(false);

  // Login form - USERNAME HOZZÁADVA!
  loginForm = this.fb.nonNullable.group({
    username: [initialUsernameValue, [Validators.required, Validators.minLength(3)]],
    email: [initialEmailValue, [Validators.required, Validators.email]],
    password: [initialPasswordValue, [Validators.required, Validators.minLength(8)]],
  });

  ngOnInit() {
    // Form mentése localStorage-ba
    const subscription = this.loginForm.valueChanges.pipe(debounceTime(500)).subscribe({
      next: (value) => {
        window.localStorage.setItem(
          'saved-login-form',
          JSON.stringify({
            username: value.username,
            email: value.email,
            password: value.password,
          }),
        );
      },
    });

    // Form változáskor login error törlése
    const errorSubscription = this.loginForm.valueChanges.subscribe(() => {
      this.loginFailed.set(false);
    });

    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
      errorSubscription.unsubscribe();
    });
  }

  onLoginSubmit() {
    // Form érvényesség ellenőrzése
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    // Login error reset
    this.loginFailed.set(false);

    const finalLoginData = {
      username: this.loginForm.value.username!,
      email: this.loginForm.value.email!,
      password: this.loginForm.value.password!,
    };

    console.log('🔐 Login próbálkozás:', finalLoginData);

    this.loginService.login(finalLoginData).subscribe({
      next: (res) => {
        console.log('✅ Sikeres bejelentkezés!', res);

        // JWT token mentése
        localStorage.setItem('jwt', res.result.JWTToken!);

        // ⭐ AuthService setLoggedIn() hívása
        // Login-nál VAN username, így mentjük!
        this.authService.setLoggedIn(
          finalLoginData.email, // Email
          finalLoginData.username, // Username - MOST MÁR VAN!
        );

        console.log('✅ AuthService frissítve:');
        console.log('  Email:', finalLoginData.email);
        console.log('  Username:', finalLoginData.username);

        // Átirányítás főoldalra
        this.router.navigate(['/']);
      },
      error: (err: HttpErrorResponse) => {
        console.error('❌ Bejelentkezési hiba:', err);

        // Login failed státusz
        this.loginFailed.set(true);

        // Különböző HTTP hibák kezelése
        if (err.status === 401) {
          console.log('⚠️ Hibás username, email vagy jelszó');
        } else if (err.status === 0) {
          console.log('⚠️ Nincs hálózati kapcsolat');
        } else {
          console.log('⚠️ Szerver hiba:', err.status);
        }
      },
    });
  }

  // ==========================================
  // VALIDATION GETTERS
  // ==========================================

  get usernameIsInvalid() {
    return (
      this.loginForm.controls.username.touched &&
      this.loginForm.controls.username.dirty &&
      this.loginForm.controls.username.invalid
    );
  }

  get emailIsInvalid() {
    return (
      this.loginForm.controls.email.touched &&
      this.loginForm.controls.email.dirty &&
      this.loginForm.controls.email.invalid
    );
  }

  get passwordIsInvalid() {
    return (
      this.loginForm.controls.password.touched &&
      this.loginForm.controls.password.dirty &&
      this.loginForm.controls.password.invalid
    );
  }
}
