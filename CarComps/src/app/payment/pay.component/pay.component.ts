import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MainHeaderComponent } from '../../main-header/main-header.component';
import { FooterComponent } from '../../footer.component/footer.component';
import { PaymentForwardButtonComponent } from '../../shared/payment-forward-button.component/payment-forward-button.component';
import { CheckoutProgressComponent } from '../../shared/checkoutprogress.component/checkoutprogress.component';

import { PaymentService } from '../../services/payment.service';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { OrderService } from '../../services/orders.service';

interface PaymentMethod {
  id: string;
  label: string;
  icon: string;
  imgSrc?: string;
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
export class PayComponent implements OnInit {
  private router = inject(Router);
  private orderSvc = inject(OrderService);
  private paymentSvc = inject(PaymentService);
  private auth = inject(AuthService);
  readonly cartService = inject(CartService);

  selectedMethod = signal<string>('cash_on_delivery');

  ngOnInit(): void {
    if (this.cartService.cartItems().length === 0) {
      this.cartService.loadCartFromBackend();
    }
  }
  isProcessing = signal(false);
  payError = signal<string | null>(null);

  paymentMethods: PaymentMethod[] = [
    { id: 'cash_on_delivery', label: 'Készpénz', icon: 'fa-money-bill', available: true },
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
    if (!this.selectedMethod() || this.isProcessing()) return;

    // Ha a kosár még töltődik (pl. page refresh után), várunk
    if (this.cartService.isLoading()) {
      this.payError.set('A kosár még betöltődik, kérlek várj egy pillanatot...');
      setTimeout(() => this.payError.set(null), 3000);
      return;
    }

    // Ha a kosár üres, nem folytathatjuk
    if (this.cartService.cartItems().length === 0) {
      this.payError.set('A kosár üres! Adj hozzá termékeket vásárlás előtt.');
      return;
    }

    const userId = this.auth.userId() || Number(localStorage.getItem('userId') || '0');
    if (!userId) {
      this.payError.set('Nincs bejelentkezve!');
      return;
    }

    this.isProcessing.set(true);
    this.payError.set(null);

    // LÉPÉS 1: createOrderFromCart
    this.orderSvc.createOrderFromCart({ userId }).subscribe({
      next: (orderRes) => {
        if (!orderRes.success) {
          this.payError.set('Rendelés létrehozása sikertelen!');
          this.isProcessing.set(false);
          return;
        }

        const orderId = orderRes.orderId;
        localStorage.setItem('orderId', String(orderId));

        // LÉPÉS 2: processPayment
        this.paymentSvc
          .processPayment({
            orderId,
            method: this.selectedMethod(),
          })
          .subscribe({
            next: (payRes) => {
              this.isProcessing.set(false);
              if (payRes.success) {
                // ⭐ cartItems mentése ELŐSZÖR mielőtt clearCart törli
                localStorage.setItem('cartItems', JSON.stringify(this.cartService.cartItems()));

                // Fizetési adatok mentése
                localStorage.setItem(
                  'paymentData',
                  JSON.stringify({
                    method: this.selectedMethod(),
                    amount: payRes.amount,
                    orderId: payRes.orderId,
                  }),
                );

                // Kosár ürítése (csak memory, localStorage cartItems megmarad summary-nak)
                this.cartService.clearCart();
                this.router.navigate(['/summary']);
              } else {
                this.payError.set('Fizetés sikertelen!');
              }
            },
            error: (err) => {
              this.isProcessing.set(false);
              this.payError.set(err.error?.message || 'Fizetési hiba történt!');
              console.error('❌ processPayment hiba:', err);
            },
          });
      },
      error: (err) => {
        this.isProcessing.set(false);
        this.payError.set(err.error?.message || 'Rendelési hiba történt!');
        console.error('❌ createOrderFromCart hiba:', err);
      },
    });
  }
}
