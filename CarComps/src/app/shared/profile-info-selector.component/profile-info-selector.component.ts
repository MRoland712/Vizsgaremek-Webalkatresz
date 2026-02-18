import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { TfaService } from '../../services/tfa.service';
import { TFAResponse } from '../../models/TFA.model';

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
  isTfaActive = signal(false);
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private TFAService = inject(TfaService);
  private readonly baseUrl = 'http://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  ProfileDatas = {
    firstname: this.authService.userFirstName(),
    lastname: this.authService.userLastName(),
    username: this.authService.userName(),
    email: this.authService.userEmail(),
    password: '********',
    phone: this.authService.userPhone(),
  };

  // 2FA State
  tfaData = signal<TFAResponse | null>(null);
  tfaStep = signal<'qr' | 'verify' | 'recovery'>('qr');
  isLoadingTFA = signal(false);
  tfaError = signal<string | null>(null);
  showSecretKey = signal(false);
  verificationCode = signal('');
  isVerifying = signal(false);
  verificationError = signal<string | null>(null);

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
      case '2fa':
        this.initiate2FA();
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
    this.tfaData.set(null);
    this.tfaStep.set('qr');
    this.tfaError.set(null);
    this.showSecretKey.set(false);
    this.verificationCode.set('');
    this.verificationError.set(null);
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
      case 'address':
        this.updateShippingAddress();
        break;
    }
  }

  // ==========================================
  // 2FA METHODS
  // ==========================================

  initiate2FA() {
    this.isLoadingTFA.set(true);
    this.tfaError.set(null);
    this.tfaStep.set('qr');

    this.TFAService.CreateUserTfa({ email: this.ProfileDatas.email }).subscribe({
      next: (response) => {
        console.log('✅ 2FA Response:', response);
        this.tfaData.set(response);
        this.isLoadingTFA.set(false);
      },
      error: (err) => {
        console.error('❌ 2FA Error:', err);
        this.tfaError.set(err.error?.message || 'Hiba történt a 2FA aktiválása során');
        this.isLoadingTFA.set(false);
      },
    });
  }

  openQRCode() {
    // ✅ result objektum (nem tömb)
    const qrUrl = this.tfaData()?.result?.QR;
    if (qrUrl) {
      window.open(qrUrl, '_blank');
    }
  }

  toggleSecretKey() {
    this.showSecretKey.update((v) => !v);
  }

  copyToClipboard(text: string) {
    navigator.clipboard.writeText(text).then(() => {
      alert('Másolva a vágólapra!');
    });
  }

  onVerificationInput(event: Event) {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\D/g, '');

    if (value.length > 6) value = value.slice(0, 6);
    if (value.length > 3) value = value.slice(0, 3) + '-' + value.slice(3);

    this.verificationCode.set(value);
    input.value = value;
  }

  verifyCode() {
    const code = this.verificationCode().replace('-', '');

    if (code.length !== 6) {
      this.verificationError.set('Add meg a teljes 6 számjegyű kódot');
      return;
    }

    this.isVerifying.set(true);
    this.verificationError.set(null);

    this.TFAService.verifyTfaCode(this.ProfileDatas.email, code).subscribe({
      next: (res) => {
        console.log('✅ Code verified:', res);
        this.isVerifying.set(false);
        this.tfaStep.set('recovery');
      },
      error: (err) => {
        console.error('❌ Verification failed:', err);
        this.verificationError.set(err.error?.message || 'Hibás kód. Próbáld újra!');
        this.isVerifying.set(false);
      },
    });
  }

  downloadRecoveryCodes() {
    // ✅ result objektum (nem tömb)
    const codes = this.tfaData()?.result?.recoveryCodes || [];
    const text = `CarComps 2FA Helyreállítási Kódok
Email: ${this.ProfileDatas.email}
Dátum: ${new Date().toLocaleDateString('hu-HU')}

${codes.map((code, i) => `${i + 1}. ${code}`).join('\n')}

FONTOS: Tartsd ezeket biztonságos helyen!
`;
    const blob = new Blob([text], { type: 'text/plain;charset=utf-8' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `carcomps-2fa-recovery-${Date.now()}.txt`;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  copyAllRecoveryCodes() {
    // ✅ result objektum (nem tömb)
    const codes = this.tfaData()?.result?.recoveryCodes || [];
    const text = codes.map((code, i) => `${i + 1}. ${code}`).join('\n');
    navigator.clipboard.writeText(text).then(() => {
      alert('Összes helyreállítási kód másolva!');
    });
  }

  finish2FASetup() {
    this.isTfaActive.set(true);
    this.closeDialog();
  }

  // ==========================================
  // ORDERS & ADDRESS
  // ==========================================

  private loadOrders() {
    this.isLoadingOrders.set(true);
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
  }

  private loadShippingAddress() {
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
    this.shippingAddress.set(data as any);
    this.handleSuccess();
  }

  // ==========================================
  // HTTP METHODS
  // ==========================================

  private updateFullName() {
    const data = {
      firstname: this.editForm.value.firstname,
      lastname: this.editForm.value.lastname,
    };
    this.http.put(`${this.baseUrl}user/updateName`, data).subscribe({
      next: () => {
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
      next: () => {
        this.ProfileDatas.username = data.username!;
        this.handleSuccess();
      },
      error: (err) => this.handleError(err),
    });
  }

  private updateEmail() {
    const data = { email: this.editForm.value.email };
    this.http.put(`${this.baseUrl}user/updateEmail`, data).subscribe({
      next: () => {
        this.ProfileDatas.email = data.email!;
        this.handleSuccess();
      },
      error: (err) => this.handleError(err),
    });
  }

  private updatePassword() {
    if (this.editForm.value.newPassword !== this.editForm.value.confirmPassword) {
      this.saveError.set('Az új jelszavak nem egyeznek!');
      this.isSaving.set(false);
      return;
    }
    const data = {
      currentPassword: this.editForm.value.currentPassword,
      newPassword: this.editForm.value.newPassword,
    };
    this.http.put(`${this.baseUrl}user/updatePassword`, data).subscribe({
      next: () => this.handleSuccess(),
      error: (err) => this.handleError(err),
    });
  }

  private updatePhone() {
    const data = { phone: this.editForm.value.phone };
    this.http.put(`${this.baseUrl}user/updatePhone`, data).subscribe({
      next: () => {
        this.ProfileDatas.phone = data.phone!;
        this.handleSuccess();
      },
      error: (err) => this.handleError(err),
    });
  }

  private handleSuccess() {
    this.isSaving.set(false);
    this.saveSuccess.set(true);
    setTimeout(() => this.closeDialog(), 2000);
  }

  private handleError(err: any) {
    console.error('❌ Mentés hiba:', err);
    this.isSaving.set(false);
    this.saveError.set(err.error?.message || 'Hiba történt a mentés során');
  }

  // ==========================================
  // HELPERS
  // ==========================================

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
        return 'Kétfaktoros hitelesítés beállítása';
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
