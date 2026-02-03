import {
  Component,
  computed,
  DestroyRef,
  EventEmitter,
  inject,
  OnInit,
  Output,
  signal,
} from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CarBrand, CarMMT, CarModel, CarYear } from './mmt.model';

@Component({
  selector: 'app-mmt-selector',
  imports: [ReactiveFormsModule],
  templateUrl: './mmt-selector.component.html',
  styleUrl: './mmt-selector.component.css',
})
export class MMTSelectorComponent implements OnInit {
  private destroyRef = inject(DestroyRef);

  @Output() dropdownChanged = new EventEmitter<CarMMT | null>();

  // ==========================================
  // ADATOK - MÁRKÁK
  // ==========================================
  brands: CarBrand[] = [
    { id: 'audi', name: 'Audi' },
    { id: 'bmw', name: 'BMW' },
    { id: 'mercedes', name: 'Mercedes-Benz' },
    { id: 'volkswagen', name: 'Volkswagen' },
    { id: 'opel', name: 'Opel' },
    { id: 'ford', name: 'Ford' },
    { id: 'toyota', name: 'Toyota' },
    { id: 'honda', name: 'Honda' },
    { id: 'mazda', name: 'Mazda' },
    { id: 'nissan', name: 'Nissan' },
  ];

  // ==========================================
  // ADATOK - MODELLEK
  // ==========================================
  models: CarModel[] = [
    // Audi modellek
    { id: 'audi-a3', name: 'A3', brandId: 'audi' },
    { id: 'audi-a4', name: 'A4', brandId: 'audi' },
    { id: 'audi-a6', name: 'A6', brandId: 'audi' },
    { id: 'audi-q5', name: 'Q5', brandId: 'audi' },

    // BMW modellek
    { id: 'bmw-3', name: '3-as sorozat', brandId: 'bmw' },
    { id: 'bmw-5', name: '5-ös sorozat', brandId: 'bmw' },
    { id: 'bmw-x3', name: 'X3', brandId: 'bmw' },
    { id: 'bmw-x5', name: 'X5', brandId: 'bmw' },

    // Mercedes modellek
    { id: 'mercedes-c', name: 'C-osztály', brandId: 'mercedes' },
    { id: 'mercedes-e', name: 'E-osztály', brandId: 'mercedes' },
    { id: 'mercedes-glc', name: 'GLC', brandId: 'mercedes' },

    // Volkswagen modellek
    { id: 'vw-golf', name: 'Golf', brandId: 'volkswagen' },
    { id: 'vw-passat', name: 'Passat', brandId: 'volkswagen' },
    { id: 'vw-tiguan', name: 'Tiguan', brandId: 'volkswagen' },
  ];

  // ==========================================
  // ADATOK - ÉVJÁRATOK
  // ==========================================
  years: CarYear[] = [
    // Audi A4 évjáratok
    { year: 2024, modelId: 'audi-a4' },
    { year: 2023, modelId: 'audi-a4' },
    { year: 2022, modelId: 'audi-a4' },
    { year: 2021, modelId: 'audi-a4' },
    { year: 2020, modelId: 'audi-a4' },

    // Audi A3 évjáratok
    { year: 2024, modelId: 'audi-a3' },
    { year: 2023, modelId: 'audi-a3' },
    { year: 2022, modelId: 'audi-a3' },

    // BMW 3-as évjáratok
    { year: 2024, modelId: 'bmw-3' },
    { year: 2023, modelId: 'bmw-3' },
    { year: 2022, modelId: 'bmw-3' },
    { year: 2021, modelId: 'bmw-3' },

    // VW Golf évjáratok
    { year: 2024, modelId: 'vw-golf' },
    { year: 2023, modelId: 'vw-golf' },
    { year: 2022, modelId: 'vw-golf' },
  ];

  // ==========================================
  // REACTIVE FORM
  // ==========================================
  mmtForm = new FormGroup({
    brand: new FormControl<string>('', {
      validators: [Validators.required],
      nonNullable: true,
    }),
    model: new FormControl<string>('', {
      validators: [Validators.required],
      nonNullable: true,
    }),
    year: new FormControl<number | ''>('', {
      validators: [Validators.required],
      nonNullable: true,
    }),
  });

