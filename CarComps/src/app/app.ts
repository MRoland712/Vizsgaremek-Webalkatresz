import { Component, signal } from '@angular/core';
import { RegistrationComponent } from './registration/registration.component';
import { LoginComponent } from './login/login.component';
import { MainHeaderComponent } from './main-header/main-header.component';

@Component({
  selector: 'app-root',
  imports: [RegistrationComponent, LoginComponent, MainHeaderComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('CarComps');
}
