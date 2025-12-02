import { Routes } from '@angular/router';
import { HomepageComponent } from './homepage.component/homepage.component';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';

export const routes: Routes = [
  { path: '', component: HomepageComponent },
  { path: 'login', component: LoginComponent },
  { path: 'registration', component: RegistrationComponent },
  { path: '**', redirectTo: '' }, // Minden más útvonal a homepage-re irányít
];
