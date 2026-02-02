import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router } from '@angular/router';
import { filter, map } from 'rxjs';
import { ButtonConfig } from '../../services/paymentBtn.service';

@Component({
  selector: 'app-payment-forward-button',
  imports: [],
  templateUrl: './payment-forward-button.html',
  styleUrl: './payment-forward-button.css',
})
export class PaymentForwardButtonComponent {
  //jelenlegi routert nézi filterezi
  private router = inject(Router);
  currentRoute = toSignal(
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      map(() => this.router.url)
    ),
    { initialValue: this.router.url }
  );

  //jelenlegi route alapján változtatjuk a gomb szövegét
  config = computed(() => {
    const route = this.currentRoute();
    const configs: Record<string, ButtonConfig> = {
      '/': {
        text: 'Böngészés',
      },
      '/login': {
        text: 'Bejelentkezés',
      },
      '/registration': {
        text: 'Regisztráció',
      },
      '/cart': {
        text: 'Pénztár',
      },
      '/profile': {
        text: 'Profil mentése',
      },
    };
    return (
      configs[route] || {
        text: 'Kattints',
      }
    );
  });
}
