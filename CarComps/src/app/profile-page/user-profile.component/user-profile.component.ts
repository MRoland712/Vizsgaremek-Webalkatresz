import { Component } from '@angular/core';
import { ProfileSidenavComponent } from '../../side-navbar.component/side-navbar.component';
import { FooterComponent } from '../../footer.component/footer.component';

@Component({
  selector: 'app-user-profile.component',
  imports: [ProfileSidenavComponent, FooterComponent],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css',
})
export class UserProfileComponent {}
