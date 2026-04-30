import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { GetMostPurchasedPartsService } from '../../services/getmostpurchasedparts.service';
import { GetAllPartsWithImagesService } from '../../services/getallpartswithimages.service';
import { CartService } from '../../services/cart.service';
import { PartWithImagesModel } from '../../models/getallpartswithimages.model';

interface TopProduct {
  part: PartWithImagesModel;
  quantity: number;
  rank: number;
  isCooldown: boolean;
}

@Component({
  selector: 'app-getmostpurchasedparts',
  imports: [CommonModule],
  templateUrl: './mostpurchasedparts.component.html',
  styleUrl: './mostpurchasedparts.component.css',
})
export class GetMostPurchasedPartsComponent implements OnInit {
  private mostPurchasedSvc = inject(GetMostPurchasedPartsService);
  private partsWithImagesSvc = inject(GetAllPartsWithImagesService);
  private cartService = inject(CartService);
  private router = inject(Router);

  isLoading = signal(true);
  hasError = signal(false);
  topProducts = signal<TopProduct[]>([]);

  ngOnInit(): void {
    this.load();
  }

  private load(): void {
    this.isLoading.set(true);
    this.hasError.set(false);

    forkJoin({
      mostPurchased: this.mostPurchasedSvc.getMostPurchasedParts(),
      allParts: this.partsWithImagesSvc.getAllPartsWithImages(),
    }).subscribe({
      next: ({ mostPurchased, allParts }) => {
        const top5 = (mostPurchased.result ?? []).slice(0, 5);
        const parts = allParts.parts ?? [];

        const result: TopProduct[] = top5
          .map((mp, i) => {
            const found = parts.find(
              (p) => p.name.toLowerCase().trim() === mp.partName.toLowerCase().trim(),
            );
            if (!found) return null;
            return {
              part: found,
              quantity: mp.quantity,
              rank: i + 1,
              isCooldown: false,
            };
          })
          .filter((x): x is TopProduct => x !== null);

        this.topProducts.set(result);
        this.isLoading.set(false);
      },
      error: () => {
        this.hasError.set(true);
        this.isLoading.set(false);
      },
    });
  }

  navigateToPart(product: PartWithImagesModel): void {
    this.router.navigate(['/product', product.id]);
  }

  addToCart(item: TopProduct): void {
    if (item.isCooldown || item.part.stock <= 0 || !item.part.isActive) return;

    this.cartService.addToCart({
      id: item.part.id,
      name: item.part.name,
      price: Number(item.part.price),
      quantity: 1,
      imageUrl: item.part.imageUrl ?? '',
      brand: '',
      sku: item.part.sku ?? '',
    });

    // Cooldown per kártya
    this.topProducts.update((list) =>
      list.map((p) => (p.part.id === item.part.id ? { ...p, isCooldown: true } : p)),
    );

    setTimeout(() => {
      this.topProducts.update((list) =>
        list.map((p) => (p.part.id === item.part.id ? { ...p, isCooldown: false } : p)),
      );
    }, 3000);
  }

  isOutOfStock(item: TopProduct): boolean {
    return item.part.stock <= 0 || !item.part.isActive;
  }

  retry(): void {
    this.load();
  }
}
