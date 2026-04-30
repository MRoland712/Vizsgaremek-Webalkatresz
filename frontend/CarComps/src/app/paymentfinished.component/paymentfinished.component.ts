import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MainHeaderComponent } from '../main-header/main-header.component';
import { FooterComponent } from '../footer.component/footer.component';

@Component({
  selector: 'app-paymentfinish',
  standalone: true,
  imports: [MainHeaderComponent, FooterComponent],
  templateUrl: './paymentfinished.component.html',
  styleUrl: './paymentfinished.component.css',
})
export class PaymentFinishComponent {
  private router = inject(Router);
  goHome() {
    this.router.navigate(['/']);
  }
  goOrders() {
    this.router.navigate(['/profile']);
  }
}
