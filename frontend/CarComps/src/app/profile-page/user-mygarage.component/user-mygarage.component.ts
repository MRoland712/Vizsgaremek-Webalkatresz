import { Component, computed, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ProfileSidenavComponent } from '../../side-navbar.component/side-navbar.component';
import { CreateUserVehicleService } from '../../services/getusercars.service';
import { GetAllCarsService } from '../../services/getallcars.service';
import { AuthService } from '../../services/auth.service';
import { GetAllCarsModel } from '../../models/cars.model';

interface ModelOption {
  name: string;
  brand: string;
  vehicleId: number;
  yearFrom: number;
  yearTo: number;
}
interface ModelOption {
  name: string;
  brand: string;
  yearFrom: number;
  yearTo: number;
}
interface SavedCar {
  brand: string;
  model: string;
  year: number;
}

@Component({
  selector: 'app-user-mygarage',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ProfileSidenavComponent],
  templateUrl: './user-mygarage.component.html',
  styleUrl: './user-mygarage.component.css',
})
export class UserMygarageComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private createVehicleService = inject(CreateUserVehicleService);
  private getAllCarsService = inject(GetAllCarsService);
  private auth = inject(AuthService);

  // ── Backend adatok ────────────────────────────────────────
  allCars = signal<GetAllCarsModel[]>([]);
  isLoading = signal(true);
  loadError = signal(false);

  // ── Levezetett listák a backend adatból ───────────────────
  brands = computed<string[]>(() => {
    const unique = new Set(this.allCars().map((c) => c.Brand));
    return Array.from(unique).sort();
  });

  filteredModels = computed<ModelOption[]>(() => {
    const brand = this.selectedBrand();
    if (!brand) return [];
    return this.allCars()
      .filter((c) => c.Brand === brand)
      .map((c) => ({
        name: c.Model,
        brand: c.Brand,
        vehicleId: c.id,
        yearFrom: c.YearFrom,
        yearTo: c.YearTo,
      }));
  });

  filteredYears = computed<number[]>(() => {
    const modelName = this.selectedModel(); // ⭐ signal - reaktív
    if (!modelName) return [];
    const car = this.allCars().find(
      (c) => c.Model === modelName && c.Brand === this.selectedBrand(),
    );
    if (!car) return [];
    const years: number[] = [];
    for (let y = car.YearTo; y >= car.YearFrom; y--) years.push(y);
    return years;
  });

  // ── Signals ───────────────────────────────────────────────
  private selectedBrand = signal<string>('');
  private selectedModel = signal<string>('');
  savedCars = signal<SavedCar[]>([]);
  saveSuccess = signal(false);
  saveError = signal<string | null>(null);
  isSaving = signal(false);

  // ── Form ──────────────────────────────────────────────────
  garageForm = new FormGroup({
    brand: new FormControl<string>('', [Validators.required]),
    model: new FormControl<string>('', [Validators.required]),
    year: new FormControl<string>('', [Validators.required]),
  });

  // ── Lifecycle ─────────────────────────────────────────────
  ngOnInit() {
    this.loadCars();
    this.loadSavedCars();

    const b = this.garageForm.get('brand')!.valueChanges.subscribe((brand) => {
      this.selectedBrand.set(brand ?? '');
      this.selectedModel.set('');
      this.garageForm.patchValue({ model: '', year: '' }, { emitEvent: false });
    });

    const m = this.garageForm.get('model')!.valueChanges.subscribe((modelName) => {
      this.selectedModel.set(modelName ?? ''); // ⭐ signal frissítés
      this.garageForm.patchValue({ year: '' }, { emitEvent: false });
    });

    this.destroyRef.onDestroy(() => {
      b.unsubscribe();
      m.unsubscribe();
    });
  }

  // ── Backend: összes autó betöltése ────────────────────────
  loadCars() {
    this.isLoading.set(true);
    this.loadError.set(false);

    this.getAllCarsService.getAllCars().subscribe({
      next: (res) => {
        this.allCars.set(res.cars ?? []);
        this.isLoading.set(false);
        console.log('✅ Cars betöltve:', res.cars?.length);
      },
      error: (err) => {
        console.error('❌ Cars betöltési hiba:', err);
        this.isLoading.set(false);
        this.loadError.set(true);
      },
    });
  }

  // ── Backend: autó mentése (createUserVehicle) ────────────
  saveCar() {
    if (this.garageForm.invalid) {
      this.garageForm.markAllAsTouched();
      return;
    }

    const { brand, model, year } = this.garageForm.getRawValue();

    // vehicleId a filteredModels computed-ból
    const selectedModelObj = this.filteredModels().find((m) => m.name === model);
    if (!selectedModelObj) {
      this.showError('Nem sikerült azonosítani a kiválasztott modellt.');
      return;
    }

    const userId = this.auth.userId() || Number(localStorage.getItem('userId') || '0');
    if (!userId) {
      this.showError('Nem sikerült azonosítani a felhasználót. Kérjük, lépj be újra!');
      return;
    }

    const selectedYear = parseInt(year as string, 10);
    this.isSaving.set(true);

    this.createVehicleService
      .createUserVehicle({
        vehicleType: 'car',
        vehicleId: selectedModelObj.vehicleId,
        year: selectedYear,
        userId: userId,
      })
      .subscribe({
        next: (res) => {
          console.log('✅ createUserVehicle response:', res);
          this.isSaving.set(false);

          const newCar: SavedCar = { brand: brand!, model: model!, year: selectedYear };
          const exists = this.savedCars().some(
            (c) => c.brand === newCar.brand && c.model === newCar.model && c.year === newCar.year,
          );
          if (!exists) {
            const updated = [...this.savedCars(), newCar];
            this.savedCars.set(updated);
            localStorage.setItem('my-garage', JSON.stringify(updated));
          }

          this.garageForm.reset({ brand: '', model: '', year: '' });
          this.selectedBrand.set('');
          this.selectedModel.set('');
          this.showSuccess();
        },
        error: (err) => {
          console.error('❌ createUserVehicle hiba:', err);
          this.isSaving.set(false);
          const msg =
            err.error?.message || err.error?.errors?.[0] || 'Mentés sikertelen, próbáld újra!';
          this.showError(msg);
        },
      });
  }

  // ── LocalStorage ─────────────────────────────────────────
  private loadSavedCars() {
    const raw = localStorage.getItem('my-garage');
    if (raw) {
      try {
        this.savedCars.set(JSON.parse(raw));
      } catch {}
    }
  }

  removeCar(index: number) {
    const updated = this.savedCars().filter((_, i) => i !== index);
    this.savedCars.set(updated);
    localStorage.setItem('my-garage', JSON.stringify(updated));
  }

  // ── Feedback ─────────────────────────────────────────────
  private showSuccess() {
    this.saveSuccess.set(true);
    setTimeout(() => this.saveSuccess.set(false), 3000);
  }
  private showError(msg: string) {
    this.saveError.set(msg);
    setTimeout(() => this.saveError.set(null), 3000);
  }
}
