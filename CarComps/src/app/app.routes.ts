import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { HomepageComponent } from './homepage.component/homepage.component';
import { ProductPageComponent } from './product-page.component/product-page.component';
import { adminGuard, authGuard } from './services/guard';

export const routes: Routes = [
  { path: '', component: HomepageComponent },
  { path: 'login', component: LoginComponent },
  { path: 'registration', component: RegistrationComponent },
  { path: 'products/:category', component: ProductPageComponent },
  {
    path: 'product/:id',
    loadComponent: () =>
      import('./product-page/single-product.component/single-product.component').then(
        (m) => m.ProductDetailComponent,
      ),
  },
  {
    path: 'profile',
    loadComponent: () =>
      import('./profile-page/user-profile.component/user-profile.component').then(
        (m) => m.UserProfileComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'mygarage',
    loadComponent: () =>
      import('./profile-page/user-mygarage.component/user-mygarage.component').then(
        (m) => m.UserMygarageComponent,
      ),
    canActivate: [authGuard],
  },

  // ── Admin (csak admin role) ──────────────────────────────────
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

  { path: '**', redirectTo: '' },
];
