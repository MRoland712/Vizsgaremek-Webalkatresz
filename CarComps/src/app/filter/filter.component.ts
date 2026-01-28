import { Component, inject, OnInit } from '@angular/core';

import { PartsModel } from '../models/parts.model';
import { GetallpartsService } from '../services/getallparts.service';
import { ManufacturersModel } from '../models/manufacturers.model';
import { GetallmanufacturersService } from '../services/getallmanufacturers.service';

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [],
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.css'],
})
export class Filter implements OnInit {
  filterServiceParts = inject(GetallpartsService);
  filterServiceManufacturers = inject(GetallmanufacturersService);
  manufacturers: ManufacturersModel[] = [];
  parts: PartsModel[] = [];
  ngOnInit(): void {
    this.loadPartCategories();
    this.loadManufacturers();
  }
  loadPartCategories() {
    this.filterServiceParts.getAllParts().subscribe({
      next: (response) => {
        this.parts = response.parts;
      },
    });
  }
  loadManufacturers() {
    this.filterServiceManufacturers.getAllManufacturers().subscribe({
      next: (response) => {
        this.manufacturers = response.Manufacturers;
      },
    });
  }
}
