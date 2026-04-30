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
  description = signal('');

  reviews = signal<ReviewModel[]>([]);
  isLoadingReviews = signal(false);

  rating = computed(() => {
    const r = this.reviews();
    if (!r.length) return 0;
    return Math.round((r.reduce((sum, rv) => sum + rv.rating, 0) / r.length) * 10) / 10;
  });
  reviewCount = computed(() => this.reviews().length);

  showReviewForm = signal(false);
  reviewRating = signal(5);
  reviewComment = signal('');
  isSubmittingReview = signal(false);
  reviewSubmitSuccess = signal(false);
  reviewSubmitError = signal<string | null>(null);

  isOutOfStock = computed(() => {
    const p = this.product();
    return !p || !p.isActive || (p.stock ?? 0) <= 0;
  });

  isCooldown = signal(false);
  private cooldownTimer: any;

  readonly PLACEHOLDER = 'assets/placeholder.jpg';

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.loadProductDetails(+params['id']);
    });
  }

  private loadProductDetails(productId: number): void {
    this.isLoading.set(true);
    this.partsService.getAllPartsWithImages().subscribe({
      next: (res) => {
        const found = res.parts.find((p) => p.id === productId);
        if (!found) {
          this.router.navigate(['/']);
          return;
        }

        // ⭐ Fallback ha nincs kép
        const img = found.imageUrl?.trim() ? found.imageUrl : this.PLACEHOLDER;

        this.product.set(found);
        this.images.set([img]);
        this.selectedImage.set(img);
        this.description.set(found.description || '');
        this.isLoading.set(false);

        this.breadcrumbService.setLastCategory(found.category.toLowerCase());
        this.breadcrumbService.updateProductName(productId, found.name);
        this.loadReviews(productId);

        this.manufacturersService.getAllManufacturers().subscribe({
          next: (mfRes) => {
            const mf = mfRes.Manufacturers.find((m) => m.id === found.manufacturerId);
            this.manufacturer.set(mf ?? null);
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
          this.loadReviews(partId);
          setTimeout(() => this.reviewSubmitSuccess.set(false), 3000);
        },
        error: (err) => {
          this.isSubmittingReview.set(false);
          this.reviewSubmitError.set(err.error?.message || 'Hiba történt a beküldés során.');
        },
      });
  }

  setReviewRating(star: number) {
    this.reviewRating.set(star);
  }
  toggleReviewForm() {
    this.showReviewForm.update((v) => !v);
    this.reviewSubmitError.set(null);
    this.reviewSubmitSuccess.set(false);
  }
  selectImage(url: string) {
    this.selectedImage.set(url);
  }
  increaseQuantity() {
    this.quantity.update((q) => q + 1);
  }
  decreaseQuantity() {
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
    const full = Math.floor(this.rating());
    return [0, 1, 2, 3, 4].map((i) => i < full);
  }
  hasHalfStar(): boolean {
    return this.rating() % 1 !== 0;
  }
  getReviewStars(rating: number): boolean[] {
    return [1, 2, 3, 4, 5].map((i) => i <= rating);
  }

  // ⭐ kép hiba kezelő — ha a kép nem töltődik be, placeholder-re vált
  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    if (img.src !== this.PLACEHOLDER) img.src = this.PLACEHOLDER;
  }
}
