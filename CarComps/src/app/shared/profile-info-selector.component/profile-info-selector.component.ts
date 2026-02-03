import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

type EditField =
  | 'fullname'
  | 'username'
  | 'email'
  | 'password'
  | 'phone'
  | '2fa'
  | 'orders'
  | 'address'
  | null;

interface Order {
  id: number;
  orderDate: string;
  totalPrice: number;
  status: string;
  items: OrderItem[];
}

interface OrderItem {
  productName: string;
  quantity: number;
  price: number;
}

@Component({
  selector: 'app-profile-info-selector',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './profile-info-selector.component.html',
  styleUrl: './profile-info-selector.component.css',
})
export class ProfileInfoSelectorComponent {
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private readonly baseUrl = 'http://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  // Profil adatok
  ProfileDatas = {
    firstname: this.authService.userFirstName(),
    lastname: this.authService.userLastName(),
    username: this.authService.userName(),
    email: this.authService.userEmail(),
    password: '********',
    phone: this.authService.userPhone(),
  };

  // Rendelések
  orders = signal<Order[]>([]);
  isLoadingOrders = signal(false);

  // Szállítási cím
  shippingAddress = signal({
    country: '',
    city: '',
    postalCode: '',
    street: '',
    houseNumber: '',
    taxnumber: '',
    lastname: '',
    firstname: '',
    phone: '',
  });

  // Dialog state
  isDialogOpen = signal(false);
  currentEditField = signal<EditField>(null);
  isSaving = signal(false);
  saveSuccess = signal(false);
  saveError = signal<string | null>(null);

  // Edit form
  editForm = this.fb.nonNullable.group({
    firstname: ['', [Validators.required, Validators.minLength(2)]],
    lastname: ['', [Validators.required, Validators.minLength(2)]],
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    currentPassword: [''],
    newPassword: ['', [Validators.minLength(8)]],
    confirmPassword: [''],
    phone: ['', [Validators.pattern(/^[0-9]{9,15}$/)]],
    country: ['', [Validators.required]],
    city: ['', [Validators.required]],
    postalCode: ['', [Validators.required, Validators.pattern(/^[0-9]{4}$/)]],
    street: ['', [Validators.required]],
    houseNumber: ['', [Validators.required]],
    taxnumber: ['', [Validators.required]],
  });

  openDialog(field: EditField) {
    console.log('🔍 Opening dialog for:', field);
    this.currentEditField.set(field);
    this.isDialogOpen.set(true);
    this.saveSuccess.set(false);
    this.saveError.set(null);

    switch (field) {
      case 'fullname':
        this.editForm.patchValue({
          firstname: this.ProfileDatas.firstname,
          lastname: this.ProfileDatas.lastname,
        });
        break;
      case 'username':
        this.editForm.patchValue({ username: this.ProfileDatas.username });
        break;
      case 'email':
        this.editForm.patchValue({ email: this.ProfileDatas.email });
        break;
      case 'phone':
        this.editForm.patchValue({ phone: this.ProfileDatas.phone });
        break;
      case 'password':
        this.editForm.patchValue({
          currentPassword: '',
          newPassword: '',
          confirmPassword: '',
        });
        break;
      case 'orders':
        this.loadOrders();
        break;
      case 'address':
        this.loadShippingAddress();
        break;
    }
  }

  closeDialog() {
    this.isDialogOpen.set(false);
    this.currentEditField.set(null);
    this.editForm.reset();
  }

  onSave() {
    const field = this.currentEditField();
    if (!field) return;

    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }

    this.isSaving.set(true);
    this.saveError.set(null);

