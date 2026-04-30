import { Component, computed, inject, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router } from '@angular/router';
import { filter, map } from 'rxjs';

interface ButtonConfig {
  text: string;
  icon: string;
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

  // ⭐ Override input-ok — ha meg van adva, az URL config helyett ezt használja
  @Input() overrideText?: string;
  @Input() overrideIcon?: string;

  currentRoute = toSignal(
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      map(() => this.router.url),
    ),
    { initialValue: this.router.url },
  );

  config = computed((): ButtonConfig => {
    if (this.overrideText) {
      return { text: this.overrideText, icon: this.overrideIcon ?? 'fa-arrow-right' };
    }
    const route = this.currentRoute();
    const configs: Record<string, ButtonConfig> = {
      '/': { text: 'Böngészés', icon: 'fa-magnifying-glass' },
      '/login': { text: 'Bejelentkezés', icon: 'fa-arrow-right-to-bracket' },
      '/registration': { text: 'Regisztráció', icon: 'fa-user-plus' },
      '/cart': { text: 'Tovább a szállításhoz', icon: 'fa-truck' },
      '/delivery': { text: 'Tovább a nyugtázáshoz', icon: 'fa-file-lines' },
      '/summary': { text: 'Tovább a fizetéshez', icon: 'fa-credit-card' },
      '/payment': { text: 'Fizetés', icon: 'fa-lock' },
      '/profile': { text: 'Profil mentése', icon: 'fa-floppy-disk' },
    };
    return configs[route] ?? { text: 'Tovább', icon: 'fa-arrow-right' };
  });
}
