import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
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
  filterServiceParts = inject(GetallpartsService);
  filterServiceManufacturers = inject(GetallmanufacturersService);
  filterService = inject(FilterService);

  allManufacturers = signal<ManufacturersModel[]>([]);
  allParts = signal<PartsModel[]>([]);

  categoriesExpanded = signal(true);
  allManufacturersExpanded = signal(true);
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

  // ⭐ Csak nem üres kategória szekciók
  categoriesWithManufacturers = computed(() => {
    const manufacturers = this.manufacturersByCategory();
    const filtered = this.uniqueCategories().filter((cat) => {
      const mfrs = manufacturers[cat];
      const hasManufacturers = mfrs && mfrs.size > 0;
      console.log(`📊 ${cat}: ${hasManufacturers ? mfrs.size : 0} gyártó`);
      return hasManufacturers;
    });
    console.log('✅ Kategóriák gyártókkal:', filtered);
    return filtered;
  });

  ngOnInit(): void {
    this.loadData();
  }

  loadData() {
    forkJoin({
      parts: this.filterServiceParts.getAllParts(),
      manufacturers: this.filterServiceManufacturers.getAllManufacturers(),
    }).subscribe({
      next: ({ parts, manufacturers }) => {
        this.allParts.set(parts.parts);
        this.allManufacturers.set(manufacturers.Manufacturers);

        // Initialize category sections expanded state
        const expandedState: Record<string, boolean> = {};
        this.uniqueCategories().forEach((cat) => {
          expandedState[cat] = true;
        });
        this.categorySectionsExpanded.set(expandedState);

        console.log('✅ Filter data loaded');
      },
      error: (err) => {
        console.error('❌ Filter load error:', err);
      },
    });
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

  shouldShowCategoriesCollapse(): boolean {
    return this.uniqueCategories().length > 10;
  }

  shouldShowAllManufacturersCollapse(): boolean {
    return this.uniqueManufacturers().length > 10;
  }

  shouldShowCategorySectionCollapse(category: string): boolean {
    const manufacturers = this.manufacturersByCategory()[category];
    return manufacturers ? manufacturers.size > 10 : false;
  }

  getManufacturersForCategory(category: string): ManufacturersModel[] {
    const manufacturerIds = this.manufacturersByCategory()[category];
    if (!manufacturerIds) return [];

    return this.uniqueManufacturers()
      .filter((m) => manufacturerIds.has(m.id))
      .sort((a, b) => a.name.localeCompare(b.name));
  }
}
