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

  // ── Adatok ─────────────────────────────────────────────────
  vehicleCategories = VEHICLE_CATEGORIES;
  manufacturers: ManufacturersModel[] = [];
  partCategories: string[] = [];

  // ── UI állapot ──────────────────────────────────────────────
  isLoading = signal(false);
  isSuccess = signal(false);
  errorMsg = signal<string | null>(null);
  createdPartId = signal<number | null>(null);

  // ── Kép ─────────────────────────────────────────────────────
  selectedFile = signal<File | null>(null);
  imagePreviewUrl = signal<string | null>(null);
  isUploadingImg = signal(false);
  uploadSuccess = signal(false);

  // ── Form ─────────────────────────────────────────────────────
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

    // SKU automatikus nagybetűsítés
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

    // Preview
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
        console.log('✅ Termék létrehozva:', res);

        if (res.statusCode === 201 || res.success === true) {
          // Ha van kép, feltöltjük
          const file = this.selectedFile();
          if (file && res.statusCode) {
            // A backend a 201 Created válaszban sajnos nem adja vissza az id-t
            // ezért a listából kell majd lekérni — egyelőre a képet manuálisan is fel lehet tölteni
            // Ha a backend visszaadja az id-t, azt használjuk
            const partId = (res as any).id ?? (res as any).partId ?? null;
            if (partId) {
              this.uploadImage(partId, file);
            } else {
              this.isLoading.set(false);
              this.isSuccess.set(true);
            }
          } else {
            this.isLoading.set(false);
            this.isSuccess.set(true);
          }
        } else {
          this.isLoading.set(false);
          const errList = res.errors?.join(', ') ?? 'Ismeretlen hiba';
          this.errorMsg.set(errList);
        }
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
        // Termék létrejött, csak a kép nem ment fel
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
