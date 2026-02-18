import { Injectable, signal, computed } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class FilterService {
  private _selectedCategories = signal<Set<string>>(new Set());
  private _selectedManufacturers = signal<Set<number>>(new Set());

  selectedCategories = this._selectedCategories.asReadonly();
  selectedManufacturers = this._selectedManufacturers.asReadonly();

  hasActiveFilters = computed(() => {
    return this._selectedCategories().size > 0 || this._selectedManufacturers().size > 0;
  });

  toggleCategory(category: string): void {
    this._selectedCategories.update((set) => {
      const newSet = new Set(set);
      if (newSet.has(category)) {
        newSet.delete(category);
      } else {
        newSet.add(category);
      }
      return newSet;
    });
  }

  toggleManufacturer(manufacturerId: number): void {
    this._selectedManufacturers.update((set) => {
      const newSet = new Set(set);
      if (newSet.has(manufacturerId)) {
        newSet.delete(manufacturerId);
      } else {
        newSet.add(manufacturerId);
      }
      return newSet;
    });
  }

  isCategorySelected(category: string): boolean {
    return this._selectedCategories().has(category);
  }

  isManufacturerSelected(manufacturerId: number): boolean {
    return this._selectedManufacturers().has(manufacturerId);
  }

  clearFilters(): void {
    this._selectedCategories.set(new Set());
    this._selectedManufacturers.set(new Set());
  }

  matchesFilters(product: { category: string; manufacturerId: number }): boolean {
    const categories = this._selectedCategories();
    const manufacturers = this._selectedManufacturers();

    if (categories.size === 0 && manufacturers.size === 0) {
      return true;
    }

    const categoryMatch = categories.size === 0 || categories.has(product.category);
    const manufacturerMatch = manufacturers.size === 0 || manufacturers.has(product.manufacturerId);

    return categoryMatch && manufacturerMatch;
  }
}
