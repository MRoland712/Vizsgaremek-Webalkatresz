import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { PartsModel } from '../models/parts.model';
import { GetallpartsService } from '../services/getallparts.service';
import { ManufacturersModel } from '../models/manufacturers.model';
import { GetallmanufacturersService } from '../services/getallmanufacturers.service';
import { DynamicBreadcrumbsComponent } from '../shared/dynamic-breadcrumbs.component/dynamic-breadcrumbs.component';

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [DynamicBreadcrumbsComponent],
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.css'],
})
export class Filter implements OnInit {
  filterServiceParts = inject(GetallpartsService);
  filterServiceManufacturers = inject(GetallmanufacturersService);

  // ⭐ Signals
  allManufacturers = signal<ManufacturersModel[]>([]);
  allParts = signal<PartsModel[]>([]);

  // ⭐ Computed - EGYEDI kategóriák
  uniqueCategories = computed(() => {
    const parts = this.allParts();
    const categorySet = new Set<string>();

    parts.forEach((part) => {
      if (part.category) {
        categorySet.add(part.category);
      }
    });

    // Set → Array, ABC sorrendben
    return Array.from(categorySet).sort();
  });

  // ⭐ Computed - EGYEDI + HASZNÁLT márkák
  uniqueManufacturers = computed(() => {
    const parts = this.allParts();
    const manufacturers = this.allManufacturers();

    // Használt manufacturerId-k gyűjtése
    const usedManufacturerIds = new Set<number>();
    parts.forEach((part) => {
      if (part.manufacturerId) {
        usedManufacturerIds.add(part.manufacturerId);
      }
    });

    // Csak azok a márkák, amelyekhez van termék
    return manufacturers
      .filter((m) => usedManufacturerIds.has(m.id))
      .sort((a, b) => a.name.localeCompare(b.name)); // ABC sorrend
  });

  ngOnInit(): void {
    this.loadPartCategories();
    this.loadManufacturers();
  }

  loadPartCategories() {
    this.filterServiceParts.getAllParts().subscribe({
      next: (response) => {
        this.allParts.set(response.parts);
        console.log('✅ Egyedi kategóriák:', this.uniqueCategories());
      },
    });
  }

  loadManufacturers() {
    this.filterServiceManufacturers.getAllManufacturers().subscribe({
      next: (response) => {
        this.allManufacturers.set(response.Manufacturers);
        console.log('✅ Egyedi márkák:', this.uniqueManufacturers());
      },
    });
  }
}
