// cypress/support/commands.ts
// CUSTOM COMMAND: showHoverMenu

declare namespace Cypress {
  interface Chainable {
    /**
     * User dropdown megjelenítése (force visible)
     * @example cy.showUserDropdown()
     */
    showUserDropdown(): Chainable<void>;

    /**
     * Login végigvitele email és jelszó segítségével
     * @example cy.loginFlow('test@example.com', 'Test1234!')
     */
    loginFlow(email: string, password: string): Chainable<void>;
  }
}

// ==========================================
// SHOW USER DROPDOWN - Force visible
// ==========================================
Cypress.Commands.add('showUserDropdown', () => {
  // Profil ikon hover
  cy.get('.user-item').trigger('mouseover', { force: true });

  // Dropdown force visible
  cy.get('.user-dropdown')
    .invoke('css', 'opacity', '1')
    .invoke('css', 'visibility', 'visible')
    .invoke('css', 'display', 'block');

  cy.wait(300);
});

// ==========================================
// LOGIN FLOW - Teljes login folyamat
// ==========================================
Cypress.Commands.add('loginFlow', (email: string, password: string) => {
  // Homepage
  cy.visit('/');

  // User dropdown megjelenítése
  cy.showUserDropdown();

  // Login gomb kattintás
  cy.get('.user-login-btn').click({ force: true });

  // Login form kitöltése
  cy.url().should('include', '/login');
  cy.get('input[type="email"]').type(email);
  cy.get('input[type="password"]').type(password);
  cy.get('button[type="submit"]').click();

  // Várunk az API-ra
  cy.wait(3000);

  // Visszatérés homepage-re
  cy.url().should('eq', 'http://localhost:4200/');
});

// ==========================================
// MEGLÉVŐ COMMANDS (ha vannak)
// ==========================================

// Login (egyszerű)
Cypress.Commands.add('login', (email: string, password: string) => {
  cy.visit('/login');
  cy.get('input[type="email"]').clear().type(email);
  cy.get('input[type="password"]').clear().type(password);
  cy.get('button[type="submit"]').click();
  cy.wait(2000);
});

// Logout
Cypress.Commands.add('logout', () => {
  cy.clearLocalStorage();
  cy.clearCookies();
  cy.visit('/login');
});

// Login with token (mock)
Cypress.Commands.add('loginWithToken', (email: string, userName: string) => {
  cy.window().then((win) => {
    win.localStorage.setItem('jwt', 'mock-jwt-token-cypress-test');
    win.localStorage.setItem('userEmail', email);
    win.localStorage.setItem('userName', userName);
    win.localStorage.setItem('isUserData', 'true');
  });
  cy.visit('/');
});
