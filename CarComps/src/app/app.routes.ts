import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { HomepageComponent } from './homepage.component/homepage.component';
import { adminGuard, authGuard } from './services/guard';

export const routes: Routes = [
  // ── Publikus ─────────────────────────────────────────────
  { path: '', component: HomepageComponent },
  { path: 'login', component: LoginComponent },
  { path: 'registration', component: RegistrationComponent },
  {
    path: 'products/:category',
    loadComponent: () =>
      import('./product-page.component/product-page.component').then((m) => m.ProductPageComponent),
  },
  {
    path: 'product/:id',
    loadComponent: () =>
      import('./product-page/single-product.component/single-product.component').then(
        (m) => m.ProductDetailComponent,
      ),
  },

  // ── Checkout folyamat ─────────────────────────────────────
  {
    path: 'cart',
    loadComponent: () =>
      import('./payment/cart.component/cart.component').then((m) => m.CartComponent),
    canActivate: [authGuard],
  },
  {
    path: 'delivery',
    loadComponent: () =>
      import('./payment/delivery.component/delivery.component').then((m) => m.DeliveryComponent),
    canActivate: [authGuard],
  },
  {
    path: 'payment',
    loadComponent: () =>
      import('./payment/pay.component/pay.component').then((m) => m.PayComponent),
    canActivate: [authGuard],
  },
  {
    path: 'summary',
    loadComponent: () =>
      import('./payment/summary.component/summary.component').then((m) => m.SummaryComponent),
    canActivate: [authGuard],
  },

  // ── Védett (bejelentkezés kell) ───────────────────────────
  {
    path: 'profile',
    loadComponent: () =>
      import('./profile-page/user-profile.component/user-profile.component').then(
        (m) => m.UserProfileComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'profile/garage',
    loadComponent: () =>
      import('./profile-page/user-mygarage.component/user-mygarage.component').then(
        (m) => m.UserMygarageComponent,
      ),
    canActivate: [authGuard],
  },

  // ── Admin (csak admin role) ───────────────────────────────
  {
    path: 'admin',
    loadComponent: () =>
      import('./admin-page/admin-landing.component/admin-landing.component').then(
        (m) => m.AdminLandingComponent,
      ),
    canActivate: [adminGuard],
  },
  {
    path: 'admin/new-product',
    loadComponent: () =>
      import('./admin-page/new-product.component/new-product.component').then(
        (m) => m.NewProductComponent,
      ),
    canActivate: [adminGuard],
  },
  {
    path: 'admin/products',
    loadComponent: () =>
      import('./admin-page/product-list-admin.component/product-list-admin.component').then(
        (m) => m.ProductListAdminComponent,
      ),
    canActivate: [adminGuard],
  },
  {
    path: 'admin/ecommerce',
    loadComponent: () =>
      import('./admin-page/ecommerce.component/ecommerce-dashboard.component').then(
        (m) => m.EcommerceDashboardComponent,
      ),
    canActivate: [adminGuard],
  },
  {
    path: 'admin/analytics',
    loadComponent: () =>
      import('./admin-page/analytics.component/analytics.component').then(
        (m) => m.AnalyticsComponent,
      ),
    canActivate: [adminGuard],
  },

  // ── 404 ───────────────────────────────────────────────────
  { path: '**', redirectTo: '' },
];
