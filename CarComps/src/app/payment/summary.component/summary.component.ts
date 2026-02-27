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

  readonly SHIPPING_FEE = 2500;

  cartItems = signal<CartItem[]>([]);
  delivery = signal<DeliveryData | null>(null);
  paymentMethod = signal<string>('Készpénz');

  subtotal = computed(() => this.cartItems().reduce((sum, i) => sum + i.price * i.quantity, 0));
  total = computed(() => this.subtotal() + this.SHIPPING_FEE);

  ngOnInit() {
    // Kosár adatok
    const cart = localStorage.getItem('cartItems');
    if (cart) {
      try {
        this.cartItems.set(JSON.parse(cart));
      } catch {}
    } else {
      // Mock ha nincs localStorage
      this.cartItems.set([
        { id: 1, name: 'RIDEX Coil spring', brand: 'RIDEX', price: 13000, quantity: 1 },
        { id: 2, name: 'RIDEX Lambda sensor', brand: 'RIDEX', price: 8000, quantity: 1 },
        { id: 3, name: 'RIDEX Starter motor', brand: 'RIDEX', price: 23000, quantity: 1 },
      ]);
    }

    // Szállítási adatok
    const del = localStorage.getItem('deliveryData');
    if (del) {
      try {
        this.delivery.set(JSON.parse(del));
      } catch {}
    } else {
      this.delivery.set({
        lastname: 'Doe',
        firstname: 'John',
        city: 'Pécs',
        postalCode: '7633',
        country: 'Magyarország',
        phone: '0630123456',
        address: 'Király utca 4.',
      });
    }
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
    // PDF generálás placeholder — majd backend integráció
    const content = `CARCOMPS SZÁMLA
===================
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
    a.download = `carcomps-szamla-${Date.now()}.txt`;
    a.click();
    URL.revokeObjectURL(url);
  }
}
