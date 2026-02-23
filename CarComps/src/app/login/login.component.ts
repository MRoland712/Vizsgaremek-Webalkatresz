import { Component, DestroyRef, inject, OnInit, signal, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { debounceTime } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { LoginService } from './login.service';
import { AuthService } from '../services/auth.service';
import { OtpComponent } from '../verifications/otp.component/otp.component';

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

  // ⭐ OTP Dialog reference
  @ViewChild(OtpComponent) otpDialog!: OtpComponent;

  // Login failed signal
  loginFailed = signal(false);

  loginForm = this.fb.nonNullable.group({
    email: [initialEmailValue, [Validators.required, Validators.email]],
    password: [initialPasswordValue, Validators.required],
  });

  onLoginSubmit() {
    // Form érvényesség ellenőrzése
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    // Login error reset
    this.loginFailed.set(false);

    const finalLoginData = {
      email: this.loginForm.value.email!,
      password: this.loginForm.value.password!,
    };

    console.log('🔐 Login próbálkozás:', finalLoginData);

    this.loginService.login(finalLoginData).subscribe({
      next: (res) => {
        console.log('✅ Sikeres bejelentkezés!', res);

        // JWT token mentése
        localStorage.setItem('jwt', res.result.JWTToken!);
        localStorage.setItem('userEmail', finalLoginData.email);

        // Username a response-ból
        const username = res.result.username || finalLoginData.email;
        const firstname = res.result.firstName || '';
        const lastname = res.result.lastName || '';
        const phone = res.result.phone || '';

        localStorage.setItem('userName', username);
        localStorage.setItem('firstName', firstname);
        localStorage.setItem('lastName', lastname);
        localStorage.setItem('phone', phone || '');
        // AuthService setLoggedIn()
        this.authService.setLoggedIn(finalLoginData.email, username);

        console.log('✅ AuthService frissítve');

        // ⭐ OTP Dialog megnyitása
        console.log('📧 OTP Dialog megnyitása...');
        setTimeout(() => {
          this.otpDialog.open(finalLoginData.email);
        }, 100);
      },
      error: (err: HttpErrorResponse) => {
        console.error('❌ Bejelentkezési hiba:', err);
        this.loginFailed.set(true);

        if (err.status === 401) {
          console.log('⚠️ Hibás email vagy jelszó');
        } else if (err.status === 0) {
          console.log('⚠️ Nincs hálózati kapcsolat');
        } else {
          console.log('⚠️ Szerver hiba:', err.status);
        }
      },
    });
  }

  // ⭐ OTP sikeres verifikáció után
  onOTPVerified() {
    console.log('✅ OTP sikeresen megerősítve!');

    // Mark user as verified in localStorage
    localStorage.setItem('emailVerified', 'true');

    // Navigáció főoldalra
    this.router.navigate(['/']);
  }

  // ⭐ OTP dialog bezárása (skip)
  onOTPCancelled() {
    console.log('⚠️ OTP megerősítés kihagyva');

    // Navigáció főoldalra (OTP nélkül is)
    this.router.navigate(['/']);
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

    // Form változáskor login error törlése
    const errorSubscription = this.loginForm.valueChanges.subscribe(() => {
      this.loginFailed.set(false);
    });

    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
      errorSubscription.unsubscribe();
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
