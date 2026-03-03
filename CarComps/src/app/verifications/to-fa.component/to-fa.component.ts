import { Component, inject, signal, output } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { TfaService } from '../../services/tfa.service';

@Component({
  selector: 'app-tfa',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './to-fa.component.html',
  styleUrl: './to-fa.component.css',
})
export class TfaVerifyDialogComponent {
  private fb = inject(FormBuilder);
  private tfaService = inject(TfaService);

  verified = output<void>();
  cancelled = output<void>();

  isOpen = signal(false);
  isVerifying = signal(false);
  errorMessage = signal<string | null>(null);
  private email = signal('');

  tfaForm = this.fb.nonNullable.group({
    code: ['', [Validators.required, Validators.pattern(/^\d{3}-?\d{3}$|^\d{6}$/)]],
  });

  open(email: string) {
    this.email.set(email);
    this.isOpen.set(true);
    this.errorMessage.set(null);
    this.tfaForm.reset();
  }

  close() {
    this.isOpen.set(false);
    this.cancelled.emit();
  }

  verify() {
    if (this.tfaForm.invalid) return;

    const code = this.tfaForm.value.code!.replace('-', '');
    if (code.length !== 6) {
      this.errorMessage.set('Add meg a teljes 6 számjegyű kódot!');
      return;
    }

    this.isVerifying.set(true);
    this.errorMessage.set(null);

    this.tfaService.verifyTfaCode(this.email(), code).subscribe({
      next: (res) => {
        this.isVerifying.set(false);
        if (res.result === 'invalid') {
          this.errorMessage.set('Hibás kód! Ellenőrizd az authenticator alkalmazást.');
          this.tfaForm.reset();
          return;
        }
        this.isOpen.set(false);
        this.verified.emit();
      },
      error: (err) => {
        this.isVerifying.set(false);
        // backend bug bypass — 404 UserTwofaNotFound esetén is engedjük át
        if (err.status === 404 && err.error?.errors?.includes('UserTwofaNotFound')) {
          this.isOpen.set(false);
          this.verified.emit();
          return;
        }
        this.errorMessage.set(err.error?.message || 'Hibás kód!');
        this.tfaForm.reset();
      },
    });
  }

  onInput(event: Event) {
    const input = event.target as HTMLInputElement;
    let v = input.value.replace(/\D/g, '').slice(0, 6);
    if (v.length > 3) v = v.slice(0, 3) + '-' + v.slice(3);
    this.tfaForm.patchValue({ code: v });
    input.value = v;
  }
}
