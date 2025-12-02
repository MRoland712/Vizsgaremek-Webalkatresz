describe('template spec', () => {
  it('passes', () => {
    cy.visit('http://localhost:4200/')
    cy.get('a[routerlink="/login"]').click();
    cy.get('a.reg-link').click();
    cy.get('#username').click();
    cy.get('#username').type('testeruser');
    cy.get('#firstname').click();
    cy.get('#firstname').type('test');
    cy.get('#phone').click();
    cy.get('#phone').type('+36512324532');
    cy.get('#lastname').click();
    cy.get('#lastname').type('user');
    cy.get('#email').click();
    cy.get('#email').type('testeruser@gmail.com');
    cy.get('#password').click();
    cy.get('#password').type('TesterUser12334!');
    cy.get('#rePassword').click();
    cy.get('#rePassword').type('TesterUser12334!');
    cy.get('button.button').click();
    cy.visit('http://localhost:4200/');
  });

  it('logintest', function() {
       cy.visit('http://localhost:4200/')
       cy.get('i.fa-user').click();
       cy.get('#email').click();
       cy.get('#email').type('testeruser@gmail.com');
       cy.get('#password').click();
       cy.get('#password').type('TestUser12334!');
       cy.get('button.button').click();
       cy.visit('http://localhost:4200/')
  });

  it('asd', function() {
       cy.visit('http://localhost:4200/')
       
  });

  it('loginFail', function() {
       cy.visit('http://localhost:4200')
       cy.get('a[routerlink="/login"]').click();
       cy.get('#email').click();
       cy.get('#email').type('galo.gmail.c');
       cy.get('div.login-control').click();
       cy.get('p.error-message').should('have.class', 'error-message');
  });
})