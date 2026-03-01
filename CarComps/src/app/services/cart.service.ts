import { inject, Injectable, signal, computed } from '@angular/core';
import { forkJoin } from 'rxjs';

import { GetallpartimgagesService } from './getallpartimages.service';
import { GetallmanufacturersService } from './getallmanufacturers.service';
import { GetallpartsService } from './getallparts.service';
import { AuthService } from './auth.service';
import { CartItemsService } from './cartitem.service';

export interface CartItem {
  id: number; // cartItem.id (backend)
  partId: number; // part azonosító
  name: string;
  brand?: string;
  brandLogo?: string;
  imageUrl?: string;
  price: number;
  quantity: number;
  sku?: string;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private cartItemsSvc = inject(CartItemsService);
  private auth = inject(AuthService);
  private partImagesSvc = inject(GetallpartimgagesService);
  private manufacturersSvc = inject(GetallmanufacturersService);
  private partsSvc = inject(GetallpartsService);

  private _cartItems = signal<CartItem[]>([]);
  private _isLoading = signal(false);

  readonly isLoading = this._isLoading.asReadonly();

  cartItems = this._cartItems.asReadonly();
  cartItemCount = computed(() => this._cartItems().reduce((s, i) => s + i.quantity, 0));
  cartTotal = computed(() => this._cartItems().reduce((s, i) => s + i.price * i.quantity, 0));

  // ── Backend-ből töltjük be a kosarat ─────────────────────
  // A backend adja: partName, partPrice — képekhez külön image call
  loadCartFromBackend(): void {
    const userId = this.auth.userId() || Number(localStorage.getItem('userId') || '0');
    if (!userId) return;
    this._isLoading.set(true);

    forkJoin({
      cart: this.cartItemsSvc.getCartItemsByUserId(userId),
      images: this.partImagesSvc.getAllPartImages(),
      manufacturers: this.manufacturersSvc.getAllManufacturers(),
      parts: this.partsSvc.getAllParts(),
    }).subscribe({
      next: ({ cart, images, manufacturers, parts }) => {
        if (!cart.success || !cart.cartItems) return;

        // Kép map: partId → url
        const imageMap = new Map<number, string>();
        images.partImages.forEach((img) => {
          if (img.isPrimary && !imageMap.has(img.partId)) imageMap.set(img.partId, img.url);
        });
        images.partImages.forEach((img) => {
          if (!imageMap.has(img.partId)) imageMap.set(img.partId, img.url);
        });

        // Parts map: partId → manufacturerId
        const partsMap = new Map(parts.parts.map((p) => [p.id, p.manufacturerId]));

        // manufacturerId → márkanév
        const brandMap = new Map<number, string>(
          manufacturers.Manufacturers.map((m) => [m.id, m.name]),
        );

        const items: CartItem[] = cart.cartItems
          .filter((i) => !i.isDeleted)
          .map((i) => {
            const manufacturerId = partsMap.get(i.partId) ?? 0;
            return {
              id: i.id,
              partId: i.partId,
              name: i.partName ?? `Alkatrész #${i.partId}`,
              brand: brandMap.get(manufacturerId) ?? '',
              imageUrl: imageMap.get(i.partId) ?? '',
              price: i.partPrice ?? 0,
              quantity: i.quantity,
            };
          });

        this._cartItems.set(items);
        localStorage.setItem('cartItems', JSON.stringify(items));
        this._isLoading.set(false);
        console.log('✅ Kosár betöltve:', items.length, 'termék');
      },
      error: (err) => {
        this._isLoading.set(false);
        if (err.status === 404) {
          this._cartItems.set([]);
          console.log('ℹ️ Kosár üres (404 CartItemsNotFound)');
        } else {
          console.error('❌ Kosár betöltési hiba:', err);
        }
      },
    });
  }

