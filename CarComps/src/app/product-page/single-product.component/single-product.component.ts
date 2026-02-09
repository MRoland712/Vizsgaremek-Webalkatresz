import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { CommonModule } from '@angular/common';
import { GetallpartsService } from '../../services/getallparts.service';
import { GetallpartimgagesService } from '../../services/getallpartimages.service';
import { PartsModel } from '../../models/parts.model';
import { MainHeaderComponent } from '../../main-header/main-header.component';
import { MmtContainerComponent } from '../../mmt-container/mmt-container.component';
import { GetallmanufacturersService } from '../../services/getallmanufacturers.service';
import { ManufacturersModel } from '../../models/manufacturers.model';

// ⭐ Review interface
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
  imports: [MainHeaderComponent, MmtContainerComponent, CommonModule],
  templateUrl: './single-product.component.html',
  styleUrl: './single-product.component.css',
})
export class ProductDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private partsService = inject(GetallpartsService);
  private partImagesService = inject(GetallpartimgagesService);
  private manufacturersService = inject(GetallmanufacturersService);

  // State
  product = signal<PartsModel | null>(null);
  images = signal<string[]>([]);
  selectedImage = signal<string>('');
  quantity = signal(1);
  isLoading = signal(true);
  manufacturer = signal<ManufacturersModel | null>(null);

  // Rating
  rating = signal(4.5);
  reviewCount = signal(128);

  // ⭐ Tab state
  activeTab = signal<'description' | 'reviews'>('description');

  // ⭐ Reviews (mock data - később API-ból)
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
    {
      id: 4,
      userName: 'Tóth Anna',
      rating: 4,
      comment:
        'Megfelel az elvárásoknak, bár az ár kicsit magasnak tűnt elsőre. Viszont a tartósság kompenzálja.',
      date: '2024. január 5.',
    },
    {
      id: 5,
      userName: 'Kiss Gábor',
      rating: 5,
      comment:
        'Második alkalommal rendelek innen, most is elégedett vagyok. Gyors ügyfélszolgálat!',
      date: '2024. január 3.',
    },
  ]);

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const productId = +params['id'];
      console.log('📦 Product ID:', productId);
      this.loadProductDetails(productId);
    });
  }

  private loadProductDetails(productId: number): void {
    this.isLoading.set(true);

    forkJoin({
      parts: this.partsService.getAllParts(),
      images: this.partImagesService.getAllPartImages(),
      manufacturers: this.manufacturersService.getAllManufacturers(),
    }).subscribe({
      next: ({ parts, images, manufacturers }) => {
        const foundProduct = parts.parts.find((p) => p.id === productId);

        if (!foundProduct) {
          console.error('❌ Termék nem található:', productId);
          this.router.navigate(['/']);
          return;
        }

        const productImages = images.partImages
          .filter((img) => img.partId === productId)
          .sort((a, b) => (a.isPrimary ? -1 : b.isPrimary ? 1 : 0))
          .map((img) => img.url);

        if (productImages.length === 0) {
          productImages.push('assets/placeholder.jpg');
        }

        const foundManufacturer = manufacturers.Manufacturers.find(
          (m) => m.id === foundProduct.manufacturerId,
        );

        this.product.set({
          ...foundProduct,
          imageUrl: productImages[0],
        });
        this.images.set(productImages);
        this.selectedImage.set(productImages[0]);
        this.manufacturer.set(foundManufacturer || null);
        this.isLoading.set(false);

        console.log('✅ Termék betöltve:', foundProduct);
        console.log('✅ Manufacturer:', foundManufacturer);
      },
      error: (err) => {
        console.error('❌ Termék betöltési hiba:', err);
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
    if (!prod) return;

    console.log('🛒 Kosárba helyezés:', {
      product: prod.name,
      quantity: this.quantity(),
      totalPrice: prod.price * this.quantity(),
    });
  }

  goBack(): void {
    this.router.navigate(['/']);
  }

  getStars(): boolean[] {
    const stars: boolean[] = [];
    const fullStars = Math.floor(this.rating());
    for (let i = 0; i < 5; i++) {
      stars.push(i < fullStars);
    }
    return stars;
  }

  hasHalfStar(): boolean {
    return this.rating() % 1 !== 0;
  }
}
