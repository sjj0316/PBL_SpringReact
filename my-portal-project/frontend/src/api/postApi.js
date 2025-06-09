// src/api/postApi.js

const API_BASE = "http://localhost:8081/api/posts";

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

export async function getPosts() {
    const response = await fetch(API_BASE, {
        headers: getAuthHeaders(),
    });
    return handleResponse(response);
}

export async function getPost(id) {
    const response = await fetch(`${API_BASE}/${id}`, {
        headers: getAuthHeaders(),
    });
    return handleResponse(response);
}

export async function createPost({ title, content, category }) {
    const response = await fetch(API_BASE, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify({ title, content, category }),
    });
    return handleResponse(response);
}

export async function updatePost(id, { title, content, category }) {
    const response = await fetch(`${API_BASE}/${id}`, {
        method: "PUT",
        headers: getAuthHeaders(),
        body: JSON.stringify({ title, content, category }),
    });
    return handleResponse(response);
}

export async function deletePost(id) {
    const response = await fetch(`${API_BASE}/${id}`, {
        method: "DELETE",
        headers: getAuthHeaders(),
    });
    return handleResponse(response);
}