  // ── Kosárba adás: POST + helyi frissítés ─────────────────
  addToCart(product: {
    id: number; // partId
    name: string;
    price: number;
    quantity: number;
    imageUrl?: string;
    brand?: string;
    sku?: string;
  }): void {
    const userId = this.auth.userId() || Number(localStorage.getItem('userId') || '0');

    // Helyi frissítés azonnal (UX)
    const existing = this._cartItems().find((i) => i.partId === product.id);
    if (existing) {
      this._cartItems.update((items) =>
        items.map((i) =>
          i.partId === product.id ? { ...i, quantity: i.quantity + product.quantity } : i,
        ),
      );
    } else {
      // Ideiglenes id=-1 amíg backend válasz nem jön
      this._cartItems.update((items) => [
        ...items,
        {
          id: -1,
          partId: product.id,
          name: product.name,
          price: product.price,
          quantity: product.quantity,
          imageUrl: product.imageUrl,
          brand: product.brand,
          sku: product.sku,
        },
      ]);
    }
    // Mindig frissítjük a localStorage-t az aktuális helyi state-tel
    localStorage.setItem('cartItems', JSON.stringify(this._cartItems()));

    if (!userId) {
      console.warn('⚠️ Nincs userId → kosár csak helyben él');
      return;
    }

    // Backend hívás — mindig POST, a backend quantity-t növel ha már létezik
    // Ha az itemnek már van valódi id-je → PUT update, különben POST create
    const currentItem = this._cartItems().find((i) => i.partId === product.id);
    const backendId = currentItem?.id ?? -1;

    if (backendId > 0) {
      // PUT CORS tiltva → DELETE + POST
      const newQty = currentItem!.quantity;
      this.cartItemsSvc.deleteCartItem(backendId).subscribe({
        next: () => {
          this.cartItemsSvc
            .createCartItem({ userId, partId: product.id, quantity: newQty })
            .subscribe({
              next: () => this.refreshCartIds(userId),
              error: (e) => console.error('❌ addToCart create hiba:', e),
            });
        },
        error: (e) => console.error('❌ addToCart delete hiba:', e),
      });
    } else {
      // Még nincs backend id (új termék vagy folyamatban lévő create) → POST
      this.cartItemsSvc
        .createCartItem({
          userId,
          partId: product.id,
          quantity: product.quantity,
        })
        .subscribe({
          next: () => this.refreshCartIds(userId),
          error: (err) => console.error('❌ createCartItem hiba:', err),
        });
    }
  }

  // ── Mennyiség növelés ─────────────────────────────────────
  increaseQty(cartItemId: number): void {
    const item = this._cartItems().find((i) => i.id === cartItemId);
    if (!item) return;
    const newQty = item.quantity + 1;

    this._cartItems.update((items) =>
      items.map((i) => (i.id === cartItemId ? { ...i, quantity: newQty } : i)),
    );
    this.syncUpdate(item, newQty);
  }

  // ── Mennyiség csökkentés ──────────────────────────────────
  decreaseQty(cartItemId: number): void {
    const item = this._cartItems().find((i) => i.id === cartItemId);
    if (!item || item.quantity <= 1) return;
    const newQty = item.quantity - 1;

    this._cartItems.update((items) =>
      items.map((i) => (i.id === cartItemId ? { ...i, quantity: newQty } : i)),
    );
    this.syncUpdate(item, newQty);
  }

  // ── Törlés: soft delete ───────────────────────────────────
  removeFromCart(cartItemId: number): void {
    this._cartItems.update((items) => items.filter((i) => i.id !== cartItemId));

    this.cartItemsSvc.deleteCartItem(cartItemId).subscribe({
      error: (err) => console.error('❌ deleteCartItem hiba:', err),
    });
  }

  // ── Kosár ürítése (logout) ────────────────────────────────
  clearCart(): void {
    this._cartItems.set([]);
    // ⭐ localStorage cartItems-t NEM töröljük - summary oldal még szüksége van rá
    // A summary oldal olvasás után maga törli
  }

  // ⭐ Csak a backend id-ket frissíti, helyi state megmarad
  private refreshCartIds(userId: number): void {
    this.cartItemsSvc.getCartItemsByUserId(userId).subscribe({
      next: (res) => {
        if (!res.success || !res.cartItems) return;
        // Csak az id-1-es (ideiglenes) itemeket frissítjük valódi id-re
        this._cartItems.update((items) =>
          items.map((localItem) => {
            if (localItem.id > 0) return localItem; // már van valódi id-je
            // Megkeressük a backendben partId alapján
            const backendItem = res.cartItems.find(
              (b) => b.partId === localItem.partId && !b.isDeleted,
            );
            return backendItem ? { ...localItem, id: backendItem.id } : localItem;
          }),
        );
        // Mentés localStorage-ba
        localStorage.setItem('cartItems', JSON.stringify(this._cartItems()));
      },
      error: (err) => console.error('❌ refreshCartIds hiba:', err),
    });
  }

  private syncUpdate(item: CartItem, newQty: number): void {
    const userId = this.auth.userId() || Number(localStorage.getItem('userId') || '0');
    if (!userId || item.id <= 0) return;

    // PUT CORS tiltva backend oldalon → mindig DELETE + POST
    this.cartItemsSvc.deleteCartItem(item.id).subscribe({
      next: () => {
        this.cartItemsSvc
          .createCartItem({ userId, partId: item.partId, quantity: newQty })
          .subscribe({
            next: () => this.refreshCartIds(userId),
            error: (e) => console.error('❌ syncUpdate create hiba:', e),
          });
      },
      error: (e) => console.error('❌ syncUpdate delete hiba:', e),
    });
  }
}
