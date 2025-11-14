import { Component, DestroyRef, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { debounceTime, distinctUntilChanged } from 'rxjs';

// Interface a keresési eredményekhez (majd az API szerint módosítható)
export interface SearchResult {
  id: number;
  name: string;
  category: string;
  price: number;
  imageUrl?: string;
}

@Component({
  selector: 'app-main-header',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './main-header.component.html',
  styleUrl: './main-header.component.css',
})
export class MainHeaderComponent {
  private destroyRef = inject(DestroyRef);

  imgSrc = '/assets/CarComps_Logo_BigassC.png';

  // Form control a kereséshez
  searchControl = new FormControl('');

  // State management
  searchResults = signal<SearchResult[]>([]);
  isSearching = signal(false);
  showDropdown = signal(false);

  constructor() {
    // Debounce a kereséshez - várunk 300ms-et mielőtt API hívást indítunk
    const subscription = this.searchControl.valueChanges
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

    this.destroyRef.onDestroy(() => subscription.unsubscribe());
  }

  // Ez a metódus hívja majd az API-t
  performSearch(searchTerm: string) {
    this.isSearching.set(true);
    this.showDropdown.set(true);

    // TODO: Itt fog majd jönni az API hívás
    // Például: this.searchService.search(searchTerm).subscribe(...)

    // Jelenleg mock adatokkal dolgozunk (töröld majd az API integrálásakor)
    setTimeout(() => {
      const mockResults: SearchResult[] = [
        { id: 1, name: 'Fékbetét Bosch', category: 'Fékrendszer', price: 8990 },
        { id: 2, name: 'Fékdob', category: 'Fékrendszer', price: 12500 },
        { id: 3, name: 'Olajszűrő Mann', category: 'Szűrők', price: 2990 },
      ].filter(
        (item) =>
          item.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
          item.category.toLowerCase().includes(searchTerm.toLowerCase())
      );

      this.searchResults.set(mockResults);
      this.isSearching.set(false);
    }, 500); // Szimuláljuk a network delay-t
  }

  // Eredmény kiválasztása
  selectResult(result: SearchResult) {
    console.log('Kiválasztott termék:', result);
    // TODO: Navigálás a termék oldalára
    // Például: this.router.navigate(['/product', result.id]);

    this.searchControl.setValue('');
    this.showDropdown.set(false);
  }

  // Dropdown bezárása
  closeDropdown() {
    setTimeout(() => {
      this.showDropdown.set(false);
    }, 200); // Kis késleltetés, hogy a click event lefusson
  }

  // Form submit kezelése
  onSearchSubmit() {
    const searchTerm = this.searchControl.value?.trim();
    if (searchTerm) {
      console.log('Keresés elküldve:', searchTerm);
      // TODO: Navigálás a keresési eredmények oldalára
      // Például: this.router.navigate(['/search'], { queryParams: { q: searchTerm } });

      this.showDropdown.set(false);
    }
  }
}
