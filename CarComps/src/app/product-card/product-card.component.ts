import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-product-card',
  imports: [],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.css',
})
export class ProductCardComponent {
  // Termék adatok
  productIMG = '/assets/CarComps_Logo_BigassC.png';
  productName = 'Brake disc Front STARK SKBD-0020354';
  articleNumber = 'SKBD-0020354';
  items: any[] = []; // Termék infók (ha kell)
  price = 11700;

  // Quantity signal - reactive state
  quantity = signal(0);

  /**
   * Mennyiség növelése
   */
  increaseQuantity(): void {
    this.quantity.update((current) => current + 1);
    console.log('📈 Quantity increased:', this.quantity());
  }

  /**
   * Mennyiség csökkentése (minimum 0)
   */
  decreaseQuantity(): void {
    this.quantity.update((current) => (current > 0 ? current - 1 : 0));
    console.log('📉 Quantity decreased:', this.quantity());
  }

  /**
   * Kosárba helyezés
   */
  addToCart(): void {
    const currentQty = this.quantity();

    if (currentQty === 0) {
      console.log('⚠️ Mennyiség 0, először válassz mennyiséget!');
      // Opcionális: Alert vagy notification
      return;
    }

    console.log('🛒 Hozzáadva a kosárhoz:');
    console.log('   Termék:', this.productName);
    console.log('   Cikkszám:', this.articleNumber);
    console.log('   Mennyiség:', currentQty);
    console.log('   Ár:', this.price * currentQty, 'HUF');

    // TODO: Itt hívd meg a cart service-t
    // this.cartService.addToCart({
    //   productName: this.productName,
    //   articleNumber: this.articleNumber,
    //   quantity: currentQty,
    //   price: this.price
    // });

    // Quantity reset után (opcionális)
    // this.quantity.set(0);
  }
}
