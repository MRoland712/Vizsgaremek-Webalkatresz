import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BreadcrumbService } from '../../services/breadcrumb.service';
import { FilterService } from '../../services/filter.service';
import { BreadcrumbModel } from '../../models/breadcrumbs.model';

@Component({
  selector: 'app-dynamic-breadcrumbs',
  imports: [RouterLink],
  templateUrl: './dynamic-breadcrumbs.component.html',
  styleUrl: './dynamic-breadcrumbs.component.css',
})
export class DynamicBreadcrumbsComponent implements OnInit {
  private breadcrumbService = inject(BreadcrumbService);
  private filterService = inject(FilterService);

  breadcrumbs: BreadcrumbModel[] = [];

  // ⭐ Filter state
  get hasActiveFilters(): boolean {
    return this.filterService.hasActiveFilters();
  }

  get activeFilterCount(): number {
    const categories = this.filterService.selectedCategories().size;
    const manufacturers = this.filterService.selectedManufacturers().size;
    return categories + manufacturers;
  }

  ngOnInit(): void {
    this.breadcrumbService.breadcrumbs$.subscribe((breadcrumbs) => {
      this.breadcrumbs = breadcrumbs;
    });
  }
}
