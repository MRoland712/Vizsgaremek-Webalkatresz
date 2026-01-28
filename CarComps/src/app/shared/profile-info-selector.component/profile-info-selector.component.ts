import { Component } from '@angular/core';

@Component({
  selector: 'app-profile-info-selector.component',
  imports: [],
  templateUrl: './profile-info-selector.component.html',
  styleUrl: './profile-info-selector.component.css',
})
export class ProfileInfoSelectorComponent {
  dummyDatas = {
    firstname: 'Doe',
    lastname: 'John',
    username: 'johndoe',
    email: 'johndoe@gmail.com',
    password: 'password123',
    phone: '1234567890',
  };
}
