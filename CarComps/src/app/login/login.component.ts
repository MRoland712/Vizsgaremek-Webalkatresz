import { Component, DestroyRef, inject, OnInit, signal, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { debounceTime } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

import { AuthService } from '../services/auth.service';
import { OtpComponent } from '../verifications/otp.component/otp.component';
import { LoginService } from '../services/login.service';

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
  imports: [ReactiveFormsModule, RouterLink, OtpComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private router = inject(Router);
  private authService = inject(AuthService);

  fb = inject(FormBuilder);
  loginService = inject(LoginService);

  @ViewChild(OtpComponent) otpDialog!: OtpComponent;
  loginFailed = signal(false);

  loginForm = this.fb.nonNullable.group({
    email: [initialEmailValue, [Validators.required, Validators.email]],
    password: [initialPasswordValue, Validators.required],
  });

  onLoginSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loginFailed.set(false);
    const { email, password } = this.loginForm.value;

    this.loginService.login({ email: email!, password: password! }).subscribe({
      next: (res) => {
        // Teljes raw response kiírása

        localStorage.setItem('jwt', res.result.JWTToken!);
        localStorage.setItem('userEmail', email!);
        localStorage.setItem('userName', res.result.username || email!);
        localStorage.setItem('firstName', res.result.firstName || '');
        localStorage.setItem('lastName', res.result.lastName || '');
        localStorage.setItem('phone', res.result.phone || '');

        // ⭐ role: res.result.role — a modellben most már benne van
        const role = res.result.role;

        this.authService.setLoggedIn(
          email,
          res.result.username,
          res.result.firstName,
          res.result.lastName,
          res.result.phone,
          role,
        );

        setTimeout(() => this.otpDialog.open(email!), 100);
      },
      error: (err: HttpErrorResponse) => {
        console.error('❌ Login hiba:', err);
        this.loginFailed.set(true);
      },
    });
  }

  onOTPVerified() {
    localStorage.setItem('emailVerified', 'true');
    if (this.authService.isAdmin()) {
      this.router.navigate(['/admin']);
    } else {
      this.router.navigate(['/']);
    }
  }

  onOTPCancelled() {
    this.authService.logout(false);
  }

  ngOnInit() {
    const sub1 = this.loginForm.valueChanges
      .pipe(debounceTime(500))
      .subscribe((v) =>
        localStorage.setItem(
          'saved-login-form',
          JSON.stringify({ email: v.email, password: v.password }),
        ),
      );
    const sub2 = this.loginForm.valueChanges.subscribe(() => this.loginFailed.set(false));
    this.destroyRef.onDestroy(() => {
      sub1.unsubscribe();
      sub2.unsubscribe();
    });
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
