describe("포털 통합 흐름 테스트", () => {
    it("게시글 작성 후 댓글까지", () => {
        cy.visit("http://localhost:5173/");
        cy.window().then((win) => {
            win.localStorage.setItem("token", "테스트용JWT");
            win.localStorage.setItem("username", "testuser");
        });

        cy.visit("/write");
        cy.get("input[placeholder='제목']").type("Cypress 테스트 제목");
        cy.get("textarea[placeholder='내용']").type("Cypress 본문입니다.");
        cy.contains("등록").click();

        cy.contains("Cypress 테스트 제목").click();
        cy.get("textarea[placeholder='댓글을 입력하세요']").type("댓글입니다");
        cy.contains("댓글 작성").click();

        cy.contains("댓글입니다").should("exist");
    });
});
