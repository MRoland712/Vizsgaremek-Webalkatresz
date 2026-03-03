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

  userEmail = signal<string>('');

  verified = output<void>();
  cancelled = output<void>();

  isOpen = signal(false);
  isSending = signal(false);
  isVerifying = signal(false);
  otpSent = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  resendCooldown = signal(0);

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
        this.isSending.set(false);
        this.otpSent.set(true);
        this.successMessage.set('Kód elküldve az email címedre!');
        this.startResendCooldown();
        setTimeout(() => this.successMessage.set(null), 5000);
      },
      error: (err: HttpErrorResponse) => {
        console.error('❌ OTP küldési hiba:', err);
        this.isSending.set(false);
        this.errorMessage.set('Hiba történt az OTP küldése során');
      },
    });
  }

  verifyOTP() {
    if (this.otpForm.invalid) {
      this.otpForm.markAllAsTouched();
      return;
    }

    const finalOTPData = {
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

    this.otpService.verifyOTP(finalOTPData).subscribe({
      next: (res) => {
        this.isVerifying.set(false);

        const isSuccess = res.result != 'invalid';

        if (isSuccess) {
          this.successMessage.set('Email cím sikeresen megerősítve!');
          this.otpForm.disable();
          setTimeout(() => {
            this.isOpen.set(false);
            this.verified.emit();
          }, 2000);
        } else {
          this.errorMessage.set('Hibás vagy lejárt kód!');

          this.otpForm.reset();
          setTimeout(() => {
            const first = document.getElementById('digit1') as HTMLInputElement;
            first?.focus();
          }, 50);
        }
      },
      error: (err: HttpErrorResponse) => {
        this.isVerifying.set(false);

        if (err.status === 400 || err.status === 401) {
          this.errorMessage.set('Hibás vagy lejárt kód!');
        } else {
          this.errorMessage.set('Szerverhiba, kérjük próbálja újra.');
        }

        this.otpForm.reset();
        setTimeout(() => {
          const first = document.getElementById('digit1') as HTMLInputElement;
          first?.focus();
        }, 50);
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
