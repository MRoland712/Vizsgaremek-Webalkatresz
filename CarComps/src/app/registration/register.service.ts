import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AbstractControl, ValidationErrors } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { RegisterBody, RegisterResponse } from './register.model';

//Speciális karakterek validator
export function mustContainSpecialCharacters(control: AbstractControl) {
  const specialChars = ['?', '.', ':', '#', '/', '@', '&', ',', '!', '=', '-', '_', '%', '$'];

  for (let i = 0; i < specialChars.length; i++) {
    if (control.value.includes(specialChars[i])) return null;
  }
  return { doesNotContainSpecialCharacters: true };
}
//Nincs azonos email validator
export function emailisUnique(control: AbstractControl) {
  if (control.value !== 'test@example.com') {
    return of(null);
  }
  return of({ notUnique: true });
}
//Email Domain validátor
export function emailMustHaveDomainValidator(control: AbstractControl): ValidationErrors | null {
  const email = control.value;
  if (!email) return null;

  // Csak azt nézi, hogy van-e @ után legalább egy ponttal végződő domain regex
  const hasDomain = /@[^@]+\.[a-zA-Z]{2,}$/.test(email);

  return hasDomain ? null : { missingDomain: true };
}

//Nagybetűt tartalmazó validator
export function mustContainUpperCase(control: AbstractControl) {
  const upperCaseChars = [
    'A',
    'B',
    'C',
    'D',
    'E',
    'F',
    'G',
    'H',
    'I',
    'J',
    'K',
    'L',
    'M',
    'N',
    'O',
    'P',
    'Q',
    'R',
    'S',
    'T',
    'U',
    'V',
    'W',
    'X',
    'Y',
    'Z',
  ];

  for (let i = 0; i < upperCaseChars.length; i++) {
    if (control.value.includes(upperCaseChars[i])) return null;
  }
  return { doesNotContainUpperCase: true };
}
//Számot tartalmazó validator
export function mustContainNumbers(control: AbstractControl) {
  const numberChars = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'];

  for (let i = 0; i < numberChars.length; i++) {
    if (control.value.includes(numberChars[i])) return null;
  }
  return { doesNotContainNumbers: true };
}
// Jelszó egyezés validátor
export function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password');
  const rePassword = control.get('rePassword');

  if (!password || !rePassword) {
    return null;
  }

  return password.value === rePassword.value ? null : { passwordMismatch: true };
}
@Injectable({
  providedIn: 'root',
})
export class RegisterService {
  private readonly baseUrl = 'http://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';
  private readonly registerUrl = this.baseUrl + 'user/createUser';
  private httpClient = inject(HttpClient);
  headers = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': 'http://localhost:4200',
    }),
  };
  register(body: RegisterBody): Observable<RegisterResponse> {
    return this.httpClient.post<RegisterResponse>(this.registerUrl, body, this.headers);
  }
}
