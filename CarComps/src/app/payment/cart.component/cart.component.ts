import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MainHeaderComponent } from '../../main-header/main-header.component';
import { FooterComponent } from '../../footer.component/footer.component';
import { PaymentForwardButtonComponent } from '../../shared/payment-forward-button.component/payment-forward-button.component';
import { CheckoutProgressComponent } from '../../shared/checkoutprogress.component/checkoutprogress.component';

export interface CartItem {
  id: number;
  name: string;
  brand: string;
  brandLogo?: string;
  imageUrl?: string;
  price: number;
  quantity: number;
}

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [
    CommonModule,
    MainHeaderComponent,
    FooterComponent,
    PaymentForwardButtonComponent,
    CheckoutProgressComponent,
  ],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css',
})
export class CartComponent {
  private router = inject(Router);

  readonly SHIPPING_FEE = 2500;

  // ── Kosár tételek (service/localStorage-ból jön majd) ─────
  cartItems = signal<CartItem[]>([
    { id: 1, name: 'RIDEX Coil spring', brand: 'RIDEX', price: 13000, quantity: 1 },
    { id: 2, name: 'RIDEX Lambda sensor', brand: 'RIDEX', price: 8000, quantity: 1 },
    { id: 3, name: 'RIDEX Starter motor', brand: 'RIDEX', price: 23000, quantity: 1 },
  ]);

  // ── Computed értékek ──────────────────────────────────────
  subtotal = computed(() =>
    this.cartItems().reduce((sum, item) => sum + item.price * item.quantity, 0),
  );

  total = computed(() => this.subtotal() + this.SHIPPING_FEE);

  // ── Mennyiség változtatás ─────────────────────────────────
  increaseQty(id: number) {
    this.cartItems.update((items) =>
      items.map((i) => (i.id === id ? { ...i, quantity: i.quantity + 1 } : i)),
    );
  }

  decreaseQty(id: number) {
    this.cartItems.update((items) =>
      items.map((i) => (i.id === id && i.quantity > 1 ? { ...i, quantity: i.quantity - 1 } : i)),
    );
  }

  // ── Törlés ────────────────────────────────────────────────
  removeItem(id: number) {
    this.cartItems.update((items) => items.filter((i) => i.id !== id));
  }

  // ── Vissza a vásárláshoz ──────────────────────────────────
  goBack() {
    this.router.navigate(['/']);
  }

  // ── Ár formázás ───────────────────────────────────────────
  formatPrice(price: number): string {
    return price.toLocaleString('hu-HU') + ' Ft';
  }
}
