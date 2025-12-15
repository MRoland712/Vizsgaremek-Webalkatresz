import { Component, Input } from '@angular/core';
import { dummyParts } from '../models/parts.model';

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [],
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.css'],
})
export class Filter {
  @Input() parts: dummyParts[] = [
    {
      id: 1,
      name: 'Fék',
    },
    {
      id: 2,
      name: 'Motor',
    },
    {
      id: 3,
      name: 'Olajszűrő',
    },
    {
      id: 4,
      name: 'Légszűrő',
    },
    {
      id: 5,
      name: 'Kuplung',
    },
  ];
}
