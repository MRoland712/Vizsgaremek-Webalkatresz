import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  imports: [],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css',
})
export class FooterComponent {
  currentYear = new Date().getFullYear();
  logoSrc = '/assets/CarComps_Logo_BigassC.png';

  quickLinks = [
    { label: 'Főoldal', url: '/' },
    { label: 'Termékek', url: '/products' },
    { label: 'Rólunk', url: '/about' },
    { label: 'Kapcsolat', url: '/contact' },
  ];

  legalLinks = [
    { label: 'ÁSZF', url: '/terms' },
    { label: 'Adatvédelmi irányelvek', url: '/privacy' },
    { label: 'Cookie szabályzat', url: '/cookies' },
    { label: 'Szállítási információk', url: '/shipping' },
  ];

  socialLinks = [
    { icon: 'fa-brands fa-facebook', url: 'https://facebook.com', label: 'Facebook' },
    { icon: 'fa-brands fa-instagram', url: 'https://instagram.com', label: 'Instagram' },
    { icon: 'fa-brands fa-twitter', url: 'https://twitter.com', label: 'Twitter' },
    { icon: 'fa-brands fa-youtube', url: 'https://youtube.com', label: 'YouTube' },
  ];

  contactInfo = {
    email: 'info@carcomps.hu',
    phone: '+36 1 234 5678',
    address: 'Budapest, Fő utca 123.',
  };
}
