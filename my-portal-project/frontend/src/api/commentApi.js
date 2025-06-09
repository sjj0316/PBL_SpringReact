// src/api/commentApi.js

const API_BASE = "http://localhost:8081/api/comments";

// JWT 토큰을 localStorage에서 꺼냄
function getAuthHeaders() {
    const token = localStorage.getItem("token");
    return {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
    };
}

async function handleResponse(response) {
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '요청 처리 중 오류가 발생했습니다.');
    }
    return response.json();
}

// 댓글 조회
export async function getComments(postId) {
    const response = await fetch(`${API_BASE}?postId=${postId}`, {
        headers: getAuthHeaders(),
    });
    return handleResponse(response);
}

// 댓글 작성
export async function createComment(postId, content) {
    const response = await fetch(API_BASE, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify({ postId, content }),
    });
    return handleResponse(response);
}

// 댓글 수정
export async function updateComment(id, content) {
    const response = await fetch(`${API_BASE}/${id}`, {
        method: "PUT",
        headers: getAuthHeaders(),
        body: JSON.stringify({ content }),
    });
    return handleResponse(response);
}

// 댓글 삭제
export async function deleteComment(id) {
    const response = await fetch(`${API_BASE}/${id}`, {
        method: "DELETE",
        headers: getAuthHeaders(),
    });
    return handleResponse(response);
}
