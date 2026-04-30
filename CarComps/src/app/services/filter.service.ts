import { Injectable, signal, computed, inject } from '@angular/core';
import { forkJoin } from 'rxjs';
import { GetAllPartsWithImagesService } from './getallpartswithimages.service';
import { GetallmanufacturersService } from './getallmanufacturers.service';
import { GetPartsByVehicleTypeService } from './getpartsbyvehicletype.service';
import { PartWithImagesModel } from '../models/getallpartswithimages.model';
import { ManufacturersModel } from '../models/manufacturers.model';

export type VehicleType = 'car' | 'truck' | 'motor' | null;

@Injectable({ providedIn: 'root' })
export class FilterService {
  private partsService = inject(GetAllPartsWithImagesService);
  private manufacturersService = inject(GetallmanufacturersService);
  private vehicleTypeService = inject(GetPartsByVehicleTypeService);

  // ── Nyers adatok ──────────────────────────────────────────
  private _allParts = signal<PartWithImagesModel[]>([]);
  private _vehicleParts = signal<PartWithImagesModel[]>([]);
  allManufacturers = signal<ManufacturersModel[]>([]);
  isLoading = signal(false);
  isLoaded = signal(false);

  // ── Jármű típus szűrő ────────────────────────────────────
  selectedVehicleType = signal<VehicleType>(null);

  // ── Aktív részhalmaz (jármű típus szerint) ───────────────
  allParts = computed(() => {
    if (this.selectedVehicleType()) return this._vehicleParts();
    return this._allParts();
  });

  // ── Szűrők ───────────────────────────────────────────────
  private _selectedCategories = signal<Set<string>>(new Set());
  private _selectedManufacturers = signal<Set<number>>(new Set());

  selectedCategories = this._selectedCategories.asReadonly();
  selectedManufacturers = this._selectedManufacturers.asReadonly();

  // ── Computed listák ───────────────────────────────────────
  uniqueCategories = computed(() => {
    const set = new Set<string>();
    this.allParts().forEach((p) => {
      if (p.category) set.add(p.category);
    });
    return Array.from(set).sort();
  });

  uniqueManufacturers = computed(() => {
    const usedIds = new Set<number>();
    this.allParts().forEach((p) => {
      if (p.manufacturerId) usedIds.add(p.manufacturerId);
    });
    return this.allManufacturers()
      .filter((m) => usedIds.has(m.id))
      .sort((a, b) => a.name.localeCompare(b.name));
  });

  manufacturersByCategory = computed(() => {
    const result: Record<string, Set<number>> = {};
    this.uniqueCategories().forEach((cat) => {
      result[cat] = new Set();
    });
    this.allParts().forEach((p) => {
      if (p.category && p.manufacturerId && result[p.category]) {
        result[p.category].add(p.manufacturerId);
      }
    });
    return result;
  });

  filteredParts = computed(() => {
    const parts = this.allParts();
    if (!this.hasActiveFilters()) return parts;
    return parts.filter((p) =>
      this.matchesFilters({
        category: p.category,
        manufacturerId: p.manufacturerId,
      }),
    );
  });

  hasActiveFilters = computed(
    () => this._selectedCategories().size > 0 || this._selectedManufacturers().size > 0,
  );

  // ── Adatbetöltés (egyszer) ────────────────────────────────
  loadData(): Promise<void> {
    if (this.isLoaded()) return Promise.resolve();
    if (this.isLoading()) return Promise.resolve();

    this.isLoading.set(true);
    return new Promise((resolve) => {
      forkJoin({
        parts: this.partsService.getAllPartsWithImages(),
        manufacturers: this.manufacturersService.getAllManufacturers(),
      }).subscribe({
        next: ({ parts, manufacturers }) => {
          this._allParts.set(parts.parts ?? []);
          this.allManufacturers.set(manufacturers.Manufacturers ?? []);
          this.isLoaded.set(true);
          this.isLoading.set(false);
          resolve();
        },
        error: (err) => {
          console.error('❌ FilterService loadData hiba:', err);
          this.isLoading.set(false);
          resolve();
        },
      });
    });
  }

  // ── Jármű típus váltás ────────────────────────────────────
  setVehicleType(type: VehicleType): void {
    if (this.selectedVehicleType() === type) {
      // Ugyanarra kattintott → reset
      this.selectedVehicleType.set(null);
      this._vehicleParts.set([]);
      this.clearFilters();
      return;
    }

    this.selectedVehicleType.set(type);
    this.clearFilters();

    if (!type) {
      this._vehicleParts.set([]);
      return;
    }

    this.isLoading.set(true);
    this.vehicleTypeService.getPartsByVehicleType({ vehicleType: type }).subscribe({
      next: (res) => {
        // A vehicleType endpoint nem ad vissza imageUrl-t
        // Párosítjuk az allParts képeivel
        const imageMap = new Map<number, string>();
        this._allParts().forEach((p) => {
          if (p.imageUrl) imageMap.set(p.id, p.imageUrl);
        });

        const partsWithImages: PartWithImagesModel[] = (res.parts ?? []).map((p) => ({
          id: p.id,
          name: p.name,
          sku: p.sku,
          category: p.category,
          price: Number(p.price),
          stock: p.stock,
          isActive: p.isActive,
          status: p.status,
          manufacturerId: p.manufacturerId,
          imageUrl: imageMap.get(p.id) || 'assets/placeholder.jpg',
          createdAt: p.createdAt,
          updatedAt: p.updatedAt,
          description: p.description,
        }));

        this._vehicleParts.set(partsWithImages);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('❌ getPartsByVehicleType hiba:', err);
        this._vehicleParts.set([]);
        this.isLoading.set(false);
      },
    });
  }

  // ── Filter műveletek ──────────────────────────────────────
  toggleCategory(category: string): void {
    this._selectedCategories.update((set) => {
      const next = new Set(set);
      next.has(category) ? next.delete(category) : next.add(category);
      return next;
    });
  }

  toggleManufacturer(id: number): void {
    this._selectedManufacturers.update((set) => {
      const next = new Set(set);
      next.has(id) ? next.delete(id) : next.add(id);
      return next;
    });
  }

  setCategory(category: string): void {
    this._selectedCategories.set(new Set([category]));
    this._selectedManufacturers.set(new Set());
  }

  clearFilters(): void {
    this._selectedCategories.set(new Set());
    this._selectedManufacturers.set(new Set());
  }

  isCategorySelected(category: string): boolean {
    return this._selectedCategories().has(category);
  }

  isManufacturerSelected(id: number): boolean {
    return this._selectedManufacturers().has(id);
  }

  getManufacturersForCategory(category: string): ManufacturersModel[] {
    const ids = this.manufacturersByCategory()[category];
    if (!ids) return [];
    return this.uniqueManufacturers().filter((m) => ids.has(m.id));
  }

  matchesFilters(product: { category: string; manufacturerId: number }): boolean {
    const cats = this._selectedCategories();
    const mfrs = this._selectedManufacturers();
    const catOk = cats.size === 0 || cats.has(product.category);
    const mfrOk = mfrs.size === 0 || mfrs.has(product.manufacturerId);
    return catOk && mfrOk;
  }
}
