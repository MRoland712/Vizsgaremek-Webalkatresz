import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { GetallpartsService } from '../../services/getallparts.service';
import { GetallmanufacturersService } from '../../services/getallmanufacturers.service';
import { CreatePartService } from '../../services/createpart.service';
import { ManufacturersModel } from '../../models/manufacturers.model';

const VEHICLE_CATEGORIES = [
  { label: 'Személygépkocsi', value: 'Személygépkocsi' },
  { label: 'Motorkerékpár', value: 'Motorkerékpár' },
  { label: 'Teherautó', value: 'Teherautó' },
];

@Component({
  selector: 'app-new-product',
  standalone: true,
  imports: [ReactiveFormsModule, DecimalPipe],
  templateUrl: './new-product.component.html',
  styleUrl: './new-product.component.css',
})
export class NewProductComponent implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private partsService = inject(GetallpartsService);
  private mfService = inject(GetallmanufacturersService);
  private createService = inject(CreatePartService);

  vehicleCategories = VEHICLE_CATEGORIES;
  manufacturers: ManufacturersModel[] = [];
  partCategories: string[] = [];

  isLoading = signal(false);
  isSuccess = signal(false);
  errorMsg = signal<string | null>(null);
  createdPartId = signal<number | null>(null);

  selectedFile = signal<File | null>(null);
  imagePreviewUrl = signal<string | null>(null);
  isUploadingImg = signal(false);
  uploadSuccess = signal(false);

  productForm = this.fb.nonNullable.group({
    vehicleCategory: ['', Validators.required],
    manufacturerId: ['', Validators.required],
    sku: ['', Validators.required],
    name: ['', Validators.required],
    category: ['', Validators.required],
    price: ['', Validators.required],
    stock: [0, [Validators.required, Validators.min(0)]],
    status: ['available', Validators.required],
    isActive: ['true', Validators.required],
  });

  ngOnInit(): void {
    this.loadManufacturers();
    this.loadCategories();

    this.productForm.controls.sku.valueChanges.subscribe((val) => {
      if (val !== val.toUpperCase()) {
        this.productForm.controls.sku.setValue(val.toUpperCase(), { emitEvent: false });
      }
    });
  }

  private loadManufacturers(): void {
    this.mfService.getAllManufacturers().subscribe({
      next: (res) => {
        this.manufacturers = res.Manufacturers ?? [];
      },
      error: (err) => console.error('Gyártók betöltési hiba:', err),
    });
  }

  private loadCategories(): void {
    this.partsService.getAllParts().subscribe({
      next: (res) => {
        const unique = [...new Set((res.parts ?? []).map((p) => p.category).filter(Boolean))];
        this.partCategories = unique.sort();
      },
      error: (err) => console.error('Kategóriák betöltési hiba:', err),
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    this.selectedFile.set(file);
    const reader = new FileReader();
    reader.onload = (e) => this.imagePreviewUrl.set(e.target?.result as string);
    reader.readAsDataURL(file);
  }

  removeImage(): void {
    this.selectedFile.set(null);
    this.imagePreviewUrl.set(null);
  }

  onSubmit(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMsg.set(null);

    const v = this.productForm.getRawValue();
    const sku = v.sku; // SKU alapján keressük meg a létrehozott terméket

    const body = {
      manufacturerId: Number(v.manufacturerId),
      sku: v.sku,
      name: v.name,
      category: v.category,
      price: v.price,
      stock: Number(v.stock),
      status: v.status,
      isActive: v.isActive === 'true',
    };

    this.createService.createPart(body).subscribe({
      next: (res) => {
        console.log('✅ Termék létrehozva (raw response):', res);

        const isOk = res.statusCode === 201 || res.statusCode === 200 || res.success === true;
        if (!isOk) {
          this.isLoading.set(false);
          this.errorMsg.set(res.errors?.join(', ') ?? 'Ismeretlen hiba');
          return;
        }

        const file = this.selectedFile();
        if (!file) {
          // Nincs kép → kész
          this.isLoading.set(false);
          this.isSuccess.set(true);
          return;
        }

        // ⭐ Van kép → SKU alapján keressük meg az id-t az összes termék között
        console.log('🔍 PartId keresése SKU alapján:', sku);
        this.partsService.getAllParts().subscribe({
          next: (partsRes) => {
            const created = partsRes.parts?.find((p) => p.sku?.toUpperCase() === sku.toUpperCase());

            if (!created?.id) {
              console.warn('⚠️ PartId nem található SKU alapján:', sku);
              this.isLoading.set(false);
              this.isSuccess.set(true);
              this.errorMsg.set(
                'A termék létrejött, de a képet nem sikerült hozzárendelni (id nem található).',
              );
              return;
            }

            console.log('✅ PartId megtalálva:', created.id);
            this.uploadImage(created.id, file);
          },
          error: (err) => {
            console.error('❌ getAllParts hiba (id keresés):', err);
            this.isLoading.set(false);
            this.isSuccess.set(true);
            this.errorMsg.set('A termék létrejött, de a képet nem sikerült hozzárendelni.');
          },
        });
      },
      error: (err) => {
        console.error('❌ Hiba:', err);
        this.isLoading.set(false);
        const msg = err.error?.errors?.join(', ') ?? err.error?.message ?? 'Szerver hiba';
        this.errorMsg.set(msg);
      },
    });
  }

  private uploadImage(partId: number, file: File): void {
    this.isUploadingImg.set(true);
    console.log('📤 Képfeltöltés:', partId, file.name);
    this.createService.uploadPartImage(partId, file, true).subscribe({
      next: (res) => {
        console.log('✅ Kép feltöltve:', res);
        this.isUploadingImg.set(false);
        this.isLoading.set(false);
        this.isSuccess.set(true);
        this.uploadSuccess.set(true);
      },
      error: (err) => {
        console.error('❌ Képfeltöltési hiba:', err);
        this.isUploadingImg.set(false);
        this.isLoading.set(false);
        this.isSuccess.set(true);
        this.errorMsg.set('A termék létrejött, de a kép feltöltése sikertelen volt.');
      },
    });
  }

  goBack(): void {
    this.router.navigate(['/admin']);
  }

  resetForm(): void {
    this.productForm.reset({ status: 'available', isActive: 'true', stock: 0 });
    this.isSuccess.set(false);
    this.errorMsg.set(null);
    this.selectedFile.set(null);
    this.imagePreviewUrl.set(null);
    this.uploadSuccess.set(false);
  }

  get mfId() {
    return this.productForm.controls.manufacturerId;
  }
  get skuCtrl() {
    return this.productForm.controls.sku;
  }
  get nameCtrl() {
    return this.productForm.controls.name;
  }
  get catCtrl() {
    return this.productForm.controls.category;
  }
  get priceCtrl() {
    return this.productForm.controls.price;
  }
  get stockCtrl() {
    return this.productForm.controls.stock;
  }

  isInvalid(ctrl: any): boolean {
    return ctrl.touched && ctrl.invalid;
  }
}
