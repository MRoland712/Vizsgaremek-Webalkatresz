import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { GetAllPartsWithImagesService } from '../../services/getallpartswithimages.service';
import { GetallmanufacturersService } from '../../services/getallmanufacturers.service';

import { MainHeaderComponent } from '../../main-header/main-header.component';
import { MmtContainerComponent } from '../../mmt-container/mmt-container.component';
import { DynamicBreadcrumbsComponent } from '../../shared/dynamic-breadcrumbs.component/dynamic-breadcrumbs.component';
import { BreadcrumbService } from '../../services/breadcrumb.service';
import { ManufacturersModel } from '../../models/manufacturers.model';
import { CartService } from '../../services/cart.service';
import { PartWithImagesModel } from '../../models/getallpartswithimages.model';

interface Review {
  id: number;
  userName: string;
  rating: number;
  comment: string;
  date: string;
}

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [MainHeaderComponent, MmtContainerComponent, DynamicBreadcrumbsComponent, CommonModule],
  templateUrl: './single-product.component.html',
  styleUrl: './single-product.component.css',
})
export class ProductDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private partsService = inject(GetAllPartsWithImagesService);
  private manufacturersService = inject(GetallmanufacturersService);
  private breadcrumbService = inject(BreadcrumbService);
  private cartService = inject(CartService);

  product = signal<PartWithImagesModel | null>(null);
  images = signal<string[]>([]);
  selectedImage = signal<string>('');
  quantity = signal(1);
  isLoading = signal(true);
  manufacturer = signal<ManufacturersModel | null>(null);
  rating = signal(4.5);
  reviewCount = signal(128);
  activeTab = signal<'description' | 'reviews'>('description');

  // Stock=0 vagy isActive=false → elfogyott
  isOutOfStock = computed(() => {
    const p = this.product();
    return !p || !p.isActive || (p.stock ?? 0) <= 0;
  });

  isCooldown = signal(false);
  private cooldownTimer: any;

  reviews = signal<Review[]>([
    {
      id: 1,
      userName: 'Kovács János',
      rating: 5,
      comment: 'Kiváló minőség! Pontosan illik az autómra, gyors szállítás. Mindenkinek ajánlom!',
      date: '2024. január 15.',
    },
    {
      id: 2,
      userName: 'Nagy Eszter',
      rating: 4,
      comment:
        'Jó ár-érték arány. Egyetlen probléma, hogy kicsit később érkezett meg, mint ígérték.',
      date: '2024. január 10.',
    },
    {
      id: 3,
      userName: 'Szabó Péter',
      rating: 5,
      comment:
        'Professzionális csomagolás, tökéletes állapotban érkezett. A szerelő is dicsérte a minőséget.',
      date: '2024. január 8.',
    },
  ]);

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const productId = +params['id'];
      this.loadProductDetails(productId);
    });
  }

  private loadProductDetails(productId: number): void {
    this.isLoading.set(true);

    this.partsService.getAllPartsWithImages().subscribe({
      next: (res) => {
        const foundProduct = res.parts.find((p) => p.id === productId);
        if (!foundProduct) {
          this.router.navigate(['/']);
          return;
        }

        const img = foundProduct.imageUrl || 'assets/placeholder.jpg';
        this.product.set(foundProduct);
        this.images.set([img]);
        this.selectedImage.set(img);
        this.isLoading.set(false);
        this.breadcrumbService.setLastCategory(foundProduct.category.toLowerCase());
        this.breadcrumbService.updateProductName(productId, foundProduct.name);

        this.manufacturersService.getAllManufacturers().subscribe({
          next: (mfRes) => {
            const found = mfRes.Manufacturers.find((m) => m.id === foundProduct.manufacturerId);
            this.manufacturer.set(found ?? null);
          },
          error: () => {},
        });
      },
      error: () => {
        this.isLoading.set(false);
        this.router.navigate(['/']);
      },
    });
  }

  selectImage(imageUrl: string): void {
    this.selectedImage.set(imageUrl);
  }

  increaseQuantity(): void {
    this.quantity.update((q) => q + 1);
  }
  decreaseQuantity(): void {
    this.quantity.update((q) => (q > 1 ? q - 1 : 1));
  }

  addToCart(): void {
    const prod = this.product();
    if (!prod || this.isOutOfStock() || this.isCooldown()) return;

    this.cartService.addToCart({
      id: prod.id,
      name: prod.name,
      price: prod.price,
      quantity: this.quantity(),
      imageUrl: prod.imageUrl,
      sku: prod.sku,
    });

    this.quantity.set(1);
    this.isCooldown.set(true);

    clearTimeout(this.cooldownTimer);
    this.cooldownTimer = setTimeout(() => {
      this.isCooldown.set(false);
    }, 3000);
  }

  getStars(): boolean[] {
    const stars: boolean[] = [];
    const fullStars = Math.floor(this.rating());
    for (let i = 0; i < 5; i++) stars.push(i < fullStars);
    return stars;
  }

  hasHalfStar(): boolean {
    return this.rating() % 1 !== 0;
  }
}
