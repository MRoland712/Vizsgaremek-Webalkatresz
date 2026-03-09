import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MainHeaderComponent } from '../../main-header/main-header.component';
import { FooterComponent } from '../../footer.component/footer.component';
import { PaymentForwardButtonComponent } from '../../shared/payment-forward-button.component/payment-forward-button.component';
import { CheckoutProgressComponent } from '../../shared/checkoutprogress.component/checkoutprogress.component';
import { CartService } from '../../services/cart.service';

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
export class CartComponent implements OnInit {
  private router = inject(Router);
  private cartService = inject(CartService);

  readonly SHIPPING_FEE = 0;

  cartItems = this.cartService.cartItems;
  subtotal = computed(() => this.cartItems().reduce((s, i) => s + i.price * i.quantity, 0));
  total = computed(() => this.subtotal() + this.SHIPPING_FEE);

  ngOnInit(): void {
    // Kosár betöltése backendből
    this.cartService.loadCartFromBackend();
  }

  increaseQty(id: number) {
    this.cartService.increaseQty(id);
  }
  decreaseQty(id: number) {
    this.cartService.decreaseQty(id);
  }
  removeItem(id: number) {
    this.cartService.removeFromCart(id);
  }

  goBack() {
    this.router.navigate(['/']);
  }
  goToDelivery() {
    this.router.navigate(['/delivery']);
  }

  formatPrice(price: number): string {
    return price.toLocaleString('hu-HU') + ' Ft';
  }
}
