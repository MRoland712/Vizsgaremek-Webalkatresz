import { Injectable, signal, computed } from '@angular/core';

export interface CartItem {
  id: number;
  name: string;
  price: number;
  quantity: number;
  imageUrl?: string;
  sku?: string;
}

@Injectable({
  providedIn: 'root',
})
export class CartService {
  // ⭐ Egyetlen forrás - minden komponens ebből olvas
  private _cartItems = signal<CartItem[]>([]);

  // Publikus readonly hozzáférés
  cartItems = this._cartItems.asReadonly();

  // Computed signals
  cartItemCount = computed(() => this._cartItems().reduce((sum, item) => sum + item.quantity, 0));

  cartTotal = computed(() =>
    this._cartItems().reduce((sum, item) => sum + item.price * item.quantity, 0),
  );

  /**
   * Termék hozzáadása kosárhoz
   * Ha már bent van, növeli a mennyiséget
   */
  addToCart(item: CartItem): void {
    const current = this._cartItems();
    const existingIndex = current.findIndex((i) => i.id === item.id);

    if (existingIndex >= 0) {
      // Már bent van - növeli a mennyiséget
      const updated = [...current];
      updated[existingIndex] = {
        ...updated[existingIndex],
        quantity: updated[existingIndex].quantity + item.quantity,
      };
      this._cartItems.set(updated);
    } else {
      // Új termék
      this._cartItems.update((items) => [...items, item]);
    }

    console.log('🛒 Kosárba adva:', item.name, '| Darab:', item.quantity);
    console.log('🛒 Kosár összesen:', this.cartItemCount(), 'db');
  }

  /**
   * Termék eltávolítása
   */
  removeFromCart(itemId: number): void {
    this._cartItems.update((items) => items.filter((i) => i.id !== itemId));
  }

  /**
   * Mennyiség módosítása
   */
  updateQuantity(itemId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(itemId);
      return;
    }
    this._cartItems.update((items) => items.map((i) => (i.id === itemId ? { ...i, quantity } : i)));
  }

  /**
   * Kosár ürítése
   */
  clearCart(): void {
    this._cartItems.set([]);
  }
}
