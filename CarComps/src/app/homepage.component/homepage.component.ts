import { Component } from '@angular/core';
import { MainHeaderComponent } from '../main-header/main-header.component';
import { FooterComponent } from '../footer.component/footer.component';
import { MmtContainerComponent } from '../mmt-container/mmt-container.component';
import { Filter } from '../filter/filter.component';
import { ProductListComponent } from '../cards/filter-card.component/product-list/product-list.component';
import { CategoryCardComponent } from '../cards/filter-card.component/filter-card.component';
import { PaymentForwardButtonComponent } from '../shared/payment-forward-button.component/payment-forward-button.component';
import { ProfileSidenavComponent } from '../side-navbar.component/side-navbar.component';
import { DynamicBreadcrumbsComponent } from '../shared/dynamic-breadcrumbs.component/dynamic-breadcrumbs.component';
import { AnalyticsComponent } from '../admin-page/analytics.component/analytics.component';
import { CartComponent } from '../payment/cart.component/cart.component';
import { EcommerceDashboardComponent } from '../admin-page/ecommerce.component/ecommerce-dashboard.component';
import { AdminLandingComponent } from '../admin-page/admin-landing.component/admin-landing.component';
import { CheckoutProgressComponent } from '../shared/checkoutprogress.component/checkoutprogress.component';

@Component({
  selector: 'app-homepage',
  imports: [
    MainHeaderComponent,
    FooterComponent,
    MmtContainerComponent,
    Filter,
    ProductListComponent,
    CategoryCardComponent,
    PaymentForwardButtonComponent,
    ProfileSidenavComponent,
    DynamicBreadcrumbsComponent,
    AnalyticsComponent,
    CartComponent,
    EcommerceDashboardComponent,
    AdminLandingComponent,
    CheckoutProgressComponent,
  ],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css',
})
export class HomepageComponent {}
