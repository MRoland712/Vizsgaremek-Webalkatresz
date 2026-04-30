describe('template spec', () => {
  it('passes', () => {
    cy.visit('https://www.carcomps.hu/#/login')
    
    // Login form
    cy.get('#email').type('vinrar712@gmail.com')
    cy.get('#password').type('#HUforever7125')
    cy.get('button.auth-btn').click()
    
    // OTP
    cy.get('#digit6').type('2')
    cy.get('button.verify-btn').click({ force: true })
    
    // Ellenőrzés — vissza a főoldalra és hover
    cy.visit('https://www.carcomps.hu')
    cy.get('.user-dropdown').invoke('css', 'opacity', '1')
    cy.get('.user-dropdown').invoke('css', 'visibility', 'visible')
    cy.get('p.user-email').should('have.text', 'vinrar712@gmail.com')
  });

  it('registrationShouldPass', function() {
       describe('Regisztráció', () => {
         it('Sikeres regisztráció', () => {
           describe('Regisztráció', () => {
             it('Sikeres regisztráció', () => {
               cy.visit('https://www.carcomps.hu/#/registration')
                      
               // Felhasználónév
               cy.get('#username').type('tesztfelhasznalo')
                      
               // Email — egyedi email kell, timestamp-pel
               const email = `teszt${Date.now()}@gmail.com`
               cy.get('#email').type(email)
                      
               // Név
               cy.get('#firstname').type('Teszt')
               cy.get('#lastname').type('Elek')
                      
               // Telefon
               cy.get('#phone').type('+36201234567')
                      
               // Jelszó
               cy.get('#password').type('Teszt@1234')
               cy.get('#rePassword').type('Teszt@1234')
                      
               // ÁSZF elfogadása
               cy.get('input[formControlName="acceptAszf"]').check({ force: true })
                      
               // Regisztráció gomb
               cy.get('button.auth-btn').should('not.be.disabled').click()
                      
               // Sikeres regisztráció után átirányít (login vagy főoldal)
               
             })
           })
             })
         })
       })
  });
})