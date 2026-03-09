import { Component, inject, signal, OnInit, ViewChild } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { TfaService } from '../../services/tfa.service';
import { TFAResponse } from '../../models/TFA.model';
import { GetUserByIdService } from '../../services/getuserbyid.service';
import { GetAddressByIdService } from '../../services/getaddresbyid.service';
import { UpdateUserInfosService } from '../../services/updateuserinfos.service';
import { UpdateAddressInfosService } from '../../services/updateaddressinfos.service';
import { CreateAddressService } from '../../services/createaddress.service';
import { TfaVerifyDialogComponent } from '../../verifications/to-fa.component/to-fa.component';

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
  imports: [ReactiveFormsModule, CommonModule, TfaVerifyDialogComponent],
  templateUrl: './profile-info-selector.component.html',
  styleUrl: './profile-info-selector.component.css',
})
export class ProfileInfoSelectorComponent implements OnInit {
  private auth = inject(AuthService);
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private tfaService = inject(TfaService);
  private getUserByIdSvc = inject(GetUserByIdService);
  private getAddressByIdSvc = inject(GetAddressByIdService);
  private updateUserSvc = inject(UpdateUserInfosService);
  private updateAddressSvc = inject(UpdateAddressInfosService);
  private createAddressSvc = inject(CreateAddressService);
  private readonly baseUrl = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';

  @ViewChild(TfaVerifyDialogComponent) tfaVerifyDialog!: TfaVerifyDialogComponent;

  ProfileDatas = signal({
    id: 0,
    firstname: '',
    lastname: '',
    username: '',
    email: '',
    phone: '',
  });

  addressId = signal<number>(0);
  isTfaActive = signal(false);
  isLoadingUser = signal(false);
  pendingField = signal<'email' | 'password' | null>(null);

  // 2FA
  tfaData = signal<TFAResponse | null>(null);
  tfaStep = signal<'qr' | 'verify' | 'recovery' | 'disable'>('qr');
  isLoadingTFA = signal(false);
  tfaError = signal<string | null>(null);
  showSecretKey = signal(false);
  verificationCode = signal('');
  isVerifying = signal(false);
  verificationError = signal<string | null>(null);
  isDisabling = signal(false);
  disableError = signal<string | null>(null);
  tfaRecordId = signal<number>(0);

  // Rendelések
  orders = signal<Order[]>([]);
  isLoadingOrders = signal(false);

