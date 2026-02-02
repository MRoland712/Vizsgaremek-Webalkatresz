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

  open(email: string) {
    this.userEmail.set(email);
    this.isOpen.set(true);
    this.otpSent.set(false);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.otpForm.reset();
    this.sendOTP();
  }

  close() {
    this.isOpen.set(false);
    this.cancelled.emit();
  }

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
        this.startResendCooldown();
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
   * ⭐ JAVÍTOTT OTP verifikáció
   */
  verifyOTP() {
    if (this.otpForm.invalid) {
      this.otpForm.markAllAsTouched();
      return;
    }

    const FinalOTPData = {
      email: this.userEmail(),
      OTP: Number(
        this.otpForm.value.digit1! +
          this.otpForm.value.digit2! +
          this.otpForm.value.digit3! +
          this.otpForm.value.digit4! +
          this.otpForm.value.digit5! +
          this.otpForm.value.digit6!,
      ),
    };

    this.isVerifying.set(true);
    this.errorMessage.set(null);

    console.log('🔐 Verify OTP request:', FinalOTPData);

    this.otpService.verifyOTP(FinalOTPData).subscribe({
      next: (res) => {
        console.log('✅ OTP TELJES RESPONSE:', res);
        console.log('  Type:', typeof res);
        console.log('  Keys:', Object.keys(res));
        console.log('  res.verified:', res.verified);
        console.log('  res.success:', res.success);
        console.log('  res.status:', (res as any).status);

        this.isVerifying.set(false);

        // ⭐ JAVÍTOTT: Többféle success ellenőrzés
        const isSuccess =
          res.verified === true ||
          res.success === true ||
          (res as any).status === 'success' ||
          res.statusCode === 200;

        console.log('🎯 Is success?', isSuccess);

        if (isSuccess) {
          this.successMessage.set('Email cím sikeresen megerősítve!');

          // 2 sec után bezárás + verified emit
          setTimeout(() => {
            this.isOpen.set(false);
            this.verified.emit();
          }, 2000);
        } else {
          console.warn('⚠️ Response nem tartalmaz success flag-et!');
          console.warn('  Full response:', JSON.stringify(res, null, 2));
          this.errorMessage.set('Hibás vagy lejárt kód!');
        }
      },
      error: (err: HttpErrorResponse) => {
        console.error('❌ OTP verifikációs hiba:', err);
        console.error('  Status:', err.status);
        console.error('  Error body:', err.error);

        this.isVerifying.set(false);
        this.errorMessage.set(err.error?.message || 'Hibás vagy lejárt kód');
      },
    });
  }

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

  onDigitInput(event: Event, nextIndex: number) {
    const input = event.target as HTMLInputElement;
    if (input.value.length === 1 && nextIndex <= 6) {
      const nextInput = document.getElementById(`digit${nextIndex}`) as HTMLInputElement;
      nextInput?.focus();
    }
  }

  onDigitKeyDown(event: KeyboardEvent, currentIndex: number) {
    if (event.key === 'Backspace') {
      const input = event.target as HTMLInputElement;
      if (input.value === '' && currentIndex > 1) {
        const prevInput = document.getElementById(`digit${currentIndex - 1}`) as HTMLInputElement;
        prevInput?.focus();
      }
    }
  }

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
