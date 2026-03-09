import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router } from '@angular/router';
import { filter, map } from 'rxjs';

interface ButtonConfig {
  text: string;
  icon: string; // FontAwesome class pl. 'fa-truck', 'fa-credit-card'
}

@Component({
  selector: 'app-payment-forward-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './payment-forward-button.html',
  styleUrl: './payment-forward-button.css',
})
export class PaymentForwardButtonComponent {
  private router = inject(Router);

  currentRoute = toSignal(
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      map(() => this.router.url),
    ),
    { initialValue: this.router.url },
  );

  config = computed((): ButtonConfig => {
    const route = this.currentRoute();
    const configs: Record<string, ButtonConfig> = {
      '/': { text: 'Böngészés', icon: 'fa-magnifying-glass' },
      '/login': { text: 'Bejelentkezés', icon: 'fa-arrow-right-to-bracket' },
      '/registration': { text: 'Regisztráció', icon: 'fa-user-plus' },
      '/cart': { text: 'Tovább a szállításhoz', icon: 'fa-truck' },
      '/delivery': { text: 'Tovább a fizetéshez', icon: 'fa-credit-card' },
      '/payment': { text: 'Fizetés', icon: 'fa-lock' },
      '/profile': { text: 'Profil mentése', icon: 'fa-floppy-disk' },
    };
    return configs[route] ?? { text: 'Tovább', icon: 'fa-arrow-right' };
  });
}
