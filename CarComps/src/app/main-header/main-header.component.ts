// src/app/main-header/main-header.component.ts

import { Component, DestroyRef, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { SearchResult } from './search.service';
import { AuthService } from '../services/auth.service';

// Cart item interface
export interface CartItem {
  id: number;
  name: string;
  price: number;
  quantity: number;
  imageUrl?: string;
}

@Component({
  selector: 'app-main-header',
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './main-header.component.html',
  styleUrl: './main-header.component.css',
})
export class MainHeaderComponent {
  private destroyRef = inject(DestroyRef);
  authService = inject(AuthService); // ← AuthService inject

  imgSrc = '/assets/CarComps_Logo_BigassC.png';

  // Form control a kereséshez
  searchControl = new FormControl('');

  // Search state
  searchResults = signal<SearchResult[]>([]);
  isSearching = signal(false);
  showDropdown = signal(false);

  // ==========================================
  // CART STATE
  // ==========================================

  // Bejelentkezve van-e (AuthService-ből)
  isLoggedIn = this.authService.isLoggedIn;

  // Kosár termékei
  cartItems = signal<CartItem[]>([]);

  // Számított értékek
  cartItemCount = signal(0);
  cartTotal = signal(0);

  // ==========================================
  // GARAGE MMT SELECTOR - FormControls
  // ==========================================

  garageMakeControl = new FormControl('');
  garageModelControl = new FormControl({ value: '', disabled: true });
  garageYearControl = new FormControl({ value: '', disabled: true });

  constructor() {
    // ==========================================
    // SEARCH - Debounce
    // ==========================================
    const searchSubscription = this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe({
        next: (searchTerm) => {
          if (searchTerm && searchTerm.trim().length > 0) {
            this.performSearch(searchTerm.trim());
          } else {
            this.searchResults.set([]);
            this.showDropdown.set(false);
          }
        },
      });

    // ==========================================
    // GARAGE: Make változás → Model enable
    // ==========================================
    const garageMakeSubscription = this.garageMakeControl.valueChanges.subscribe((makeId) => {
      // Reset model és year
      this.garageModelControl.setValue('');
      this.garageYearControl.setValue('');
      this.garageYearControl.disable();

      if (makeId) {
        this.garageModelControl.enable();
        console.log('Garage - Márka:', makeId);

        // TODO: API hívás modellekhez
        // this.http.get(`/api/models/${makeId}`).subscribe(...)
      } else {
        this.garageModelControl.disable();
      }
    });

    // ==========================================
    // GARAGE: Model változás → Year enable
    // ==========================================
    const garageModelSubscription = this.garageModelControl.valueChanges.subscribe((modelId) => {
      // Reset year
      this.garageYearControl.setValue('');

      if (modelId) {
        this.garageYearControl.enable();
        console.log('Garage - Modell:', modelId);

        // TODO: API hívás évjáratokhoz
        // this.http.get(`/api/years/${modelId}`).subscribe(...)
      } else {
        this.garageYearControl.disable();
      }
    });

    // ==========================================
    // Cleanup subscriptions
    // ==========================================
    this.destroyRef.onDestroy(() => {
      searchSubscription.unsubscribe();
      garageMakeSubscription.unsubscribe();
      garageModelSubscription.unsubscribe();
    });

    // Kosár adatok betöltése
    this.loadCartData();
  }

  // ==========================================
  // CART METHODS
  // ==========================================

  loadCartData() {
    // TODO: Cart service-ből töltsd be a kosár tartalmát
    // Ha BE van jelentkezve, töltsük be a cart-ot
    if (this.isLoggedIn()) {
      // TODO: Példa API hívás
      // this.cartService.getCart().subscribe(items => {
      //   this.cartItems.set(items);
      //   this.updateCartCalculations();
      // });

      // EGYELŐRE ÜRES - nincs mock adat
      this.cartItems.set([]);
      this.updateCartCalculations();
    }
  }

  updateCartCalculations() {
    const items = this.cartItems();

    // Termékek száma
    const totalCount = items.reduce((sum, item) => sum + item.quantity, 0);
    this.cartItemCount.set(totalCount);

    // Teljes ár
    const total = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
    this.cartTotal.set(total);
  }

  removeFromCart(itemId: number) {
    const updatedItems = this.cartItems().filter((item) => item.id !== itemId);
    this.cartItems.set(updatedItems);
    this.updateCartCalculations();

    // TODO: Cart service API hívás
    console.log('Termék eltávolítva:', itemId);
  }

  // ==========================================
  // SEARCH METHODS
  // ==========================================

  performSearch(searchTerm: string) {
    this.isSearching.set(true);
    this.showDropdown.set(true);

    // TODO: API hívás
    setTimeout(() => {
      const mockResults: SearchResult[] = [
        { id: 1, name: 'Fékbetét Bosch', category: 'Fékrendszer', price: 8990 },
        { id: 2, name: 'Fékdob', category: 'Fékrendszer', price: 12500 },
        { id: 3, name: 'Olajszűrő Mann', category: 'Szűrők', price: 2990 },
      ].filter(
        (item) =>
          item.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
          item.category.toLowerCase().includes(searchTerm.toLowerCase()),
      );

      this.searchResults.set(mockResults);
      this.isSearching.set(false);
    }, 500);
  }

  selectResult(result: SearchResult) {
    console.log('Kiválasztott termék:', result);
    this.searchControl.setValue('');
    this.showDropdown.set(false);
  }

  closeDropdown() {
    setTimeout(() => {
      this.showDropdown.set(false);
    }, 200);
  }

  onSearchSubmit() {
    const searchTerm = this.searchControl.value?.trim();
    if (searchTerm) {
      console.log('Keresés elküldve:', searchTerm);
      this.showDropdown.set(false);
    }
  }

  // ==========================================
  // GARAGE: Autó kiválasztása
  // ==========================================

  selectGarageCar() {
    const make = this.garageMakeControl.value;
    const model = this.garageModelControl.value;
    const year = this.garageYearControl.value;

    if (!year) {
      console.log('Nincs kiválasztva autó!');
      return;
    }

    console.log('Garage - Kiválasztott autó:', { make, model, year });

    // TODO: Autó mentése
    // localStorage.setItem('selectedCar', JSON.stringify({ make, model, year }));

    alert(`Kiválasztva: ${year} (Model ID: ${model})`);
  }

  // ==========================================
  // LOGOUT
  // ==========================================

  logout() {
    // AuthService logout hívása
    this.authService.logout();

    console.log('✅ Kijelentkezés sikeres');
  }
}
