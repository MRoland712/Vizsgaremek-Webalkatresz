import { Component, inject, Input, OnInit } from '@angular/core';
import { dummyParts, PartsModel } from '../models/parts.model';
import { FilterService } from './filter.service';

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [],
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.css'],
})
export class Filter implements OnInit {
  filterService = inject(FilterService);
  parts: PartsModel[] = [];
  ngOnInit(): void {
    this.loadPartCategories();
  }
  loadPartCategories() {
    this.filterService.getAllParts().subscribe({
      next: (response) => {
        this.parts = response.parts;
      },
    });
  }
}
