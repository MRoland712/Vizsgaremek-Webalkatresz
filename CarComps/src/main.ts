import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import { provideHttpClient } from '@angular/common/http';
import { environment } from './enviroments/enviroments';

bootstrapApplication(App, appConfig).catch((err) => console.error(err));

// Production módban console logok kikapcsolása
if (environment.production) {
  console.log = () => {};
  console.warn = () => {};
  console.error = () => {};
  console.info = () => {};
  console.debug = () => {};
}

bootstrapApplication(App, appConfig).catch((err) => console.error(err));
