import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    // Base URL beállítása
    baseUrl: 'http://localhost:4200',

    // Viewport méret
    viewportWidth: 1920,
    viewportHeight: 1080,

    // Videó rögzítés letiltása (gyorsabb teszt)
    video: false,

    // Screenshot csak hiba esetén
    screenshotOnRunFailure: true,

    // Timeout-ok növelése
    defaultCommandTimeout: 10000,
    requestTimeout: 10000,
    responseTimeout: 10000,

    setupNodeEvents(on, config) {
      // implement node event listeners here
    },

    // Spec fájlok helye
    specPattern: 'cypress/e2e/**/*.cy.{js,jsx,ts,tsx}',

    // Support file
    supportFile: 'cypress/support/e2e.ts',
  },

  // Chrome headless módban
  chromeWebSecurity: false,
});
