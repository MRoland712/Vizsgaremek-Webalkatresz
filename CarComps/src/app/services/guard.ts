// src/app/guards/auth.guard.ts

import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * AUTH GUARD
 * Ellenőrzi hogy be van-e jelentkezve a user
 * Ha nincs → átirányít /registration-ra
 */
export const authGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Ellenőrizzük hogy be van-e jelentkezve
  if (authService.isAuthenticated()) {
    // ✅ Be van jelentkezve → beengedjük
    return true;
  } else {
    // ❌ NINCS bejelentkezve → REGISZTRÁCIÓRA irányítjuk
    console.log('AuthGuard: Nincs bejelentkezve, átirányítás /registration-ra');
    router.navigate(['/registration']);
    return false;
  }
};
