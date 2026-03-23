import { inject, Injectable, signal, computed } from '@angular/core';
import { forkJoin } from 'rxjs';

import { GetAllPartsWithImagesService } from './getallpartswithimages.service';
import { GetallmanufacturersService } from './getallmanufacturers.service';
import { AuthService } from './auth.service';
import { CartItemsService } from './cartitem.service';

export interface CartItem {
  id: number;
  partId: number;
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
  private partsSvc = inject(GetAllPartsWithImagesService);
  private manufacturersSvc = inject(GetallmanufacturersService);

  private _cartItems = signal<CartItem[]>([]);
  private _isLoading = signal(false);

  readonly isLoading = this._isLoading.asReadonly();
  cartItems = this._cartItems.asReadonly();
  cartItemCount = computed(() => this._cartItems().reduce((s, i) => s + i.quantity, 0));
  cartTotal = computed(() => this._cartItems().reduce((s, i) => s + i.price * i.quantity, 0));

  // ── Backend-ből töltjük be a kosarat ─────────────────────
  loadCartFromBackend(): void {
    const userId = this.auth.userId() || Number(localStorage.getItem('userId') || '0');
    if (!userId) return;
    this._isLoading.set(true);

    forkJoin({
      cart: this.cartItemsSvc.getCartItemsByUserId(userId),
      parts: this.partsSvc.getAllPartsWithImages(),
      manufacturers: this.manufacturersSvc.getAllManufacturers(),
    }).subscribe({
      next: ({ cart, parts, manufacturers }) => {
        if (!cart.success || !cart.cartItems) {
          this._isLoading.set(false);
          return;
        }

        // imageUrl közvetlenül a parts response-ban van
        const imageMap = new Map<number, string>(parts.parts.map((p) => [p.id, p.imageUrl]));

        // partId → manufacturerId
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

  // ── Kosárba adás ──────────────────────────────────────────
  addToCart(product: {
    id: number;
    name: string;
    price: number;
    quantity: number;
    imageUrl?: string;
    brand?: string;
    sku?: string;
  }): void {
    const userId = this.auth.userId() || Number(localStorage.getItem('userId') || '0');

    const existing = this._cartItems().find((i) => i.partId === product.id);

    if (existing) {
      // Már van ilyen termék → helyi növelés
      const newQty = existing.quantity + product.quantity;
      this._cartItems.update((items) =>
        items.map((i) => (i.partId === product.id ? { ...i, quantity: newQty } : i)),
      );
      localStorage.setItem('cartItems', JSON.stringify(this._cartItems()));

      if (!userId) return;

      // Ha még nincs valódi backend id (folyamatban lévő POST) → ne csinálj semmit
      // A refreshCartIds majd beállítja az id-t
      if (existing.id <= 0) return;

      // PUT CORS tiltva → soft-delete + create új a helyes qty-vel
      this.cartItemsSvc.deleteCartItem(existing.id).subscribe({
        next: () => {
          this.cartItemsSvc
            .createCartItem({ userId, partId: product.id, quantity: newQty })
            .subscribe({
              next: () => this.refreshCartIds(userId),
              error: (e) => console.error('❌ addToCart re-create hiba:', e),
            });
        },
        error: (err) => {
          if (err.status === 409) {
            // Már soft-deleted → csak POST az új qty-vel
            this._cartItems.update((items) => items.filter((i) => i.partId !== product.id));
            this.cartItemsSvc
              .createCartItem({ userId, partId: product.id, quantity: product.quantity })
              .subscribe({
                next: () => this.refreshCartIds(userId),
                error: (e) => console.error('❌ addToCart create (after 409) hiba:', e),
              });
          } else {
            console.error('❌ addToCart delete hiba:', err);
          }
        },
      });
    } else {
      // Új termék → azonnal helyi state, majd POST
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
      localStorage.setItem('cartItems', JSON.stringify(this._cartItems()));

      if (!userId) return;

      this.cartItemsSvc
        .createCartItem({ userId, partId: product.id, quantity: product.quantity })
        .subscribe({
          next: () => this.refreshCartIds(userId),
          error: (err) => {
            // 409 → a termék már soft-deleted rekordként létezik a backenden
            // refreshCartIds-szel szinkronizáljuk
            if (err.status === 409) {
              console.warn('⚠️ createCartItem 409 → refreshCartIds');
              this.refreshCartIds(userId);
            } else {
              console.error('❌ createCartItem hiba:', err);
            }
          },
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
    localStorage.setItem('cartItems', JSON.stringify(this._cartItems()));
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
    localStorage.setItem('cartItems', JSON.stringify(this._cartItems()));
    this.syncUpdate(item, newQty);
  }

  // ── Törlés ────────────────────────────────────────────────
  removeFromCart(cartItemId: number): void {
    const item = this._cartItems().find((i) => i.id === cartItemId);

    // Helyi törlés azonnal
    this._cartItems.update((items) => items.filter((i) => i.id !== cartItemId));
    localStorage.setItem('cartItems', JSON.stringify(this._cartItems()));

    if (!item || item.id <= 0) return;

    this.cartItemsSvc.deleteCartItem(cartItemId).subscribe({
      next: () => console.log('✅ Törölve backend:', cartItemId),
      error: (err) => {
        // 409 CartItemIsSoftDeleted → már törölve van, nem baj
        if (err.status === 409) {
          console.warn('⚠️ Item már soft-deleted volt:', cartItemId);
        } else {
          console.error('❌ deleteCartItem hiba:', err);
        }
      },
    });
  }

  // ── Kosár ürítése ─────────────────────────────────────────
  clearCart(): void {
    this._cartItems.set([]);
    // localStorage cartItems-t NEM töröljük — summary oldal olvassa
  }

  // ── Csak az id=-1 itemeket frissíti valódi backend id-re ─
  private refreshCartIds(userId: number): void {
    this.cartItemsSvc.getCartItemsByUserId(userId).subscribe({
      next: (res) => {
        if (!res.success || !res.cartItems) return;

        // Csak az aktív (nem soft-deleted) backend itemek
        const activeBackendItems = res.cartItems.filter((b) => !b.isDeleted);

        this._cartItems.update((items) =>
          items
            .map((localItem) => {
              if (localItem.id > 0) {
                // Ellenőrizzük hogy a backend még ismeri-e ezt az id-t aktívként
                const stillActive = activeBackendItems.find((b) => b.id === localItem.id);
                if (!stillActive) {
                  // A backend már törölte → frissítsük partId alapján
                  const byPartId = activeBackendItems.find((b) => b.partId === localItem.partId);
                  return byPartId ? { ...localItem, id: byPartId.id } : localItem;
                }
                return localItem;
              }
              // id=-1 → keressük partId alapján
              const backendItem = activeBackendItems.find((b) => b.partId === localItem.partId);
              return backendItem ? { ...localItem, id: backendItem.id } : localItem;
            })
            // Kiszűrjük azokat amelyeknek már nincs aktív backend párjuk
            .filter((localItem) => {
              if (localItem.id <= 0) return true; // még folyamatban
              return activeBackendItems.some((b) => b.id === localItem.id);
            }),
        );
        localStorage.setItem('cartItems', JSON.stringify(this._cartItems()));
      },
      error: (err) => {
        if (err.status === 404) {
          // Üres kosár
          this._cartItems.set([]);
          localStorage.setItem('cartItems', JSON.stringify([]));
        } else {
          console.error('❌ refreshCartIds hiba:', err);
        }
      },
    });
  }

  // ── Qty sync: soft-delete + create új ────────────────────
  private syncUpdate(item: CartItem, newQty: number): void {
    const userId = this.auth.userId() || Number(localStorage.getItem('userId') || '0');
    if (!userId || item.id <= 0) return;

    this.cartItemsSvc.deleteCartItem(item.id).subscribe({
      next: () => {
        this.cartItemsSvc
          .createCartItem({ userId, partId: item.partId, quantity: newQty })
          .subscribe({
            next: () => this.refreshCartIds(userId),
            error: (e) => console.error('❌ syncUpdate create hiba:', e),
          });
      },
      error: (err) => {
        if (err.status === 409) {
          // Már soft-deleted → csak create
          this.cartItemsSvc
            .createCartItem({ userId, partId: item.partId, quantity: newQty })
            .subscribe({
              next: () => this.refreshCartIds(userId),
              error: (e) => console.error('❌ syncUpdate create (after 409) hiba:', e),
            });
        } else {
          console.error('❌ syncUpdate delete hiba:', err);
        }
      },
    });
  }
}
