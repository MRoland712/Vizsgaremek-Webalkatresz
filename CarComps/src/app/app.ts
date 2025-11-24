import { Component, signal } from '@angular/core';
import { RegistrationComponent } from './registration/registration.component';
import { LoginComponent } from './login/login.component';
import { MainHeaderComponent } from './main-header/main-header.component';
import { ProductCardComponent } from './product-card/product-card.component';
import { ProductListComponent } from './product-list/product-list.component';
import { MMTSelectorComponent } from './mmt-selector/mmt-selector.component';
import { MmtContainerComponent } from './mmt-container/mmt-container.component';

@Component({
  selector: 'app-root',
  imports: [
    RegistrationComponent,
    LoginComponent,
    MainHeaderComponent,
    ProductCardComponent,
    ProductListComponent,
    MMTSelectorComponent,
    MmtContainerComponent,
  ],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('CarComps');
}
