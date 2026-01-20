// src/app/login/login.component.ts

import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { debounceTime } from 'rxjs';
import { LoginService } from './login.service';
import { AuthService } from '../services/auth.service';

let initialEmailValue = '';
let initialPasswordValue = '';

const savedForm = window.localStorage.getItem('saved-login-form');
if (savedForm) {
  const loadedForm = JSON.parse(savedForm);
  initialEmailValue = loadedForm.email;
  initialPasswordValue = loadedForm.password;
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
  fb = inject(FormBuilder);
  loginService = inject(LoginService);
  authService = inject(AuthService); // ← AuthService inject

  loginForm = this.fb.nonNullable.group({
    email: [initialEmailValue, [Validators.required, Validators.email]],
    password: [initialPasswordValue, Validators.required],
  });

  onLoginSubmit() {
    const finalLoginData = {
      email: this.loginForm.value.email!,
      password: this.loginForm.value.password!,
    };

    console.log('Login adatok:', finalLoginData);

    this.loginService.login(finalLoginData).subscribe({
      next: (res) => {
        console.log('Sikeres bejelentkezés:', res);

        // JWT token mentése
        localStorage.setItem('jwt', res.result.JWTToken!);

        // ==========================================
        // AuthService-nek szólunk hogy bejelentkezett
        // ==========================================
        this.authService.setLoggedIn();

        // Sikeres bejelentkezés után navigálás a FŐOLDALRA
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error('Bejelentkezési hiba:', err);
        // TODO: Hibakezelés (pl. toast üzenet)
      },
    });
  }

  ngOnInit() {
    const subscription = this.loginForm.valueChanges.pipe(debounceTime(500)).subscribe({
      next: (value) => {
        window.localStorage.setItem(
          'saved-login-form',
          JSON.stringify({ email: value.email, password: value.password }),
        );
      },
    });

    this.destroyRef.onDestroy(() => subscription.unsubscribe());
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
