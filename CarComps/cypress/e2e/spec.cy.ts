describe('template spec', () => {
  it('passes', () => {
    cy.visit('http://localhost:4200/');
    cy.get('#email').click();
    cy.get('#email').type('test@gmail.com');
    cy.get('label[for="password"]').click();
    cy.get('#password').click();
    cy.get('#password').type('HUforever1');
    cy.get('span.checkmark').click();
    cy.get('input[type="checkbox"]').check();
    cy.get('button.button').click();
  });
});
