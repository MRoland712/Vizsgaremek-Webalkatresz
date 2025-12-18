import { Component } from '@angular/core';
import { MainHeaderComponent } from '../main-header/main-header.component';
import { FooterComponent } from '../footer.component/footer.component';
import { MmtContainerComponent } from '../mmt-container/mmt-container.component';
import { Filter } from '../filter/filter.component';

@Component({
  selector: 'app-homepage',
  imports: [MainHeaderComponent, FooterComponent, MmtContainerComponent, Filter],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css',
})
export class HomepageComponent {}
