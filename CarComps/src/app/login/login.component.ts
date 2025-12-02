import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { debounceTime } from 'rxjs';
import { LoginService } from './login.service';

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

  loginForm = this.fb.nonNullable.group({
    email: [initialEmailValue, [Validators.required, Validators.email]],
    password: [initialPasswordValue, Validators.required],
  });

  onLoginSubmit() {
    const finalLoginData = {
      email: this.loginForm.value.email!,
      password: this.loginForm.value.password!,
    };

    console.log(finalLoginData);

    this.loginService.login(finalLoginData).subscribe({
      next: (res) => {
        console.log(res);
        localStorage.setItem('jwt', res.result.JWTToken!);
        // Sikeres bejelentkezés után navigálás a főoldalra
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error('Bejelentkezési hiba:', err);
        // Itt kezelheted a hibákat (pl. toast üzenet)
      },
    });
  }

  ngOnInit() {
    const subscription = this.loginForm.valueChanges.pipe(debounceTime(500)).subscribe({
      next: (value) => {
        window.localStorage.setItem(
          'saved-login-form',
          JSON.stringify({ email: value.email, password: value.password })
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
