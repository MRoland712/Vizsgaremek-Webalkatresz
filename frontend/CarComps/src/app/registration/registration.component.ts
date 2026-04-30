import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import {
  mustContainNumbers,
  mustContainSpecialCharacters,
  mustContainUpperCase,
  emailMustHaveDomainValidator,
  passwordMatchValidator,
  RegisterService,
} from '../services/register.service';
import { RegisterErrorResponse } from '../models/register.model';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-reg',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent {
  registerService = inject(RegisterService);
  private router = inject(Router);
  private http = inject(HttpClient);
  authService = inject(AuthService);

  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  // ==========================================
  // SIGNALS
  // ==========================================
  emailAlreadyExists = signal(false);
  aszfError = signal(false); // ⭐ ÁSZF nem fogadva el

  signupForm = new FormGroup({
    firstname: new FormControl('', {
      validators: [Validators.required],
    }),
    lastname: new FormControl('', {
      validators: [Validators.required],
    }),
    username: new FormControl('', {
      validators: [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(20),
        Validators.pattern(/^[a-zA-Z0-9_-]+$/),
      ],
    }),
    email: new FormControl('', {
      validators: [Validators.email, Validators.required, emailMustHaveDomainValidator],
    }),
    phone: new FormControl('', {
      validators: [Validators.required, Validators.maxLength(17), Validators.minLength(7)],
    }),
    password: new FormControl('', {
      validators: [
        Validators.required,
        Validators.minLength(8),
        mustContainUpperCase,
        mustContainNumbers,
        mustContainSpecialCharacters,
      ],
    }),
    rePassword: new FormControl('', {
      validators: [Validators.required, passwordMatchValidator],
    }),
    acceptAszf: new FormControl(false), // ⭐ ÁSZF elfogadása
    subscribeNews: new FormControl(false), // ⭐ Hírlevél feliratkozás
  });

  constructor() {
    // ==========================================
    // EMAIL VÁLTOZÁSKOR RESET
    // ==========================================
    this.signupForm.controls.email.valueChanges.subscribe(() => {
      this.emailAlreadyExists.set(false);
    });
  }

  // ==========================================
  // VALIDATION GETTERS
  // ==========================================
  get usernameIsInvalid() {
    return (
      this.signupForm.controls.username.touched &&
      this.signupForm.controls.username.dirty &&
      this.signupForm.controls.username.invalid
    );
  }

  get emailIsInvalid() {
    return (
      this.signupForm.controls.email.touched &&
      this.signupForm.controls.email.dirty &&
      this.signupForm.controls.email.invalid
    );
  }

  get passwordIsInvalid() {
    return (
      this.signupForm.controls.password.touched &&
      this.signupForm.controls.password.dirty &&
      this.signupForm.controls.password.invalid
    );
  }

  get rePasswordIsInvalid() {
    return (
      this.signupForm.controls.rePassword.touched &&
      this.signupForm.controls.rePassword.dirty &&
      (this.signupForm.controls.rePassword.invalid || this.signupForm.errors?.['passwordMismatch'])
    );
  }

  get firstnameIsInvalid() {
    return (
      this.signupForm.controls.firstname.touched &&
      this.signupForm.controls.firstname.dirty &&
      this.signupForm.controls.firstname.invalid
    );
  }

  get lastnameIsInvalid() {
    return (
      this.signupForm.controls.lastname.touched &&
      this.signupForm.controls.lastname.dirty &&
      this.signupForm.controls.lastname.invalid
    );
  }

  get phoneNumberIsInvalid() {
    return (
      this.signupForm.controls.phone.touched &&
      this.signupForm.controls.phone.dirty &&
      this.signupForm.controls.phone.invalid
    );
  }

  isLoading() {}

  // ==========================================
  // SUBMIT - 409 HIBAKEZELÉS
  // ==========================================
  onSignUpSubmit() {
    this.emailAlreadyExists.set(false);
    this.aszfError.set(false);

    // ⭐ ÁSZF kötelező
    if (!this.signupForm.value.acceptAszf) {
      this.aszfError.set(true);
      return;
    }

    console.log('📋 Form value:', this.signupForm.value);

    const finalRegisterData = {
      firstName: this.signupForm.value.firstname || 'User',
      lastName: this.signupForm.value.lastname || 'User',
      username: this.signupForm.value.username!,
      email: this.signupForm.value.email!,
      password: this.signupForm.value.password!,
      repassword: this.signupForm.value.rePassword!,
      phone: this.signupForm.value.phone!,
    };

    console.log('📤 Regisztráció küldése...', finalRegisterData);

    this.registerService.register(finalRegisterData).subscribe({
      next: (res) => {
        console.log('✅ Sikeres regisztráció!', res);

        // ⭐ Ha feliratkozott hírlevélre → updateUser
        if (this.signupForm.value.subscribeNews) {
          this.subscribeNewsletter(finalRegisterData.email);
        }

        // ⭐ NEM mentjük JWT-t
        // ⭐ NEM hívjuk setLoggedIn()-t
        // ⭐ CSAK login-ra irányítunk
        console.log('🔄 Átirányítás login oldalra...');

        localStorage.setItem('registrationSuccess', 'true');
        localStorage.setItem('registeredEmail', finalRegisterData.email);

        this.router.navigate(['/login']);
      },
      error: (err: HttpErrorResponse) => {
        console.error('❌ Regisztráció hiba:', err);
        // 409 = Email már létezik
        if (err.status === 409) {
          const errorResponse = err.error as RegisterErrorResponse;
          if (errorResponse.errors?.includes('EmailIsSameAsDB')) {
            console.log('⚠️ Email már használatban van!');
            this.emailAlreadyExists.set(true);
          }
        }
      },
    });
  }

  // ⭐ Hírlevél feliratkozás — updateUser endpoint
  private subscribeNewsletter(email: string) {
    const token = localStorage.getItem('jwt') ?? '';
    const headers = new HttpHeaders({ 'Content-Type': 'application/json', token });
    this.http
      .put(
        `${this.baseUrl}user/updateUser?email=${encodeURIComponent(email)}`,
        { isSubscribed: true },
        { headers },
      )
      .subscribe({
        next: () => {},
        error: () => {}, // ne blokkolja a regisztrációt ha ez hibázik
      });
  }
}
