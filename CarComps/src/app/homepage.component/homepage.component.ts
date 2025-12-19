import { Component } from '@angular/core';
import { MainHeaderComponent } from '../main-header/main-header.component';
import { FooterComponent } from '../footer.component/footer.component';
import { MmtContainerComponent } from '../mmt-container/mmt-container.component';
import { Filter } from '../filter/filter.component';
import { ProductListComponent } from '../cards/filter-card.component/product-list/product-list.component';
import { CategoryCardComponent } from '../cards/filter-card.component/filter-card.component';

@Component({
  selector: 'app-homepage',
  imports: [
    MainHeaderComponent,
    FooterComponent,
    MmtContainerComponent,
    Filter,
    ProductListComponent,
    CategoryCardComponent,
  ],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css',
})
export class HomepageComponent {}
