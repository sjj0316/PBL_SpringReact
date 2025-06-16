describe('Authentication', () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it('should navigate to login page', () => {
    cy.get('a').contains('로그인').click();
    cy.url().should('include', '/login');
    cy.get('h1').should('contain', '로그인');
  });

  it('should navigate to signup page', () => {
    cy.get('a').contains('회원가입').click();
    cy.url().should('include', '/signup');
    cy.get('h1').should('contain', '회원가입');
  });

  it('should login successfully', () => {
    cy.visit('/login');
    cy.get('input[name="username"]').type('testuser');
    cy.get('input[name="password"]').type('password123');
    cy.get('button').contains('로그인').click();
    cy.url().should('eq', Cypress.config().baseUrl + '/');
    cy.get('a').contains('로그아웃').should('be.visible');
  });

  it('should show error message for invalid login', () => {
    cy.visit('/login');
    cy.get('input[name="username"]').type('wronguser');
    cy.get('input[name="password"]').type('wrongpass');
    cy.get('button').contains('로그인').click();
    cy.get('[role="alert"]').should('contain', '로그인에 실패했습니다');
  });

  it('should signup successfully', () => {
    cy.visit('/signup');
    cy.get('input[name="username"]').type('newuser');
    cy.get('input[name="email"]').type('newuser@example.com');
    cy.get('input[name="password"]').type('password123');
    cy.get('input[name="passwordConfirm"]').type('password123');
    cy.get('button').contains('가입').click();
    cy.url().should('eq', Cypress.config().baseUrl + '/');
    cy.get('a').contains('로그아웃').should('be.visible');
  });

  it('should show validation errors for signup', () => {
    cy.visit('/signup');
    cy.get('button').contains('가입').click();
    cy.get('[role="alert"]').should('contain', '필수 입력 항목입니다');
  });

  it('should logout successfully', () => {
    // 먼저 로그인
    cy.visit('/login');
    cy.get('input[name="username"]').type('testuser');
    cy.get('input[name="password"]').type('password123');
    cy.get('button').contains('로그인').click();

    // 로그아웃
    cy.get('a').contains('로그아웃').click();
    cy.url().should('eq', Cypress.config().baseUrl + '/');
    cy.get('a').contains('로그인').should('be.visible');
  });

  it('should redirect to login for protected routes', () => {
    cy.visit('/posts/new');
    cy.url().should('include', '/login');
    cy.get('[role="alert"]').should('contain', '로그인이 필요합니다');
  });

  it('should maintain session after page refresh', () => {
    // 로그인
    cy.visit('/login');
    cy.get('input[name="username"]').type('testuser');
    cy.get('input[name="password"]').type('password123');
    cy.get('button').contains('로그인').click();

    // 페이지 새로고침
    cy.reload();
    cy.get('a').contains('로그아웃').should('be.visible');
  });
}); 