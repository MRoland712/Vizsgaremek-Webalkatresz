// src/app/main-header/main-header.component.ts
// ⭐ JAVÍTVA: refreshUserData() hívás

import { Component, DestroyRef, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { SearchResult } from './search.service';
import { AuthService } from '../services/auth.service';

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
  authService = inject(AuthService);

  imgSrc = '/assets/CarComps_Logo_BigassC.png';
  searchControl = new FormControl('');
  searchResults = signal<SearchResult[]>([]);
  isSearching = signal(false);
  showDropdown = signal(false);

  isLoggedIn = this.authService.isLoggedIn;
  userName = this.authService.userName;
  userEmail = this.authService.userEmail;

  cartItems = signal<CartItem[]>([]);
  cartItemCount = signal(0);
  cartTotal = signal(0);

  garageMakeControl = new FormControl('');
  garageModelControl = new FormControl({ value: '', disabled: true });
  garageYearControl = new FormControl({ value: '', disabled: true });

  constructor() {
    // ⭐ JAVÍTÁS: Signal-ok frissítése
    this.authService.refreshUserData();

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

    const garageMakeSubscription = this.garageMakeControl.valueChanges.subscribe((makeId) => {
      this.garageModelControl.setValue('');
      this.garageYearControl.setValue('');
      this.garageYearControl.disable();
      if (makeId) {
        this.garageModelControl.enable();
      } else {
        this.garageModelControl.disable();
      }
    });

    const garageModelSubscription = this.garageModelControl.valueChanges.subscribe((modelId) => {
      this.garageYearControl.setValue('');
      if (modelId) {
        this.garageYearControl.enable();
      } else {
        this.garageYearControl.disable();
      }
    });

    this.destroyRef.onDestroy(() => {
      searchSubscription.unsubscribe();
      garageMakeSubscription.unsubscribe();
      garageModelSubscription.unsubscribe();
    });

    this.loadCartData();

    console.log('👤 Main Header:');
    console.log('  Név:', this.userName());
    console.log('  Email:', this.userEmail());
  }

  loadCartData() {
    if (this.isLoggedIn()) {
      this.cartItems.set([]);
      this.updateCartCalculations();
    }
  }

  updateCartCalculations() {
    const items = this.cartItems();
    const totalCount = items.reduce((sum, item) => sum + item.quantity, 0);
    this.cartItemCount.set(totalCount);
    const total = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
    this.cartTotal.set(total);
  }

  removeFromCart(itemId: number) {
    const updatedItems = this.cartItems().filter((item) => item.id !== itemId);
    this.cartItems.set(updatedItems);
    this.updateCartCalculations();
  }

  performSearch(searchTerm: string) {
    this.isSearching.set(true);
    this.showDropdown.set(true);
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
    this.searchControl.setValue('');
    this.showDropdown.set(false);
  }

  closeDropdown() {
    setTimeout(() => this.showDropdown.set(false), 200);
  }

  onSearchSubmit() {
    const searchTerm = this.searchControl.value?.trim();
    if (searchTerm) {
      this.showDropdown.set(false);
    }
  }

  selectGarageCar() {
    const year = this.garageYearControl.value;
    if (!year) return;
    alert(`Kiválasztva: ${year}`);
  }

  logout() {
    this.authService.logout();
  }
}
