import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GetAllPartsWithImagesService } from '../../services/getallpartswithimages.service';
import { GetallmanufacturersService } from '../../services/getallmanufacturers.service';
import { MainHeaderComponent } from '../../main-header/main-header.component';
import { MmtContainerComponent } from '../../mmt-container/mmt-container.component';
import { DynamicBreadcrumbsComponent } from '../../shared/dynamic-breadcrumbs.component/dynamic-breadcrumbs.component';
import { BreadcrumbService } from '../../services/breadcrumb.service';
import { ManufacturersModel } from '../../models/manufacturers.model';
import { CartService } from '../../services/cart.service';
import { PartWithImagesModel } from '../../models/getallpartswithimages.model';
import { CreateReviewsService } from '../../services/createreviews.service';
import { ReviewModel } from '../../models/createreviews.model';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [
    MainHeaderComponent,
    MmtContainerComponent,
    DynamicBreadcrumbsComponent,
    CommonModule,
    FormsModule,
  ],
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
  private reviewsService = inject(CreateReviewsService);

  // ── Bejelentkezett user adatai ────────────────────────────
  currentUserId = signal(Number(localStorage.getItem('userId') || '0'));
  currentUserName = signal(
    `${localStorage.getItem('firstName') || ''} ${localStorage.getItem('lastName') || ''}`.trim() ||
      localStorage.getItem('userName') ||
      'Te',
  );

  product = signal<PartWithImagesModel | null>(null);
  images = signal<string[]>([]);
  selectedImage = signal<string>('');
  quantity = signal(1);
  isLoading = signal(true);
  manufacturer = signal<ManufacturersModel | null>(null);
  activeTab = signal<'description' | 'reviews'>('description');

  // ── Reviews ──────────────────────────────────────────────
  reviews = signal<ReviewModel[]>([]);
  isLoadingReviews = signal(false);

  rating = computed(() => {
    const r = this.reviews();
    if (!r.length) return 0;
    return Math.round((r.reduce((sum, rv) => sum + rv.rating, 0) / r.length) * 10) / 10;
  });

  reviewCount = computed(() => this.reviews().length);

  // ── Vélemény írása form ───────────────────────────────────
  showReviewForm = signal(false);
  reviewRating = signal(5);
  reviewComment = signal('');
  isSubmittingReview = signal(false);
  reviewSubmitSuccess = signal(false);
  reviewSubmitError = signal<string | null>(null);

  // ── Stock / Cooldown ──────────────────────────────────────
  isOutOfStock = computed(() => {
    const p = this.product();
    return !p || !p.isActive || (p.stock ?? 0) <= 0;
  });

  isCooldown = signal(false);
  private cooldownTimer: any;

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
        this.loadReviews(productId);
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

  private loadReviews(partId: number): void {
    this.isLoadingReviews.set(true);
    this.reviewsService.getReviewsByPartId(partId).subscribe({
      next: (res) => {
        this.reviews.set((res.Reviews ?? []).filter((r) => !r.isDeleted));
        this.isLoadingReviews.set(false);
      },
      error: () => {
        this.reviews.set([]);
        this.isLoadingReviews.set(false);
      },
    });
  }

  // ── Vélemény beküldése ────────────────────────────────────
  submitReview(): void {
    const userId = Number(localStorage.getItem('userId') || '0');
    const partId = this.product()?.id;
    if (!userId || !partId) {
      this.reviewSubmitError.set('Be kell jelentkezned vélemény írásához!');
      return;
    }
    if (!this.reviewComment().trim()) {
      this.reviewSubmitError.set('A vélemény szövege kötelező!');
      return;
    }
    this.isSubmittingReview.set(true);
    this.reviewSubmitError.set(null);
    this.reviewsService
      .createReview({
        userId,
        partId,
        ratingIN: this.reviewRating(),
        commentIN: this.reviewComment().trim(),
      })
      .subscribe({
        next: () => {
          this.isSubmittingReview.set(false);
          this.reviewSubmitSuccess.set(true);
          this.reviewComment.set('');
          this.reviewRating.set(5);
          this.showReviewForm.set(false);
          // Frissítjük a listát
          this.loadReviews(partId);
          setTimeout(() => this.reviewSubmitSuccess.set(false), 3000);
        },
        error: (err) => {
          this.isSubmittingReview.set(false);
          this.reviewSubmitError.set(err.error?.message || 'Hiba történt a beküldés során.');
        },
      });
  }

  setReviewRating(star: number): void {
    this.reviewRating.set(star);
  }

  toggleReviewForm(): void {
    this.showReviewForm.update((v) => !v);
    this.reviewSubmitError.set(null);
    this.reviewSubmitSuccess.set(false);
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
    this.cooldownTimer = setTimeout(() => this.isCooldown.set(false), 3000);
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

  getReviewStars(rating: number): boolean[] {
    return [1, 2, 3, 4, 5].map((i) => i <= rating);
  }
}
