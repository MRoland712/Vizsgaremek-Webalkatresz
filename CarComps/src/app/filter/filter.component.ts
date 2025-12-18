import { Component, inject, OnInit } from '@angular/core';

import { PartsModel } from '../models/parts.model';
import { GetallpartsService } from '../services/getallparts.service';

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [],
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.css'],
})
export class Filter implements OnInit {
  filterService = inject(GetallpartsService);
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
