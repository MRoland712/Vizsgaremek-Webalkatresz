import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
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
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  private destroyRef = inject(DestroyRef);

  // loginForm = new FormGroup({
  //   email: new FormControl(initialEmailValue, {
  //     validators: [Validators.email, Validators.required],
  //   }),
  //   password: new FormControl(initialPasswordValue, {
  //     validators: [Validators.required],
  //   }),
  // });

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
    // spread operator másolatot csinál az eredeti objectről

    // const secondObject = {
    //   ...finalLoginData,
    //   email: 'asdfg',
    // };

    console.log(finalLoginData);
    // console.log(secondObject);

    this.loginService.login(finalLoginData).subscribe({
      next: (res) => {
        console.log(res);
        localStorage.setItem('jwt', res.result.JWTToken!);
      },
    });
  }
  ngOnInit() {
    // const savedForm = window.localStorage.getItem('saved-login-form');
    //  if (savedForm) {
    //    const loadedForm = JSON.parse(savedForm);
    //   this.loginForm.patchValue({
    //     email: loadedForm.email,
    //    password: loadedForm.password,
    //  });
    //  }

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
