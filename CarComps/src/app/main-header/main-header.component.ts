import { Component, DestroyRef, inject, signal, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { SearchResult } from './search.service';
import { AuthService } from '../services/auth.service';
import { CartService } from '../services/cart.service';

@Component({
  selector: 'app-main-header',
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './main-header.component.html',
  styleUrl: './main-header.component.css',
})
export class MainHeaderComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  authService = inject(AuthService);
  cartService = inject(CartService);

  imgSrc = '/assets/CarComps_Logo_BigassC.png';
  searchControl = new FormControl('');
  searchResults = signal<SearchResult[]>([]);
  isSearching = signal(false);
  showDropdown = signal(false);

  isLoggedIn = this.authService.isLoggedIn;
  userName = this.authService.userName;
  userEmail = this.authService.userEmail;

  cartItems = this.cartService.cartItems;
  cartItemCount = this.cartService.cartItemCount;
  cartTotal = this.cartService.cartTotal;

  garageMakeControl = new FormControl('');
  garageModelControl = new FormControl({ value: '', disabled: true });
  garageYearControl = new FormControl({ value: '', disabled: true });

  ngOnInit(): void {
    this.authService.refreshUserData();
    // Bejelentkezve → töltjük a kosarat backendből
    if (this.authService.isLoggedIn()) {
      this.cartService.loadCartFromBackend();
    }
  }

  constructor() {
    const searchSub = this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe((term) => {
        if (term && term.trim().length > 0) {
          this.performSearch(term.trim());
        } else {
          this.searchResults.set([]);
          this.showDropdown.set(false);
        }
      });

    const makeSub = this.garageMakeControl.valueChanges.subscribe((makeId) => {
      this.garageModelControl.setValue('');
      this.garageYearControl.setValue('');
      this.garageYearControl.disable();
      if (makeId) this.garageModelControl.enable();
      else this.garageModelControl.disable();
    });

    const modelSub = this.garageModelControl.valueChanges.subscribe((modelId) => {
      this.garageYearControl.setValue('');
      if (modelId) this.garageYearControl.enable();
      else this.garageYearControl.disable();
    });

    this.destroyRef.onDestroy(() => {
      searchSub.unsubscribe();
      makeSub.unsubscribe();
      modelSub.unsubscribe();
    });
  }

  removeFromCart(itemId: number) {
    this.cartService.removeFromCart(itemId);
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
    this.showDropdown.set(false);
  }
  selectGarageCar() {
    const y = this.garageYearControl.value;
    if (!y) return;
  }

  logout() {
    this.cartService.clearCart();
    this.authService.logout();
  }
}
