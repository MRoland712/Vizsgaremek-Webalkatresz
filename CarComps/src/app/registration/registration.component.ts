import { Component, inject } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {
  mustContainNumbers,
  mustContainSpecialCharacters,
  mustContainUpperCase,
  emailMustHaveDomainValidator,
  passwordMatchValidator,
  RegisterService,
} from './register.service';

@Component({
  selector: 'app-reg',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent {
  registerService = inject(RegisterService);
  private router = inject(Router);

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
        Validators.pattern(/^[a-zA-Z0-9_-]+$/), // csak betűk, számok, aláhúzás és kötőjel
      ],
    }),
    email: new FormControl('', {
      validators: [Validators.email, Validators.required, emailMustHaveDomainValidator],
    }),
    phone: new FormControl('', {
      validators: [Validators.required, Validators.maxLength(17)],
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
  });

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
      this.signupForm.controls.rePassword.touched &&
      this.signupForm.controls.rePassword.dirty &&
      this.signupForm.controls.firstname.invalid
    );
  }

  get lastnameIsInvalid() {
    return (
      this.signupForm.controls.rePassword.touched &&
      this.signupForm.controls.rePassword.dirty &&
      this.signupForm.controls.firstname.invalid
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

  onSignUpSubmit() {
    const finalRegisterData = {
      firstName: this.signupForm.value.firstname!,
      lastName: this.signupForm.value.lastname!,
      username: this.signupForm.value.username!,
      email: this.signupForm.value.email!,
      password: this.signupForm.value.password!,
      repassword: this.signupForm.value.rePassword!,
      phone: this.signupForm.value.phone!,
    };
    this.registerService.register(finalRegisterData).subscribe({
      next: (res) => {
        console.log(res);
        localStorage.setItem('jwt', res.result.JWTToken!);
        // Sikeres regisztráció után navigálás a főoldalra
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error('Regisztrációs hiba:', err);
        // Itt kezelheted a hibákat (pl. toast üzenet)
      },
    });
  }
}
