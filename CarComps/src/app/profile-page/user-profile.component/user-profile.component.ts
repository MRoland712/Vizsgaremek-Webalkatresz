import { Component } from '@angular/core';
import { ProfileSidenavComponent } from '../../side-navbar.component/side-navbar.component';
import { FooterComponent } from '../../footer.component/footer.component';
import { ProfileInfoSelectorComponent } from '../../shared/profile-info-selector.component/profile-info-selector.component';
@Component({
  selector: 'app-user-profile.component',
  imports: [ProfileSidenavComponent, FooterComponent, ProfileInfoSelectorComponent],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css',
})
export class UserProfileComponent {}
