// src/api/commentApi.js

const API_BASE = "http://localhost:8080/api/comments";

// JWT 토큰을 localStorage에서 꺼냄
function getAuthHeaders() {
    const token = localStorage.getItem("token");
    return {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
    };
}

// 댓글 조회
export async function fetchComments(postId) {
    const res = await fetch(`${API_BASE}?postId=${postId}`, {
        method: "GET",
        headers: getAuthHeaders(),
    });
    return res.json();
}

// 댓글 작성
export async function submitComment({ postId, content }) {
    const res = await fetch(API_BASE, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify({ postId, content }),
    });
    return res.text(); // "댓글이 등록되었습니다."
}

// 댓글 수정
export async function updateComment(id, content) {
    const res = await fetch(`${API_BASE}/${id}`, {
        method: "PUT",
        headers: getAuthHeaders(),
        body: JSON.stringify({ content }),
    });
    return res.text();
}

// 댓글 삭제
export async function deleteComment(id) {
    const res = await fetch(`${API_BASE}/${id}`, {
        method: "DELETE",
        headers: getAuthHeaders(),
    });
    return res.text();
}