    switch (field) {
      case 'fullname':
        this.updateFullName();
        break;
      case 'username':
        this.updateUsername();
        break;
      case 'email':
        this.updateEmail();
        break;
      case 'password':
        this.updatePassword();
        break;
      case 'phone':
        this.updatePhone();
        break;
      case '2fa':
        this.toggle2FA();
        break;
      case 'address':
        this.updateShippingAddress();
        break;
    }
  }

  private loadOrders() {
    this.isLoadingOrders.set(true);

    // ⭐ MOCK DATA (backend nincs kész)
    setTimeout(() => {
      this.orders.set([
        {
          id: 1001,
          orderDate: '2024-01-15',
          totalPrice: 45000,
          status: 'Delivered',
          items: [
            { productName: 'Michelin gumiabroncs', quantity: 4, price: 40000 },
            { productName: 'Bosch fékbetét', quantity: 1, price: 5000 },
          ],
        },
        {
          id: 1002,
          orderDate: '2024-01-20',
          totalPrice: 12000,
          status: 'Processing',
          items: [{ productName: 'Castrol olaj', quantity: 2, price: 12000 }],
        },
      ]);
      this.isLoadingOrders.set(false);
    }, 500);

    // REAL API (később)
    /*
    this.http.get<{ orders: Order[] }>(`${this.baseUrl}orders/getUserOrders`).subscribe({
      next: (res) => {
        this.orders.set(res.orders);
        this.isLoadingOrders.set(false);
      },
      error: (err) => {
        console.error('❌ Rendelések hiba:', err);
        this.isLoadingOrders.set(false);
        this.orders.set([]);
      },
    });
    */
  }

  private loadShippingAddress() {
    // ⭐ MOCK DATA
    this.shippingAddress.set({
      country: 'Magyarország',
      city: 'Budapest',
      postalCode: '1234',
      street: 'Fő utca',
      houseNumber: '12',
      taxnumber: '1245252',
      lastname: 'Kovács',
      firstname: 'János',
      phone: '06123456789',
    });

    this.editForm.patchValue({
      country: 'Magyarország',
      city: 'Budapest',
      postalCode: '1234',
      street: 'Fő utca',
      houseNumber: '12',
      firstname: 'János',
      lastname: 'Kovács',
      phone: '06123456789',
      taxnumber: '1245252',
    });

    // REAL API (később)
    /*
    this.http.get<any>(`${this.baseUrl}user/getShippingAddress`).subscribe({
      next: (res) => {
        const address = res.address || res;
        this.shippingAddress.set(address);
        this.editForm.patchValue(address);
      },
      error: (err) => {
        console.error('❌ Szállítási cím hiba:', err);
      },
    });
    */
  }

  private updateShippingAddress() {
    const data = {
      country: this.editForm.value.country,
      city: this.editForm.value.city,
      postalCode: this.editForm.value.postalCode,
      street: this.editForm.value.street,
      houseNumber: this.editForm.value.houseNumber,
      firstname: this.editForm.value.firstname,
      lastname: this.editForm.value.lastname,
      phone: this.editForm.value.phone,
      taxnumber: this.editForm.value.taxnumber,
    };

    console.log('💾 Szállítási cím mentése:', data);
    this.shippingAddress.set(data as any);
    this.handleSuccess();

    // REAL API (később)
    /*
    this.http.put(`${this.baseUrl}user/updateShippingAddress`, data).subscribe({
      next: (res) => {
        this.shippingAddress.set(data as any);
        this.handleSuccess();
      },
      error: (err) => this.handleError(err),
    });
    */
  }

  private updateFullName() {
    const data = {
      firstname: this.editForm.value.firstname,
      lastname: this.editForm.value.lastname,
    };

    this.http.put(`${this.baseUrl}user/updateName`, data).subscribe({
      next: (res) => {
        console.log('✅ Teljes név frissítve', res);
        this.ProfileDatas.firstname = data.firstname!;
        this.ProfileDatas.lastname = data.lastname!;
        this.handleSuccess();
      },
      error: (err) => this.handleError(err),
    });
  }

  private updateUsername() {
    const data = { username: this.editForm.value.username };
    this.http.put(`${this.baseUrl}user/updateUsername`, data).subscribe({
      next: (res) => {
        this.ProfileDatas.username = data.username!;
        this.handleSuccess();
      },
      error: (err) => this.handleError(err),
    });
  }

  private updateEmail() {
    const data = { email: this.editForm.value.email };
    this.http.put(`${this.baseUrl}user/updateEmail`, data).subscribe({
      next: (res) => {
        this.ProfileDatas.email = data.email!;
        this.handleSuccess();
      },
      error: (err) => this.handleError(err),
    });
  }

  private updatePassword() {
    const data = {
      currentPassword: this.editForm.value.currentPassword,
      newPassword: this.editForm.value.newPassword,
    };

    if (this.editForm.value.newPassword !== this.editForm.value.confirmPassword) {
      this.saveError.set('Az új jelszavak nem egyeznek!');
      this.isSaving.set(false);
      return;
    }

    this.http.put(`${this.baseUrl}user/updatePassword`, data).subscribe({
      next: (res) => {
        this.handleSuccess();
      },
      error: (err) => this.handleError(err),
    });
  }

  private updatePhone() {
    const data = { phone: this.editForm.value.phone };
    this.http.put(`${this.baseUrl}user/updatePhone`, data).subscribe({
      next: (res) => {
        this.ProfileDatas.phone = data.phone!;
        this.handleSuccess();
      },
      error: (err) => this.handleError(err),
    });
  }

  private toggle2FA() {
    this.http.post(`${this.baseUrl}user/toggle2FA`, {}).subscribe({
      next: (res) => {
        this.handleSuccess();
      },
      error: (err) => this.handleError(err),
    });
  }

  private handleSuccess() {
    this.isSaving.set(false);
    this.saveSuccess.set(true);
    setTimeout(() => {
      this.closeDialog();
    }, 2000);
  }

  private handleError(err: any) {
    console.error('❌ Mentés hiba:', err);
    this.isSaving.set(false);
    this.saveError.set(err.error?.message || 'Hiba történt a mentés során');
  }

  getDialogTitle(): string {
    switch (this.currentEditField()) {
      case 'fullname':
        return 'Teljes név szerkesztése';
      case 'username':
        return 'Felhasználónév szerkesztése';
      case 'email':
        return 'Email cím szerkesztése';
      case 'password':
        return 'Jelszó módosítása';
      case 'phone':
        return 'Telefonszám szerkesztése';
      case '2fa':
        return 'Kétfaktoros hitelesítés';
      case 'orders':
        return 'Rendeléseim';
      case 'address':
        return 'Szállítási cím';
      default:
        return '';
    }
  }

  getOrderStatusClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'completed':
      case 'delivered':
        return 'status-success';
      case 'pending':
      case 'processing':
        return 'status-warning';
      case 'cancelled':
      case 'failed':
        return 'status-error';
      default:
        return 'status-default';
    }
  }
}
