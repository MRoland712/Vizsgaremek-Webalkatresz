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
import { CarMMT } from './mmt.model';
import { GetAllCarsService } from '../services/getallcars.service';
import { GetAllCarsModel } from '../models/cars.model';

@Component({
  selector: 'app-mmt-selector',
  imports: [ReactiveFormsModule],
  templateUrl: './mmt-selector.component.html',
  styleUrl: './mmt-selector.component.css',
})
export class MMTSelectorComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private getAllCarsSvc = inject(GetAllCarsService);

  @Output() dropdownChanged = new EventEmitter<CarMMT | null>();

  // ── Backend adatok ──────────────────────────────────────
  private allCars = signal<GetAllCarsModel[]>([]);
  isLoading = signal(true);
  loadError = signal(false);

  // ── Dinamikus listák a backendből ────────────────────────
  // Egyedi márkák ABC sorrendben
  brands = computed(() => {
    const unique = [...new Set(this.allCars().map((c) => c.Brand))].sort();
    return unique.map((b) => ({ id: b, name: b }));
  });

  // Modellek a kiválasztott márkához
  filteredModels = computed(() => {
    const brand = this.selectedBrand();
    if (!brand) return [];
    const unique = [
      ...new Set(
        this.allCars()
          .filter((c) => c.Brand === brand)
          .map((c) => c.Model),
      ),
    ].sort();
    return unique.map((m) => ({ id: m, name: m }));
  });

  // Évjáratok a kiválasztott márkához+modellhez (YearFrom→YearTo tartomány)
  filteredYears = computed(() => {
    const brand = this.selectedBrand();
    const model = this.selectedModel();
    if (!brand || !model) return [];

    const car = this.allCars().find((c) => c.Brand === brand && c.Model === model);
    if (!car) return [];

    // YearFrom-tól YearTo-ig minden évet felsorolunk, csökkenő sorrendben
    const years: number[] = [];
    for (let y = car.YearTo; y >= car.YearFrom; y--) {
      years.push(y);
    }
    return years;
  });

  // ── Form ─────────────────────────────────────────────────
  mmtForm = new FormGroup({
    brand: new FormControl<string>('', { validators: [Validators.required], nonNullable: true }),
    model: new FormControl<string>('', { validators: [Validators.required], nonNullable: true }),
    year: new FormControl<number | ''>('', {
      validators: [Validators.required],
      nonNullable: true,
    }),
  });

  // ── Signals a cascading logikához ────────────────────────
  private selectedBrand = signal<string>('');
  private selectedModel = signal<string>('');

  // ── Lifecycle ────────────────────────────────────────────
  ngOnInit(): void {
    this.loadCars();
    this.setupFormSubscriptions();
  }

  private loadCars(): void {
    this.isLoading.set(true);
    this.loadError.set(false);

    this.getAllCarsSvc.getAllCars().subscribe({
      next: (res) => {
        // Csak a nem törölt autókat töltjük be
        this.allCars.set(res.cars.filter((c) => !c.isDeleted));
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('❌ getAllCars hiba:', err);
        this.loadError.set(true);
        this.isLoading.set(false);
      },
    });
  }

  // ── Cascading form logika ────────────────────────────────
  private setupFormSubscriptions(): void {
    const brandSub = this.mmtForm.get('brand')!.valueChanges.subscribe((brandId) => {
      this.selectedBrand.set(brandId);
      this.mmtForm.patchValue({ model: '', year: '' }, { emitEvent: false });
      this.selectedModel.set('');
    });

    const modelSub = this.mmtForm.get('model')!.valueChanges.subscribe((modelId) => {
      this.selectedModel.set(modelId);
      this.mmtForm.patchValue({ year: '' }, { emitEvent: false });
    });

    // Évjárat változáskor azonnal emit-elünk
    const yearSub = this.mmtForm.get('year')!.valueChanges.subscribe((year) => {
      if (year && this.mmtForm.valid) {
        this.applyFilter();
      }
    });

    this.destroyRef.onDestroy(() => {
      brandSub.unsubscribe();
      modelSub.unsubscribe();
      yearSub.unsubscribe();
    });
  }

  // ── Szűrés alkalmazása ───────────────────────────────────
  applyFilter(): void {
    if (this.mmtForm.invalid) {
      this.mmtForm.markAllAsTouched();
      return;
    }
    const { brand, model, year } = this.mmtForm.getRawValue();
    this.dropdownChanged.emit({
      brandId: brand,
      brandName: brand,
      modelId: model,
      modelName: model,
      year: year as number,
    });
  }

  // ── Szűrés törlése ───────────────────────────────────────
  clearFilter(): void {
    this.mmtForm.reset({ brand: '', model: '', year: '' });
    this.selectedBrand.set('');
    this.selectedModel.set('');
    this.dropdownChanged.emit(null);
  }

  // ── Getters ──────────────────────────────────────────────
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
