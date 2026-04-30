import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Ellenőrizzük hogy be van-e jelentkezve
  if (authService.isAuthenticated()) {
    return true;
  } else {
    console.log('AuthGuard: Nincs bejelentkezve, átirányítás /registration-ra');
    router.navigate(['/registration']);
    return false;
  }
};
export const adminGuard = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  if (auth.isAdmin()) {
    return true;
  }

  console.warn('nem admin felhasználó');
  router.navigate(['/']);
  return false;
};
