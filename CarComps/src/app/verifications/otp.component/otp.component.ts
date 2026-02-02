import { Component, inject, signal, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { HttpErrorResponse } from '@angular/common/http';
import { OtpService } from '../../services/otp.service';

@Component({
  selector: 'app-otp-dialog',
  imports: [ReactiveFormsModule],
  templateUrl: './otp.component.html',
  styleUrl: './otp.component.css',
})
export class OtpComponent {
  private fb = inject(FormBuilder);
  private otpService = inject(OtpService);

  // Inputs
  userEmail = signal<string>('');

  // Outputs
  verified = output<void>();
  cancelled = output<void>();

  // State
  isOpen = signal(false);
  isSending = signal(false);
  isVerifying = signal(false);
  otpSent = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  resendCooldown = signal(0);

  // Form
  otpForm = this.fb.nonNullable.group({
    digit1: ['', [Validators.required, Validators.pattern(/^[0-9]$/)]],
    digit2: ['', [Validators.required, Validators.pattern(/^[0-9]$/)]],
    digit3: ['', [Validators.required, Validators.pattern(/^[0-9]$/)]],
    digit4: ['', [Validators.required, Validators.pattern(/^[0-9]$/)]],
    digit5: ['', [Validators.required, Validators.pattern(/^[0-9]$/)]],
    digit6: ['', [Validators.required, Validators.pattern(/^[0-9]$/)]],
  });

  /**
   * Dialog megnyitása + OTP küldés
   */
  open(email: string) {
    this.userEmail.set(email);
    this.isOpen.set(true);
    this.otpSent.set(false);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.otpForm.reset();

    // OTP küldés automatikusan
    this.sendOTP();
  }

  /**
   * Dialog bezárása
   */
  close() {
    this.isOpen.set(false);
    this.cancelled.emit();
  }

  /**
   * OTP küldése email-re
   */
  sendOTP() {
    const email = this.userEmail();
    if (!email) return;

    this.isSending.set(true);
    this.errorMessage.set(null);

    this.otpService.sendOTP(email).subscribe({
      next: (res) => {
        console.log('✅ OTP elküldve:', res);
        this.isSending.set(false);
        this.otpSent.set(true);
        this.successMessage.set('Kód elküldve az email címedre!');

        // Cooldown timer (60 sec)
        this.startResendCooldown();

        // Success message eltűnik 5 sec után
        setTimeout(() => this.successMessage.set(null), 5000);
      },
      error: (err: HttpErrorResponse) => {
        console.error('❌ OTP küldési hiba:', err);
        this.isSending.set(false);
        this.errorMessage.set(err.error?.message || 'Hiba történt az OTP küldése során');
      },
    });
  }

  /**
   * OTP verifikáció
   */
  verifyOTP() {
    if (this.otpForm.invalid) {
      this.otpForm.markAllAsTouched();
      return;
    }

    const otp =
      this.otpForm.value.digit1! +
      this.otpForm.value.digit2! +
      this.otpForm.value.digit3! +
      this.otpForm.value.digit4! +
      this.otpForm.value.digit5! +
      this.otpForm.value.digit6!;

    const email = this.userEmail();

    this.isVerifying.set(true);
    this.errorMessage.set(null);

    console.log('🔐 OTP verifikáció:', { email, otp });

    this.otpService.verifyOTP(email, otp).subscribe({
      next: (res) => {
        console.log('✅ OTP sikeres:', res);
        this.isVerifying.set(false);

        if (res.verified) {
          this.successMessage.set('Email cím sikeresen megerősítve!');

          // 2 sec után bezárás + verified emit
          setTimeout(() => {
            this.isOpen.set(false);
            this.verified.emit();
          }, 2000);
        } else {
          this.errorMessage.set('Hibás vagy lejárt kód!');
        }
      },
      error: (err: HttpErrorResponse) => {
        console.error('❌ OTP verifikációs hiba:', err);
        this.isVerifying.set(false);
        this.errorMessage.set(err.error?.message || 'Hibás vagy lejárt kód');
      },
    });
  }

  /**
   * Újraküldés cooldown timer
   */
  private startResendCooldown() {
    this.resendCooldown.set(60);

    const interval = setInterval(() => {
      const current = this.resendCooldown();
      if (current > 0) {
        this.resendCooldown.set(current - 1);
      } else {
        clearInterval(interval);
      }
    }, 1000);
  }

  /**
   * Automatikus focus következő input-ra
   */
  onDigitInput(event: Event, nextIndex: number) {
    const input = event.target as HTMLInputElement;

    if (input.value.length === 1 && nextIndex <= 6) {
      const nextInput = document.getElementById(`digit${nextIndex}`) as HTMLInputElement;
      nextInput?.focus();
    }
  }

  /**
   * Backspace kezelése
   */
  onDigitKeyDown(event: KeyboardEvent, currentIndex: number) {
    if (event.key === 'Backspace') {
      const input = event.target as HTMLInputElement;

      if (input.value === '' && currentIndex > 1) {
        const prevInput = document.getElementById(`digit${currentIndex - 1}`) as HTMLInputElement;
        prevInput?.focus();
      }
    }
  }

  /**
   * Paste kezelése (teljes kód beillesztése)
   */
  onPaste(event: ClipboardEvent) {
    event.preventDefault();
    const pastedData = event.clipboardData?.getData('text');

    if (pastedData && /^\d{6}$/.test(pastedData)) {
      this.otpForm.patchValue({
        digit1: pastedData[0],
        digit2: pastedData[1],
        digit3: pastedData[2],
        digit4: pastedData[3],
        digit5: pastedData[4],
        digit6: pastedData[5],
      });
    }
  }
}
