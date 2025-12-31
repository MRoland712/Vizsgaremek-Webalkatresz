// src/app/app.routes.ts

import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { HomepageComponent } from './homepage.component/homepage.component';

export const routes: Routes = [
  // ==========================================
  // PUBLIKUS ROUTE-OK (mindenki láthatja)
  // ==========================================
  {
    path: '',
    component: HomepageComponent,
    // NINCS canActivate! Mindenki láthatja
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'registration',
    component: RegistrationComponent,
  },

  // ==========================================
  // VÉDETT ROUTE-OK (csak bejelentkezve)
  // ==========================================
  // {
  //   path: 'cart',
  //   loadComponent: () => import('./cart/cart.component').then((m) => m.CartComponent),
  //   canActivate: [authGuard], // ← VÉDETT! Login kell
  // },
  // {
  //   path: 'checkout',
  //   loadComponent: () => import('./checkout/checkout.component').then((m) => m.CheckoutComponent),
  //   canActivate: [authGuard], // ← VÉDETT! Login kell
  // },
  // {
  //   path: 'profile',
  //   loadComponent: () => import('./profile/profile.component').then((m) => m.ProfileComponent),
  //   canActivate: [authGuard], // ← VÉDETT! Login kell
  // },

  // ==========================================
  // CATCH-ALL (404)
  // ==========================================
  {
    path: '**',
    redirectTo: '', // Homepage-re irányít
  },
];