  // Dialog
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
    phone: ['', [Validators.pattern(/^[+0-9]{9,15}$/)]],
    country: ['', Validators.required],
    city: ['', Validators.required],
    postalCode: ['', [Validators.required, Validators.pattern(/^[0-9]{4}$/)]],
    street: ['', Validators.required],
    houseNumber: [''],
    taxnumber: [''],
  });

  ngOnInit() {
    if (localStorage.getItem('tfaActive') === 'true') {
      this.isTfaActive.set(true);
    }

    const email = this.auth.userEmail() || localStorage.getItem('userEmail') || '';
    const userId = this.auth.userId() || Number(localStorage.getItem('userId') || '0');

    this.ProfileDatas.set({
      id: userId,
      firstname: this.auth.userFirstName() || localStorage.getItem('firstName') || '',
      lastname: this.auth.userLastName() || localStorage.getItem('lastName') || '',
      username: this.auth.userName() || localStorage.getItem('userName') || '',
      email: email,
      phone: this.auth.userPhone() || localStorage.getItem('phone') || '',
    });

    if (userId > 0) {
      this.loadUserById(userId);
      return;
    }

    if (email) {
      const token = localStorage.getItem('jwt') ?? '';
      const headers = new HttpHeaders({ token });
      this.http
        .get<any>(`${this.baseUrl}user/getUserByEmail`, { params: { email }, headers })
        .subscribe({
          next: (res) => {
            const u = res?.result || res?.user || res;
            const uid = u?.id;
            if (uid) {
              localStorage.setItem('userId', String(uid));
              this.auth.setUserProfile({
                id: uid,
                firstName: u.firstName || '',
                lastName: u.lastName || '',
                username: u.username || '',
                email: u.email || email,
                phone: u.phone || '',
                role: u.role || '',
              });
              this.ProfileDatas.set({
                id: uid,
                firstname: u.firstName || this.ProfileDatas().firstname,
                lastname: u.lastName || this.ProfileDatas().lastname,
                username: u.username || this.ProfileDatas().username,
                email: u.email || email,
                phone: u.phone || this.ProfileDatas().phone,
              });
            }
          },
          error: () => console.warn('⚠️ getUserByEmail endpoint nem elérhető'),
        });
    }
  }

  private loadUserById(userId: number) {
    this.isLoadingUser.set(true);
    this.getUserByIdSvc.getUserById(userId).subscribe({
      next: (res) => {
        const u = res.result;
        this.auth.setUserProfile({
          id: u.id,
          firstName: u.firstName,
          lastName: u.lastName,
          username: u.username,
          email: u.email,
          phone: u.phone,
          role: u.role,
        });
        this.ProfileDatas.set({
          id: u.id,
          firstname: u.firstName,
          lastname: u.lastName,
          username: u.username,
          email: u.email,
          phone: u.phone,
        });
        this.isLoadingUser.set(false);
      },
      error: (err) => {
        console.error('❌ getUserById hiba:', err);
        this.ProfileDatas.set({
          id: 0,
          firstname: this.auth.userFirstName(),
          lastname: this.auth.userLastName(),
          username: this.auth.userName(),
          email: this.auth.userEmail(),
          phone: this.auth.userPhone(),
        });
        this.isLoadingUser.set(false);
      },
    });
  }

  openDialog(field: EditField) {
    this.currentEditField.set(field);
    this.isDialogOpen.set(true);
    this.saveSuccess.set(false);
    this.saveError.set(null);
    const d = this.ProfileDatas();

    switch (field) {
      case 'fullname':
        this.editForm.patchValue({ firstname: d.firstname, lastname: d.lastname });
        break;
      case 'username':
        this.editForm.patchValue({ username: d.username });
        break;
      case 'email':
        this.editForm.patchValue({ email: d.email });
        break;
      case 'phone':
        this.editForm.patchValue({ phone: d.phone });
        break;
      case 'password':
        this.editForm.patchValue({ currentPassword: '', newPassword: '', confirmPassword: '' });
        break;
      case '2fa':
        this.open2FADialog();
        break;
      case 'orders':
        this.loadOrders();
        break;
      case 'address':
        this.loadAddress();
        break;
    }
  }

  private open2FADialog() {
    if (this.isTfaActive()) {
      this.tfaStep.set('disable');
      this.disableError.set(null);
    } else {
      this.initiate2FA();
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
    this.disableError.set(null);
  }

  onSave() {
    const field = this.currentEditField();
    if (!field) return;

    const relevantControls: Record<string, string[]> = {
      fullname: ['firstname', 'lastname'],
      username: ['username'],
      email: ['email'],
      password: ['newPassword'],
      phone: ['phone'],
      address: ['country', 'city', 'postalCode', 'street'],
    };

    const controls = relevantControls[field] ?? [];
    const isInvalid = controls.some((key) => {
      const ctrl = this.editForm.get(key);
      ctrl?.markAsTouched();
      return ctrl?.invalid;
    });

    if (isInvalid) return;

    // Email vagy jelszó változtatás + TFA aktív → TFA ellenőrzés először
    if ((field === 'email' || field === 'password') && this.isTfaActive()) {
      this.pendingField.set(field as 'email' | 'password');
      setTimeout(() => this.tfaVerifyDialog.open(this.ProfileDatas().email), 50);
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
        this.updateAddress();
        break;
    }
  }

  // TFA verify után folytatja a mentést
  onTFAVerifiedForSave() {
    const field = this.pendingField();
    this.pendingField.set(null);
    this.isSaving.set(true);
    this.saveError.set(null);
    if (field === 'email') this.updateEmail();
    else if (field === 'password') this.updatePassword();
  }

  onTFACancelledForSave() {
    this.pendingField.set(null);
  }

  private loadAddress() {
    const userId = this.ProfileDatas().id || this.auth.userId();
    const d = this.ProfileDatas();
    this.editForm.patchValue({ firstname: d.firstname, lastname: d.lastname, phone: d.phone });
    if (!userId) return;
    this.getAddressByIdSvc.getAddressById(userId).subscribe({
      next: (res) => {
        const a = res.address;
        this.addressId.set(a.id);
        this.editForm.patchValue({
          firstname: a.firstName || d.firstname,
          lastname: a.lastName || d.lastname,
          country: a.country || 'Magyarország',
          city: a.city || '',
          postalCode: a.zipCode || '',
          street: a.street || '',
          taxnumber: a.taxNumber || '',
          phone: d.phone,
        });
      },
      error: (err) => console.error('❌ getAddressById hiba:', err),
    });
  }

  private updateFullName() {
    const email = this.ProfileDatas().email;
    const body = {
      firstName: this.editForm.value.firstname!,
      lastName: this.editForm.value.lastname!,
    };
    this.updateUserSvc.updateUserInfos(email, body).subscribe({
      next: () => {
        this.ProfileDatas.update((d) => ({
          ...d,
          firstname: body.firstName,
          lastname: body.lastName,
        }));
        this.auth.setUserProfile({
          ...this.currentProfile(),
          firstName: body.firstName,
          lastName: body.lastName,
        });
        this.handleSuccess();
      },
      error: (err) => this.handleError(err),
    });
  }

  private updateUsername() {
    const email = this.ProfileDatas().email;
    const body = { username: this.editForm.value.username! };
    this.updateUserSvc.updateUserInfos(email, body).subscribe({
      next: () => {
        this.ProfileDatas.update((d) => ({ ...d, username: body.username }));
        this.handleSuccess();
      },
      error: (err) => this.handleError(err),
    });
  }

  private updateEmail() {
    const oldEmail = this.ProfileDatas().email;
    const body = { email: this.editForm.value.email! };
    this.updateUserSvc.updateUserInfos(oldEmail, body).subscribe({
      next: () => {
        this.ProfileDatas.update((d) => ({ ...d, email: body.email }));
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
    const email = this.ProfileDatas().email;
    const body = {
      currentPassword: this.editForm.value.currentPassword!,
      newPassword: this.editForm.value.newPassword!,
    };
    this.updateUserSvc.updateUserInfos(email, body).subscribe({
      next: () => this.handleSuccess(),
      error: (err) => this.handleError(err),
    });
  }

  private updatePhone() {
    const email = this.ProfileDatas().email;
    const body = { phone: this.editForm.value.phone! };
    this.updateUserSvc.updateUserInfos(email, body).subscribe({
      next: () => {
        this.ProfileDatas.update((d) => ({ ...d, phone: body.phone }));
        this.handleSuccess();
      },
      error: (err) => this.handleError(err),
    });
  }

  private updateAddress() {
    const addrId = this.addressId();
    if (!addrId) {
      this.createAddress();
      return;
    }
    const body = {
      firstName: this.editForm.value.firstname!,
      lastName: this.editForm.value.lastname!,
      country: this.editForm.value.country!,
      city: this.editForm.value.city!,
      zipCode: this.editForm.value.postalCode!,
      street: this.editForm.value.street!,
      taxNumber: this.editForm.value.taxnumber ?? '',
    };
    const newPhone = this.editForm.value.phone || this.ProfileDatas().phone;
    this.updateAddressSvc.updateAddressInfos(addrId, body).subscribe({
      next: () => {
        if (newPhone !== this.ProfileDatas().phone) {
          this.ProfileDatas.update((d) => ({ ...d, phone: newPhone }));
          this.auth.setUserProfile({ ...this.currentProfile(), phone: newPhone });
          this.updateUserSvc
            .updateUserInfos(this.ProfileDatas().email, { phone: newPhone })
            .subscribe();
        }
        localStorage.setItem(
          'shippingAddress',
          JSON.stringify({
            firstname: body.firstName,
            lastname: body.lastName,
            country: body.country,
            city: body.city,
            postalCode: body.zipCode,
            street: body.street,
            phone: newPhone,
          }),
        );
        this.handleSuccess();
      },
      error: (err) => this.handleError(err),
    });
  }

  private createAddress() {
    const userId = this.ProfileDatas().id || this.auth.userId();
    if (!userId) {
      this.saveError.set('Nem sikerült azonosítani a felhasználót!');
      this.isSaving.set(false);
      return;
    }
    const newPhone = this.editForm.value.phone || this.ProfileDatas().phone;
    const body = {
      userId,
      firstName: this.editForm.value.firstname!,
      lastName: this.editForm.value.lastname!,
      company: '',
      taxNumber: this.editForm.value.taxnumber ?? '',
      country: this.editForm.value.country!,
      city: this.editForm.value.city!,
      zipCode: this.editForm.value.postalCode!,
      street: this.editForm.value.street!,
      isDefault: true,
    };
    this.createAddressSvc.createAddress(body).subscribe({
      next: () => {
        this.getAddressByIdSvc.getAddressById(userId).subscribe({
          next: (addrRes) => this.addressId.set(addrRes.address.id),
          error: () => {},
        });
        localStorage.setItem(
          'shippingAddress',
          JSON.stringify({
            firstname: body.firstName,
            lastname: body.lastName,
            country: body.country,
            city: body.city,
            postalCode: body.zipCode,
            street: body.street,
            phone: newPhone,
          }),
        );
        this.handleSuccess();
      },
      error: (err) => this.handleError(err),
    });
  }

  private currentProfile() {
    const d = this.ProfileDatas();
    return {
      id: d.id,
      firstName: d.firstname,
      lastName: d.lastname,
      username: d.username,
      email: d.email,
      phone: d.phone,
    };
  }

  initiate2FA() {
    this.isLoadingTFA.set(true);
    this.tfaError.set(null);
    this.tfaStep.set('qr');
    this.tfaService.CreateUserTfa({ email: this.ProfileDatas().email }).subscribe({
      next: (res) => {
        this.tfaData.set(res);
        this.isLoadingTFA.set(false);
      },
      error: (err) => {
        this.isLoadingTFA.set(false);
        if (err.status === 409 && err.error?.errors?.includes('UserHasActiveTFA')) {
          this.isTfaActive.set(true);
          localStorage.setItem('tfaActive', 'true');
          this.tfaStep.set('disable');
          return;
        }
        this.tfaError.set(err.error?.message || err.error?.errors?.[0] || 'Hiba a 2FA során');
      },
    });
  }

  disableTFA() {
    this.isDisabling.set(true);
    this.disableError.set(null);
    const userId = Number(localStorage.getItem('userId') || '0');
    if (!userId) {
      this.disableError.set('Nem található a felhasználó azonosítója!');
      this.isDisabling.set(false);
      return;
    }
    this.tfaService.getUserTwofa(userId).subscribe({
      next: (res) => {
        const tfaId = res.result.id;
        this.tfaService.disableTfa(tfaId).subscribe({
          next: () => {
            this.isTfaActive.set(false);
            localStorage.removeItem('tfaActive');
            this.isDisabling.set(false);
            this.closeDialog();
          },
          error: (err) => {
            this.disableError.set(
              err.error?.message || err.error?.errors?.[0] || 'Hiba a 2FA kikapcsolásakor',
            );
            this.isDisabling.set(false);
          },
        });
      },
      error: (err) => {
        console.error('❌ getUserTwofa hiba:', err);
        this.disableError.set('Nem sikerült lekérdezni a 2FA adatokat');
        this.isDisabling.set(false);
      },
    });
  }

  toggleSecretKey() {
    this.showSecretKey.update((v) => !v);
  }

  copyToClipboard(text: string) {
    navigator.clipboard.writeText(text).then(() => alert('Másolva!'));
  }

  onVerificationInput(event: Event) {
    const input = event.target as HTMLInputElement;
    let v = input.value.replace(/\D/g, '').slice(0, 6);
    if (v.length > 3) v = v.slice(0, 3) + '-' + v.slice(3);
    this.verificationCode.set(v);
    input.value = v;
  }

  verifyCode() {
    const code = this.verificationCode().replace('-', '');
    if (code.length !== 6) {
      this.verificationError.set('Add meg a teljes 6 számjegyű kódot');
      return;
    }
    this.isVerifying.set(true);
    this.verificationError.set(null);
    this.tfaService.verifyTfaCode(this.ProfileDatas().email, code).subscribe({
      next: (res) => {
        this.isVerifying.set(false);
        if (res.result === 'invalid') {
          this.verificationError.set('Hibás kód! Ellenőrizd az authenticator alkalmazást.');
          return;
        }
        this.tfaStep.set('recovery');
      },
      error: (err) => {
        this.isVerifying.set(false);
        if (err.status === 404 && err.error?.errors?.includes('UserTwofaNotFound')) {
          console.warn('⚠️ TFA validateTFACode 404 - backend bug bypass');
          this.tfaStep.set('recovery');
          return;
        }
        this.verificationError.set(err.error?.message || err.error?.errors?.[0] || 'Hibás kód!');
      },
    });
  }

  downloadRecoveryCodes() {
    const codes = this.tfaData()?.result?.recoveryCodes || [];
    const text = `CarComps 2FA Helyreállítási Kódok\nEmail: ${this.ProfileDatas().email}\n\n${codes.map((c, i) => `${i + 1}. ${c}`).join('\n')}`;
    const blob = new Blob([text], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `carcomps-2fa-${Date.now()}.txt`;
    a.click();
    URL.revokeObjectURL(url);
  }

  copyAllRecoveryCodes() {
    const codes = this.tfaData()?.result?.recoveryCodes || [];
    navigator.clipboard
      .writeText(codes.map((c, i) => `${i + 1}. ${c}`).join('\n'))
      .then(() => alert('Másolva!'));
  }

  finish2FASetup() {
    this.isTfaActive.set(true);
    localStorage.setItem('tfaActive', 'true');
    this.closeDialog();
  }

  private loadOrders() {
    this.isLoadingOrders.set(true);
    setTimeout(() => {
      this.orders.set([
        {
          id: 1001,
          orderDate: '2024-01-15',
          totalPrice: 45000,
          status: 'Delivered',
          items: [{ productName: 'Michelin gumiabroncs', quantity: 4, price: 40000 }],
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

  getDialogTitle(): string {
    const map: Record<string, string> = {
      fullname: 'Teljes név szerkesztése',
      username: 'Felhasználónév szerkesztése',
      email: 'Email cím szerkesztése',
      password: 'Jelszó módosítása',
      phone: 'Telefonszám szerkesztése',
      '2fa': 'Kétfaktoros hitelesítés',
      orders: 'Rendeléseim',
      address: 'Szállítási cím',
    };
    return map[this.currentEditField() ?? ''] ?? '';
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
