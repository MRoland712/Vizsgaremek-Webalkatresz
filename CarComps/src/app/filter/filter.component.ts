import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';
import { PartsModel } from '../models/parts.model';
import { GetallpartsService } from '../services/getallparts.service';
import { ManufacturersModel } from '../models/manufacturers.model';
import { GetallmanufacturersService } from '../services/getallmanufacturers.service';
import { DynamicBreadcrumbsComponent } from '../shared/dynamic-breadcrumbs.component/dynamic-breadcrumbs.component';
import { FilterService } from '../services/filter.service';

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [DynamicBreadcrumbsComponent, CommonModule],
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.css'],
})
export class Filter implements OnInit {
  private route = inject(ActivatedRoute);
  filterServiceParts = inject(GetallpartsService);
  filterServiceManufacturers = inject(GetallmanufacturersService);
  filterService = inject(FilterService);

  allManufacturers = signal<ManufacturersModel[]>([]);
  allParts = signal<PartsModel[]>([]);
  currentCategory = signal<string>('');

  categoriesExpanded = signal(false);
  allManufacturersExpanded = signal(false);
  categorySectionsExpanded = signal<Record<string, boolean>>({});

  uniqueCategories = computed(() => {
    const parts = this.allParts();
    const categorySet = new Set<string>();
    parts.forEach((part) => {
      if (part.category) categorySet.add(part.category);
    });
    return Array.from(categorySet).sort();
  });

  uniqueManufacturers = computed(() => {
    const parts = this.allParts();
    const manufacturers = this.allManufacturers();
    const usedIds = new Set<number>();

    parts.forEach((part) => {
      if (part.manufacturerId) usedIds.add(part.manufacturerId);
    });

    return manufacturers
      .filter((m) => usedIds.has(m.id))
      .sort((a, b) => a.name.localeCompare(b.name));
  });

  manufacturersByCategory = computed(() => {
    const parts = this.allParts();
    const result: Record<string, Set<number>> = {};

    this.uniqueCategories().forEach((cat) => {
      result[cat] = new Set();
    });

    parts.forEach((part) => {
      if (part.category && part.manufacturerId && result[part.category]) {
        result[part.category].add(part.manufacturerId);
      }
    });

    return result;
  });

  // ⭐ Ha VAN URL kategória → csak az, ha NINCS → minden
  categoriesWithManufacturers = computed(() => {
    const manufacturers = this.manufacturersByCategory();
    const currentCat = this.currentCategory();

    let categories = this.uniqueCategories();

    // ⭐ Ha van URL kategória, csak azt mutasd
    if (currentCat) {
      categories = categories.filter((cat) => {
        const normalized = this.normalizeCategory(cat);
        return normalized === currentCat.toLowerCase();
      });
    }

    // Csak ahol van márka
    return categories.filter((cat) => {
      const mfrs = manufacturers[cat];
      return mfrs && mfrs.size > 0;
    });
  });

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const category = params['category'] || '';
      const previousCategory = this.currentCategory();

      // Ha változott a kategória
      if (category !== previousCategory) {
        this.currentCategory.set(category);

        // Várjuk meg amíg az adatok betöltődnek
        if (this.allParts().length === 0) {
          this.loadData().then(() => {
            this.autoSelectCategory(category);
          });
        } else {
          this.autoSelectCategory(category);
        }
      }
    });
  }

  async loadData(): Promise<void> {
    return new Promise((resolve) => {
      forkJoin({
        parts: this.filterServiceParts.getAllParts(),
        manufacturers: this.filterServiceManufacturers.getAllManufacturers(),
      }).subscribe({
        next: ({ parts, manufacturers }) => {
          this.allParts.set(parts.parts);
          this.allManufacturers.set(manufacturers.Manufacturers);

          const expandedState: Record<string, boolean> = {};
          this.uniqueCategories().forEach((cat) => {
            expandedState[cat] = false;
          });
          this.categorySectionsExpanded.set(expandedState);

          console.log('✅ Filter data loaded');
          resolve();
        },
        error: (err) => {
          console.error('❌ Filter load error:', err);
          resolve();
        },
      });
    });
  }

  // ⭐ Automatikus kategória kiválasztás
  private autoSelectCategory(urlCategory: string) {
    console.log('🔍 autoSelectCategory meghívva:', urlCategory);

    if (!urlCategory) {
      console.log('❌ Nincs URL kategória - filterek törlése');
      this.filterService.clearFilters();
      return;
    }

    const categories = this.uniqueCategories();
    console.log('📋 Elérhető kategóriák:', categories);

    // ⭐ Normalizált összehasonlítás
    const category = categories.find((cat) => {
      const normalized = this.normalizeCategory(cat);
      console.log(
        `  "${cat}" → "${normalized}" === "${urlCategory}"? ${normalized === urlCategory.toLowerCase()}`,
      );
      return normalized === urlCategory.toLowerCase();
    });

    if (category) {
      console.log('✅ Kategória MEGTALÁLVA:', category);
      this.filterService.clearFilters();
      this.filterService.toggleCategory(category);
      console.log('✅ Auto-selected:', category);
    } else {
      console.warn('❌ Kategória NEM található:', urlCategory);
    }
  }

  // ⭐ Normalizálás (ugyanaz mint filter-card-ban)
  private normalizeCategory(category: string): string {
    return category
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/\s+/g, '-');
  }

  toggleCategories() {
    this.categoriesExpanded.update((v) => !v);
  }

  toggleAllManufacturers() {
    this.allManufacturersExpanded.update((v) => !v);
  }

  toggleCategorySection(category: string) {
    this.categorySectionsExpanded.update((state) => ({
      ...state,
      [category]: !state[category],
    }));
  }

  toggleCategory(category: string) {
    this.filterService.toggleCategory(category);
  }

  toggleManufacturer(manufacturerId: number) {
    this.filterService.toggleManufacturer(manufacturerId);
  }

  isCategorySelected(category: string): boolean {
    return this.filterService.isCategorySelected(category);
  }

  isManufacturerSelected(manufacturerId: number): boolean {
    return this.filterService.isManufacturerSelected(manufacturerId);
  }

  getManufacturersForCategory(category: string): ManufacturersModel[] {
    const manufacturerIds = this.manufacturersByCategory()[category];
    if (!manufacturerIds) return [];

    return this.uniqueManufacturers()
      .filter((m) => manufacturerIds.has(m.id))
      .sort((a, b) => a.name.localeCompare(b.name));
  }
}
