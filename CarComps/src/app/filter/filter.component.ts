import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
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
  filterService = inject(FilterService);

  currentCategory = signal<string>('');
  categoriesExpanded         = signal(false);
  allManufacturersExpanded   = signal(false);
  categorySectionsExpanded   = signal<Record<string, boolean>>({});

  // ── Kategória szekciók (csak ahol van márka) ──────────────
  categoriesWithManufacturers = computed(() => {
    const currentCat = this.currentCategory();
    let cats = this.filterService.uniqueCategories();

    // Ha van URL kategória → csak azt mutasd
    if (currentCat) {
      cats = cats.filter(cat =>
        this.normalizeCategory(cat) === currentCat.toLowerCase()
      );
    }

    // Csak ahol van márka
    return cats.filter(cat => {
      const mfrs = this.filterService.manufacturersByCategory()[cat];
      return mfrs && mfrs.size > 0;
    });
  });

  async ngOnInit(): Promise<void> {
    // Adatok betöltése (ha még nem töltötték be)
    await this.filterService.loadData();

    // Expanded state inicializálás
    const expandedState: Record<string, boolean> = {};
    this.filterService.uniqueCategories().forEach(cat => { expandedState[cat] = false; });
    this.categorySectionsExpanded.set(expandedState);

    // URL kategória figyelése
    this.route.params.subscribe(params => {
      const category = params['category'] || '';
      if (category !== this.currentCategory()) {
        this.currentCategory.set(category);
        this.applyUrlCategory(category);
      }
    });
  }

  private applyUrlCategory(urlCategory: string) {
    if (!urlCategory) {
      this.filterService.clearFilters();
      return;
    }
    const match = this.filterService.uniqueCategories().find(cat =>
      this.normalizeCategory(cat) === urlCategory.toLowerCase()
    );
    if (match) {
      this.filterService.setCategory(match);
    }
  }

  private normalizeCategory(category: string): string {
    return category
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/\s+/g, '-');
  }

  toggleCategories()          { this.categoriesExpanded.update(v => !v); }
  toggleAllManufacturers()    { this.allManufacturersExpanded.update(v => !v); }

  toggleCategorySection(category: string) {
    this.categorySectionsExpanded.update(state => ({
      ...state,
      [category]: !state[category],
    }));
  }

  toggleCategory(category: string)     { this.filterService.toggleCategory(category); }
  toggleManufacturer(id: number)        { this.filterService.toggleManufacturer(id); }
  isCategorySelected(cat: string)       { return this.filterService.isCategorySelected(cat); }
  isManufacturerSelected(id: number)    { return this.filterService.isManufacturerSelected(id); }

  getManufacturersForCategory(category: string) {
    return this.filterService.getManufacturersForCategory(category);
  }
}