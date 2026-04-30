import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import { environment } from './enviroments/enviroments';

// ⭐ ELŐSZÖR kapcsoljuk ki a console logokat
if (environment.production) {
  console.log = () => {};
  console.warn = () => {};
  console.info = () => {};
  console.debug = () => {};
}

// ⭐ AZTÁN indítjuk az appot — csak egyszer!
bootstrapApplication(App, appConfig).catch((err) => console.error(err));
