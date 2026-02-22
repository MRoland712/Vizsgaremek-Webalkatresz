import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-landing',
  standalone: true,
  imports: [],
  templateUrl: './admin-landing.component.html',
  styleUrl: './admin-landing.component.css',
})
export class AdminLandingComponent {
  private router = inject(Router);

  goToNewProduct() {
    this.router.navigate(['/admin/new-product']);
  }

  goToProducts() {
    this.router.navigate(['/admin/products']);
  }

  goToEcommerce() {
    this.router.navigate(['/admin/ecommerce']);
  }

  goToAnalytics() {
    this.router.navigate(['/admin/analytics']);
  }
}
