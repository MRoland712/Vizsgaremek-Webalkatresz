import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

type EditField = 'fullname' | 'username' | 'email' | 'password' | 'phone' | '2fa' | null;

@Component({
  selector: 'app-profile-info-selector',
  imports: [ReactiveFormsModule],
  templateUrl: './profile-info-selector.component.html',
  styleUrl: './profile-info-selector.component.css',
})
export class ProfileInfoSelectorComponent {
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);

  // Profil adatok
  ProfileDatas = {
    firstname: this.authService.userFirstName(),
    lastname: this.authService.userLastName(),
    username: this.authService.userName(),
    email: this.authService.userEmail(),
    password: '********',
    phone: this.authService.userPhone(),
  };

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
  });

  /**
   * Dialog megnyitása
   */
  openDialog(field: EditField) {
    this.currentEditField.set(field);
    this.isDialogOpen.set(true);
    this.saveSuccess.set(false);
    this.saveError.set(null);

    // Form értékek beállítása
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
    }
  }

  /**
   * Dialog bezárása
   */
  closeDialog() {
    this.isDialogOpen.set(false);
    this.currentEditField.set(null);
    this.editForm.reset();
  }

  /**
   * Mentés
   */
  onSave() {
    const field = this.currentEditField();
    if (!field) return;

    // Validáció
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }

    this.isSaving.set(true);
    this.saveError.set(null);

    // API hívás field alapján
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
    }
  }

  /**
   * API: Teljes név frissítése
   */
  private updateFullName() {
    const data = {
      firstname: this.editForm.value.firstname,
      lastname: this.editForm.value.lastname,
    };

    this.http
      .put('http://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/user/updateName', data)
      .subscribe({
        next: (res) => {
          console.log('✅ Teljes név frissítve', res);
          this.ProfileDatas.firstname = data.firstname!;
          this.ProfileDatas.lastname = data.lastname!;
          this.handleSuccess();
        },
        error: (err) => this.handleError(err),
      });
  }

  /**
   * API: Username frissítése
   */
  private updateUsername() {
    const data = { username: this.editForm.value.username };

    this.http
      .put('http://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/user/updateUsername', data)
      .subscribe({
        next: (res) => {
          console.log('✅ Username frissítve', res);
          this.ProfileDatas.username = data.username!;
          this.handleSuccess();
        },
        error: (err) => this.handleError(err),
      });
  }

  /**
   * API: Email frissítése
   */
  private updateEmail() {
    const data = { email: this.editForm.value.email };

    this.http
      .put('http://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/user/updateEmail', data)
      .subscribe({
        next: (res) => {
          console.log('✅ Email frissítve', res);
          this.ProfileDatas.email = data.email!;
          this.handleSuccess();
        },
        error: (err) => this.handleError(err),
      });
  }

  /**
   * API: Jelszó frissítése
   */
  private updatePassword() {
    const data = {
      currentPassword: this.editForm.value.currentPassword,
      newPassword: this.editForm.value.newPassword,
    };

    // Jelszó egyezés ellenőrzés
    if (this.editForm.value.newPassword !== this.editForm.value.confirmPassword) {
      this.saveError.set('Az új jelszavak nem egyeznek!');
      this.isSaving.set(false);
      return;
    }

    this.http
      .put('http://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/user/updatePassword', data)
      .subscribe({
        next: (res) => {
          console.log('✅ Jelszó frissítve', res);
          this.handleSuccess();
        },
        error: (err) => this.handleError(err),
      });
  }

  /**
   * API: Telefonszám frissítése
   */
  private updatePhone() {
    const data = { phone: this.editForm.value.phone };

    this.http
      .put('http://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/user/updatePhone', data)
      .subscribe({
        next: (res) => {
          console.log('✅ Telefonszám frissítve', res);
          this.ProfileDatas.phone = data.phone!;
          this.handleSuccess();
        },
        error: (err) => this.handleError(err),
      });
  }

  /**
   * API: 2FA toggle
   */
  private toggle2FA() {
    this.http
      .post('http://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/user/toggle2FA', {})
      .subscribe({
        next: (res) => {
          console.log('✅ 2FA toggled', res);
          this.handleSuccess();
        },
        error: (err) => this.handleError(err),
      });
  }

  /**
   * Sikeres mentés kezelése
   */
  private handleSuccess() {
    this.isSaving.set(false);
    this.saveSuccess.set(true);

    // 2 másodperc múlva bezárás
    setTimeout(() => {
      this.closeDialog();
    }, 2000);
  }

  /**
   * Hiba kezelése
   */
  private handleError(err: any) {
    console.error('❌ Mentés hiba:', err);
    this.isSaving.set(false);
    this.saveError.set(err.error?.message || 'Hiba történt a mentés során');
  }

  /**
   * Dialog cím lekérése
   */
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
      default:
        return '';
    }
  }
}
