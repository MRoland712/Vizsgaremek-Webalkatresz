// cypress/e2e/login-e2e-FINAL.cy.ts
// VÉGLEGES - Custom command használatával

describe('Login E2E - FINAL (custom commands)', () => {
  const email = 'test@example.com';

  const password = 'Test1234!';

  beforeEach(() => {
    cy.clearLocalStorage();
  });

  // ==========================================
  // TELJES LOGIN FLOW - 1 parancs!
  // ==========================================
  it('Teljes login flow - cy.loginFlow()', () => {
    // Egy parancs mindent csinál!
    cy.loginFlow(email, password);

    cy.log('✅ Login flow sikeres');

    // Ellenőrzés: Bejelentkezett-e?
    cy.window().then((win) => {
      expect(win.localStorage.getItem('userEmail')).to.equal(email);
    });

    // Profil dropdown megjelenítése
    cy.showUserDropdown();

    // Email látható
    cy.get('.user-email').should('contain', email);

    cy.log('🎉 TESZT SIKERES!');
  });

  // ==========================================
  // RÉSZLETES LÉPÉSEK
  // ==========================================
  it('Login flow részletesen (step by step)', () => {
    // 1. Homepage
    cy.visit('/');
    cy.log('1️⃣ Homepage');

    // 2. User dropdown megjelenítése
    cy.showUserDropdown();
    cy.log('2️⃣ Dropdown visible');

    // 3. Nincs bejelentkezve ellenőrzés
    cy.get('.user-not-logged-in').should('exist');
    cy.contains('Üdvözlünk!').should('exist');
    cy.log('3️⃣ Nincs bejelentkezve OK');

    // 4. Login gomb
    cy.get('.user-login-btn').click({ force: true });
    cy.log('4️⃣ Login gomb kattintva');

    // 5. Login form
    cy.url().should('include', '/login');
    cy.get('input[type="email"]').type(email);
    cy.get('input[type="password"]').type(password);
    cy.get('button[type="submit"]').click();
    cy.log('5️⃣ Login form elküldve');

    // 6. Várunk
    cy.wait(3000);
    cy.log('6️⃣ Várunk...');

    // 7. Visszatérés
    cy.url().should('eq', 'http://localhost:4200/');
    cy.log('7️⃣ Homepage vissza');

    // 8. LocalStorage ellenőrzés
    cy.window().then((win) => {
      expect(win.localStorage.getItem('jwt')).to.exist;
      expect(win.localStorage.getItem('userEmail')).to.equal(email);
      cy.log('8️⃣ localStorage OK');
    });

    // 9. Profil dropdown
    cy.wait(1000);
    cy.showUserDropdown();
    cy.log('9️⃣ Dropdown bejelentkezve');

    // 10. Bejelentkezett státusz
    cy.get('.user-logged-in').should('exist');
    cy.get('.user-email').should('contain', email);
    cy.log('🔟 Email látható ✅');

    cy.screenshot('final-success');
    cy.log('🎉 TESZT SIKERES!');
  });

  // ==========================================
  // MOCK API
  // ==========================================
  it('Login flow MOCK API-val', () => {
    // Mock
    cy.intercept('POST', '**/api/login', {
      statusCode: 200,
      body: {
        token: 'mock-jwt',
        email: email,
        userName: 'Test User',
      },
    }).as('login');

    // Login flow
    cy.loginFlow(email, password);

    // API hívás történt
    cy.wait('@login');

    // Dropdown
    cy.showUserDropdown();

    // Email check
    cy.get('.user-email').should('contain', email);

    cy.log('✅ MOCK teszt OK');
  });

  // ==========================================
  // KIJELENTKEZÉS
  // ==========================================
  it('Login → Kijelentkezés → Újra nincs bejelentkezve', () => {
    // Mock bejelentkezés
    cy.loginWithToken(email, 'Test User');
    cy.wait(1000);

    // Dropdown
    cy.showUserDropdown();

    // Bejelentkezett
    cy.get('.user-logged-in').should('exist');
    cy.get('.user-email').should('contain', email);
    cy.log('✅ Bejelentkezett OK');

    // Kijelentkezés
    cy.get('.logout-btn').click({ force: true });
    cy.wait(2000);

    // Login oldalra irányít
    cy.url().should('include', '/login');

    // LocalStorage üres
    cy.window().then((win) => {
      expect(win.localStorage.getItem('jwt')).to.be.null;
    });

    cy.log('✅ Kijelentkezés OK');

    // Vissza homepage
    cy.visit('/');

    // Dropdown
    cy.showUserDropdown();

    // Nincs bejelentkezve
    cy.get('.user-not-logged-in').should('exist');

    cy.log('✅ Újra nincs bejelentkezve OK');
  });

  // ==========================================
  // GYORS SMOKE TEST
  // ==========================================
  it('SMOKE - Login működik?', () => {
    cy.loginFlow(email, password);
    cy.showUserDropdown();
    cy.get('.user-email').should('contain', email);
    cy.log('✅ Login működik!');
  });

  it('logintest', function() {
       cy.visit('http://localhost:4200')
       cy.get('.user-item').rightclick();
       
  });
});
