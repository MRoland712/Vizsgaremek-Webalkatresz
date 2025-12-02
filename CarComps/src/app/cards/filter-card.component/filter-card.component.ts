import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-filter-card',
  imports: [],
  templateUrl: './filter-card.component.html',
  styleUrl: './filter-card.component.css',
})
export class CategoryCardComponent {
  @Input() categoryName: string = 'Fékek';
  @Input() imageUrl: string = '';
  @Input() categoryUrl: string = '/';
}
