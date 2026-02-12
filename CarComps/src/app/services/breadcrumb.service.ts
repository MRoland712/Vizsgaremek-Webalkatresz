import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, NavigationEnd, Router } from '@angular/router';
import { BehaviorSubject, filter } from 'rxjs';
import { BreadcrumbModel } from '../models/breadcrumbs.model';

@Injectable({
  providedIn: 'root',
})
export class BreadcrumbService {
  private breadcrumbsSubject = new BehaviorSubject<BreadcrumbModel[]>([]);
  public breadcrumbs$ = this.breadcrumbsSubject.asObservable();

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
  private categoryMap: { [key: string]: string } = {
    tires: 'Gumik és felnik',
    brakes: 'Fékrendszer',
    lights: 'Világítás',
    bodys: 'Karosszéria',
    shockAbsorbers: 'Lengéscsillapító',
    chassis: 'Futómű',
    filters: 'Szűrők',
    engineParts: 'Motoralkatrész',
    generator: 'Generátor',
    liquids: 'Folyadékok',
    exhaust: 'Kipufogó',
    other: 'Egyéb',
  };

  constructor(private router: Router) {
    this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe(() => {
      const root = this.router.routerState.snapshot.root;
      const breadcrumbs = this.createBreadcrumbs(root);
      this.breadcrumbsSubject.next(breadcrumbs);
    });
  }
  private createBreadcrumbs(
    route: ActivatedRouteSnapshot,
    url: string = '',
    breadcrumbs: BreadcrumbModel[] = [],
  ): BreadcrumbModel[] {
    if (breadcrumbs.length === 0) {
      breadcrumbs.push({
        label: 'Főoldal',
        url: '/',
      });
    }
    const children: ActivatedRouteSnapshot[] = route.children;
    if (children.length === 0) {
      return breadcrumbs;
    }
    // minden child route
    for (const child of children) {
      const routeURL: string = child.url.map((segment) => segment.path).join('/');
      if (routeURL !== '') {
        url += `/${routeURL}`;
      }

      let label = '';

      // első Route path alapján

      const pathSegment = child.url[0]?.path;
      if (pathSegment) {
        //kategória
        if (route.url[0]?.path === 'products') {
          label = `Termék #${pathSegment}`;
        }

        //alap mapping
        else {
          label = this.labelMap[pathSegment] || this.capitalize(pathSegment);
        }
        breadcrumbs.push({
          label,
          url,
        });
      }
      return this.createBreadcrumbs(child, url, breadcrumbs);
    }
    return breadcrumbs;
  }
  // szó első betűje nagy
  private capitalize(str: string): string {
    return str.charAt(0).toUpperCase() + str.slice(1);
  }

  public updateProductName(productId: number, productName: string): void {
    const breadcrumbs = this.breadcrumbsSubject.value;
    const updated = breadcrumbs.map((bc) => {
      if (bc.label.includes(`Termék #${productId}`)) {
        return { ...bc, label: productName };
      }
      return bc;
    });
    this.breadcrumbsSubject.next(updated);
  }
}
