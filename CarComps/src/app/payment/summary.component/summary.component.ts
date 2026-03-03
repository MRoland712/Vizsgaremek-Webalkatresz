import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MainHeaderComponent } from '../../main-header/main-header.component';
import { FooterComponent } from '../../footer.component/footer.component';
import { AuthService } from '../../services/auth.service';
import { CheckoutProgressComponent } from '../../shared/checkoutprogress.component/checkoutprogress.component';

interface CartItem {
  id: number;
  name: string;
  brand: string;
  brandLogo?: string;
  imageUrl?: string;
  price: number;
  quantity: number;
}

interface DeliveryData {
  lastname: string;
  firstname: string;
  city: string;
  postalCode: string;
  country: string;
  phone: string;
  address: string;
}

interface PaymentData {
  method: string;
  amount: number;
  orderId: number;
}

@Component({
  selector: 'app-summary',
  standalone: true,
  imports: [CommonModule, MainHeaderComponent, FooterComponent, CheckoutProgressComponent],
  templateUrl: './summary.component.html',
  styleUrl: './summary.component.css',
})
export class SummaryComponent implements OnInit {
  private router = inject(Router);
  private auth = inject(AuthService);

  readonly SHIPPING_FEE = 0; // Ingyenes szállítás

  cartItems = signal<CartItem[]>([]);
  delivery = signal<DeliveryData | null>(null);
  paymentData = signal<PaymentData | null>(null);
  paymentMethod = signal<string>('Készpénz');

  subtotal = computed(() => this.cartItems().reduce((s, i) => s + i.price * i.quantity, 0));
  total = computed(() => this.subtotal() + this.SHIPPING_FEE);

  ngOnInit() {
    // Kosár adatok localStorage-ból (pay.component mentette fizetés előtt)
    try {
      const cart = localStorage.getItem('cartItems');
      if (cart) {
        this.cartItems.set(JSON.parse(cart));
        // Olvasás után töröljük - következő vásárlásnál friss kosár legyen
        localStorage.removeItem('cartItems');
      }
    } catch {}

    // Szállítási adatok
    try {
      const del = localStorage.getItem('deliveryData');
      if (del) this.delivery.set(JSON.parse(del));
    } catch {}

    // Fizetési adatok (pay.component mentette)
    try {
      const pay = localStorage.getItem('paymentData');
      if (pay) {
        const pd: PaymentData = JSON.parse(pay);
        this.paymentData.set(pd);
        const methodMap: Record<string, string> = {
          cash: 'Készpénz',
          paypal: 'PayPal',
          mastercard: 'Mastercard',
          visa: 'VISA',
        };
        this.paymentMethod.set(methodMap[pd.method] ?? pd.method);
      }
    } catch {}
  }

  get fullName(): string {
    const d = this.delivery();
    return d ? `${d.firstname} ${d.lastname}` : this.auth.userName();
  }

  get fullAddress(): string {
    const d = this.delivery();
    return d ? `${d.postalCode}, ${d.city}, ${d.address}` : '';
  }

  formatPrice(price: number): string {
    return price.toLocaleString('hu-HU') + ' Ft';
  }

  editDelivery() {
    this.router.navigate(['/delivery']);
  }
  editPayment() {
    this.router.navigate(['/payment']);
  }

  downloadInvoice() {
    const orderId = this.paymentData()?.orderId ?? '-';
    const content = `CARCOMPS SZÁMLA
===================
Rendelésszám: #${orderId}
Vevő: ${this.fullName}
Cím: ${this.fullAddress}
Fizetési mód: ${this.paymentMethod()}

TÉTELEK:
${this.cartItems()
  .map((i) => `- ${i.name} x${i.quantity}: ${this.formatPrice(i.price * i.quantity)}`)
  .join('\n')}

Összesen: ${this.formatPrice(this.subtotal())}
Szállítási díj: ${this.formatPrice(this.SHIPPING_FEE)}
Végösszeg: ${this.formatPrice(this.total())}

Az ár tartalmazza az ÁFÁ-t.
`;
    const blob = new Blob([content], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `carcomps-szamla-${orderId}.txt`;
    a.click();
    URL.revokeObjectURL(url);
  }
}
