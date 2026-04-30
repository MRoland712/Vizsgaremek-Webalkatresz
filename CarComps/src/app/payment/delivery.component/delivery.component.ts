import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MainHeaderComponent } from '../../main-header/main-header.component';
import { FooterComponent } from '../../footer.component/footer.component';
import { AuthService } from '../../services/auth.service';
import { PaymentForwardButtonComponent } from '../../shared/payment-forward-button.component/payment-forward-button.component';
import { CheckoutProgressComponent } from '../../shared/checkoutprogress.component/checkoutprogress.component';

@Component({
  selector: 'app-delivery',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MainHeaderComponent,
    FooterComponent,
    CheckoutProgressComponent,
    PaymentForwardButtonComponent,
  ],
  templateUrl: './delivery.component.html',
  styleUrl: './delivery.component.css',
})
export class DeliveryComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private auth = inject(AuthService);

  autoFillSuccess = signal(false);

  deliveryForm = this.fb.nonNullable.group({
    lastname: ['', [Validators.required, Validators.minLength(2)]],
    firstname: ['', [Validators.required, Validators.minLength(2)]],
    city: ['', [Validators.required]],
    postalCode: ['', [Validators.required, Validators.pattern(/^[0-9]{4}$/)]],
    country: ['', [Validators.required]],
    phone: ['', [Validators.required, Validators.pattern(/^[+0-9]{7,15}$/)]],
    address: ['', [Validators.required]],
    newsletter: [false],
  });

  autoFill() {
    let filled = false;
    const userId = this.auth.userId() || Number(localStorage.getItem('userId') || '0');

    if (userId) {
      const userAddr = localStorage.getItem(`shippingAddress_${userId}`);
      if (userAddr) {
        try {
          const addr = JSON.parse(userAddr);
          this.deliveryForm.patchValue({
            lastname: addr.lastname || '',
            firstname: addr.firstname || '',
            city: addr.city || '',
            postalCode: addr.postalCode || '',
            country: addr.country || 'Magyarország',
            phone: addr.phone || '',
            address: [addr.street, addr.houseNumber].filter(Boolean).join(' '),
          });
          filled = true;
        } catch {}
      }
    }

    if (!filled) {
      const savedAddress = localStorage.getItem('shippingAddress');
      if (savedAddress) {
        try {
          const addr = JSON.parse(savedAddress);
          if (!addr._userId || addr._userId === userId) {
            this.deliveryForm.patchValue({
              lastname: addr.lastname || '',
              firstname: addr.firstname || '',
              city: addr.city || '',
              postalCode: addr.postalCode || '',
              country: addr.country || 'Magyarország',
              phone: addr.phone || '',
              address: [addr.street, addr.houseNumber].filter(Boolean).join(' '),
            });
            filled = true;
          }
        } catch {}
      }
    }

    if (!filled) {
      const prev = localStorage.getItem('deliveryData');
      if (prev) {
        try {
          this.deliveryForm.patchValue(JSON.parse(prev));
          filled = true;
        } catch {}
      }
    }

    if (!filled) {
      const name = this.auth.userName();
      if (name) {
        const parts = name.trim().split(' ');
        this.deliveryForm.patchValue({
          firstname: parts[0] || '',
          lastname: parts.slice(1).join(' ') || '',
          country: 'Magyarország',
        });
        filled = true;
      }
    }

    if (filled) {
      this.autoFillSuccess.set(true);
      setTimeout(() => this.autoFillSuccess.set(false), 3000);
    }
  }

  goBack() {
    this.router.navigate(['/cart']);
  }

  onSubmit() {
    if (this.deliveryForm.invalid) {
      this.deliveryForm.markAllAsTouched();
      return;
    }
    const val = this.deliveryForm.getRawValue();
    localStorage.setItem('deliveryData', JSON.stringify(val));
    this.router.navigate(['/summary']); // ⭐ delivery → summary (nyugtázás)
  }
}