  // ==========================================
  // SIGNALS
  // ==========================================
  private selectedBrand = signal<string>('');
  private selectedModel = signal<string>('');

  // ==========================================
  // COMPUTED SIGNALS
  // ==========================================

  // Filtered models - automatikusan újraszámol ha selectedBrand változik
  filteredModels = computed(() => {
    const brandId = this.selectedBrand();
    if (!brandId) return [];
    return this.models.filter((model) => model.brandId === brandId);
  });

  // Filtered years - automatikusan újraszámol ha selectedModel változik
  filteredYears = computed(() => {
    const modelId = this.selectedModel();
    if (!modelId) return [];

    const yearsObject = this.years.filter((y) => y.modelId === modelId);
    return yearsObject.map((y) => y.year).sort((a, b) => b - a); // legújabb előre
  });

  // ==========================================
  // LIFECYCLE
  // ==========================================
  ngOnInit(): void {
    this.setupFormSubscriptions();
  }

  // ==========================================
  // FORM SUBSCRIPTIONS - CASCADING LOGIC
  // ==========================================
  private setupFormSubscriptions(): void {
    // 1. MÁRKA változás figyelése
    const brandSubscription = this.mmtForm.get('brand')!.valueChanges.subscribe({
      next: (brandId) => {
        // ✅ Signal frissítése
        this.selectedBrand.set(brandId);

        // Modell és évjárat reset
        this.mmtForm.patchValue(
          {
            model: '',
            year: '',
          },
          { emitEvent: false }
        );

        // Model signal reset
        this.selectedModel.set('');
      },
    });

    // 2. MODELL változás figyelése
    const modelSubscription = this.mmtForm.get('model')!.valueChanges.subscribe({
      next: (modelId) => {
        // ✅ Signal frissítése
        this.selectedModel.set(modelId);

        // Évjárat reset
        this.mmtForm.patchValue(
          {
            year: '',
          },
          { emitEvent: false }
        );
      },
    });

    // 3. ÉVJÁRAT változás figyelése
    const yearSubscription = this.mmtForm.get('year')!.valueChanges.subscribe({
      next: (year) => {},
    });

    // 4. Cleanup
    this.destroyRef.onDestroy(() => {
      brandSubscription.unsubscribe();
      modelSubscription.unsubscribe();
      yearSubscription.unsubscribe();
    });
  }

  // ==========================================
  // HELPER METÓDUSOK
  // ==========================================

  // Kiválasztott márka neve
  getSelectedBrandName(): string {
    const brandId = this.mmtForm.get('brand')?.value;
    if (!brandId) return '';

    const brand = this.brands.find((b) => b.id === brandId);
    return brand ? brand.name : '';
  }

  // Kiválasztott modell neve
  getSelectedModelName(): string {
    const modelId = this.mmtForm.get('model')?.value;
    if (!modelId) return '';

    const model = this.models.find((m) => m.id === modelId);
    return model ? model.name : '';
  }

  // ==========================================
  // SZŰRÉS ALKALMAZÁSA
  // ==========================================
  applyFilter(): void {
    // Validáció ellenőrzés
    if (this.mmtForm.invalid) {
      this.mmtForm.markAllAsTouched();
      return;
    }

    const formValue = this.mmtForm.getRawValue();

    const filter: CarMMT = {
      brandId: formValue.brand,
      brandName: this.getSelectedBrandName(),
      modelId: formValue.model,
      modelName: this.getSelectedModelName(),
      year: formValue.year as number,
    };

    // Event emit a parent komponensnek
    this.dropdownChanged.emit(filter);
  }

  // ==========================================
  // SZŰRÉS TÖRLÉSE
  // ==========================================
  clearFilter(): void {
    // Form reset
    this.mmtForm.reset({
      brand: '',
      model: '',
      year: '',
    });

    // Signals reset
    this.selectedBrand.set('');
    this.selectedModel.set('');

    // Event emit - null = nincs szűrés
    this.dropdownChanged.emit(null);
  }

  // ==========================================
  // FORM CONTROL GETTERS
  // ==========================================
  get brandControl() {
    return this.mmtForm.get('brand');
  }

  get modelControl() {
    return this.mmtForm.get('model');
  }

  get yearControl() {
    return this.mmtForm.get('year');
  }
}
