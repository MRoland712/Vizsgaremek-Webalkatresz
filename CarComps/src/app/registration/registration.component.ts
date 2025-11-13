import { Component } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import {
  mustContainNumbers,
  mustContainSpecialCharacters,
  mustContainUpperCase,
  emailMustHaveDomainValidator,
} from './register.service';

// Jelszó egyezés validátor
function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password');
  const rePassword = control.get('rePassword');

  if (!password || !rePassword) {
    return null;
  }

  return password.value === rePassword.value ? null : { passwordMismatch: true };
}

@Component({
  selector: 'app-reg',
  imports: [ReactiveFormsModule],
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent {
  signupForm = new FormGroup(
    {
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
        validators: [Validators.required],
      }),
    },
    { validators: passwordMatchValidator }
  );

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

  onSignUpSubmit() {
    if (this.signupForm.valid) {
      console.log('Form submitted:', {
        username: this.signupForm.value.username,
        email: this.signupForm.value.email,
        password: this.signupForm.value.password,
      });
      // Itt végezheted el a regisztrációs logikát
      // Például: this.authService.register(this.signupForm.value.username, this.signupForm.value.email, this.signupForm.value.password)
    } else {
      // Megjelölünk minden mezőt mint touched, hogy megjelenjenek a hibaüzenetek
      Object.keys(this.signupForm.controls).forEach((key) => {
        const control = this.signupForm.get(key);
        control?.markAsTouched();
        control?.markAsDirty();
      });
    }
  }
}
