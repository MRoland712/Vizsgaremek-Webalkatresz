import { Component, inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile-info-selector',
  imports: [],
  templateUrl: './profile-info-selector.component.html',
  styleUrl: './profile-info-selector.component.css',
})
export class ProfileInfoSelectorComponent {
  authService = inject(AuthService);

  dummyDatas = {
    firstname: 'Doe',
    lastname: 'John',
    username: this.authService.userName(),
    email: this.authService.userEmail(),
    password: 'password123',
    phone: '1234567890',
  };
}
