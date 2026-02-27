import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { inject } from '@angular/core';
import { MainHeaderComponent } from '../../main-header/main-header.component';
import { FooterComponent } from '../../footer.component/footer.component';
import { PaymentForwardButtonComponent } from '../../shared/payment-forward-button.component/payment-forward-button.component';
import { CheckoutProgressComponent } from '../../shared/checkoutprogress.component/checkoutprogress.component';

interface PaymentMethod {
  id: string;
  label: string;
  icon: string; // FontAwesome class VAGY 'img'
  imgSrc?: string; // ha kép
  available: boolean;
}

@Component({
  selector: 'app-pay',
  standalone: true,
  imports: [
    CommonModule,
    MainHeaderComponent,
    FooterComponent,
    CheckoutProgressComponent,
    PaymentForwardButtonComponent,
  ],
  templateUrl: './pay.component.html',
  styleUrl: './pay.component.css',
})
export class PayComponent {
  private router = inject(Router);

  selectedMethod = signal<string>('cash');

  paymentMethods: PaymentMethod[] = [
    { id: 'cash', label: 'Készpénz', icon: 'fa-money-bill', available: true },
    {
      id: 'mastercard',
      label: 'Mastercard',
      icon: 'img',
      imgSrc: 'assets/mastercard.png',
      available: false,
    },
    { id: 'visa', label: 'VISA', icon: 'img', imgSrc: 'assets/visa.png', available: false },
    { id: 'paypal', label: 'PayPal', icon: 'img', imgSrc: 'assets/paypal.png', available: false },
  ];

  select(method: PaymentMethod) {
    if (method.available) this.selectedMethod.set(method.id);
  }

  goBack() {
    this.router.navigate(['/delivery']);
  }

  finalize() {
    if (!this.selectedMethod()) return;
    this.router.navigate(['/summary']);
  }
}
