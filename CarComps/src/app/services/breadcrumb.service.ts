import { Injectable } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { BehaviorSubject, filter } from 'rxjs';

export interface Breadcrumb {
  label: string;
  url: string;
}

@Injectable({
  providedIn: 'root',
})
export class BreadcrumbService {
  private breadcrumbsSubject = new BehaviorSubject<Breadcrumb[]>([]);
  public breadcrumbs$ = this.breadcrumbsSubject.asObservable();

  // Utolsó meglátogatott kategória tárolása
  private lastVisitedCategory: string | null = null;

  // Label mapping
  private labelMap: { [key: string]: string } = {
    '': 'Főoldal',
    products: 'Termékek',
    product: 'Termék',
    profile: 'Profilom',
    mygarage: 'Garázsom',
    login: 'Bejelentkezés',
    registration: 'Regisztráció',
    cart: 'Kosár',
    checkout: 'Pénztár',
  };

  // Kategória mapping (KEY = URL slug, VALUE = Magyar név)
  private categoryMap: { [key: string]: string } = {
    tires: 'Gumiabroncsok',
    gumiabroncsok: 'Gumiabroncsok',
    brakes: 'Fékek',
    fékek: 'Fékek',
    oils: 'Olajok',
    olajok: 'Olajok',
    filters: 'Szűrők',
    szűrők: 'Szűrők',
    batteries: 'Akkumulátorok',
    akkumulátorok: 'Akkumulátorok',
    lights: 'Lámpák',
    lámpák: 'Lámpák',
    wipers: 'Ablaktörlők',
    ablaktörlők: 'Ablaktörlők',
    exhaust: 'Kipufogók',
    kipufogók: 'Kipufogók',
    suspension: 'Futómű',
    futómű: 'Futómű',
    engine: 'Motoralkatrészek',
    motoralkatrészek: 'Motoralkatrészek',
  };

  constructor(private router: Router) {
    this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe(() => {
      const breadcrumbs = this.buildBreadcrumbs();
      this.breadcrumbsSubject.next(breadcrumbs);
    });
  }

  /**
   * ⭐ Breadcrumbs építése URL alapján
   */
  private buildBreadcrumbs(): Breadcrumb[] {
    const breadcrumbs: Breadcrumb[] = [];
    const url = this.router.url;

    // ⭐ URL DECODING - Unicode karakterek dekódolása
    const decodedUrl = decodeURIComponent(url);
    const urlSegments = decodedUrl.split('/').filter((segment) => segment);

    console.log('🍞 Building breadcrumbs for URL:', url);
    console.log('  Decoded URL:', decodedUrl);
    console.log('  Segments:', urlSegments);

    // Főoldal mindig az első
    breadcrumbs.push({
      label: 'Főoldal',
      url: '/',
    });

    // Ha üres URL (főoldal), return
    if (urlSegments.length === 0) {
      return breadcrumbs;
    }

    // ⭐ /products/:category
    if (urlSegments[0] === 'products' && urlSegments[1]) {
      const category = urlSegments[1];

      // ⭐ Kategória normalizálása (kisbetű + trim)
      const normalizedCategory = category.toLowerCase().trim();

      // Kategória label keresése mapping-ből
      const categoryLabel = this.categoryMap[normalizedCategory] || this.capitalize(category);

      // Kategória tárolása (eredeti formában)
      this.lastVisitedCategory = normalizedCategory;

      breadcrumbs.push({
        label: categoryLabel,
        url: `/products/${category}`, // Eredeti (encoded) formában
      });

      console.log('  ✅ Kategória mentve:', normalizedCategory);
      console.log('  ✅ Kategória label:', categoryLabel);
    }

    // ⭐ /product/:id
    else if (urlSegments[0] === 'product' && urlSegments[1]) {
      const productId = urlSegments[1];

      // Ha van utolsó kategória, hozzáadjuk
      if (this.lastVisitedCategory) {
        const categoryLabel =
          this.categoryMap[this.lastVisitedCategory] || this.capitalize(this.lastVisitedCategory);

        breadcrumbs.push({
          label: categoryLabel,
          url: `/products/${this.lastVisitedCategory}`,
        });

        console.log('  ✅ Kategória hozzáadva breadcrumb-hoz:', this.lastVisitedCategory);
      }

      // Termék placeholder (később frissül)
      breadcrumbs.push({
        label: `Termék #${productId}`,
        url: `/product/${productId}`,
      });

      console.log('  ✅ Termék placeholder:', productId);
    }

    // ⭐ Egyéb route-ok (/profile, /mygarage, stb.)
    else {
      const segment = urlSegments[0];
      const label = this.labelMap[segment] || this.capitalize(segment);

      breadcrumbs.push({
        label,
        url: `/${segment}`,
      });
    }

    console.log('  Final breadcrumbs:', breadcrumbs);
    return breadcrumbs;
  }

  /**
   * ⭐ Termék név frissítése
   */
  public updateProductName(productId: number, productName: string): void {
    const breadcrumbs = this.breadcrumbsSubject.value;
    const updated = breadcrumbs.map((bc) => {
      if (bc.label.includes(`Termék #${productId}`)) {
        return { ...bc, label: productName };
      }
      return bc;
    });
    this.breadcrumbsSubject.next(updated);
    console.log('  ✅ Termék név frissítve:', productName);
  }

  /**
   * ⭐ Kategória manuális beállítása
   */
  public setLastCategory(category: string): void {
    // ⭐ Normalizálás (kisbetű + trim)
    const normalized = category.toLowerCase().trim();
    this.lastVisitedCategory = normalized;
    console.log('  ✅ Kategória manuálisan beállítva:', normalized);
  }

  /**
   * Helper: Capitalize
   */
  private capitalize(str: string): string {
    if (!str) return '';
    return str.charAt(0).toUpperCase() + str.slice(1);
  }

  /**
   * ⭐ Utolsó kategória lekérése (debug)
   */
  public getLastCategory(): string | null {
    return this.lastVisitedCategory;
  }
}
