import { Component, inject } from '@angular/core';
import { MainHeaderComponent } from '../main-header/main-header.component';
import { FooterComponent } from '../footer.component/footer.component';
import { MmtContainerComponent } from '../mmt-container/mmt-container.component';
import { CategoryCardComponent } from '../cards/filter-card.component/filter-card.component';
import { Filter } from '../filter/filter.component';
import { dummyParts } from '../models/parts.model';

@Component({
  selector: 'app-homepage',
  imports: [
    MainHeaderComponent,
    FooterComponent,
    MmtContainerComponent,
    CategoryCardComponent,
    Filter,
  ],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css',
})
export class HomepageComponent {
  /** parts to pass into the <app-filter> component */
}
