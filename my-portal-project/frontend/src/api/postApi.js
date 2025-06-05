// src/api/postApi.js

const API_BASE = "http://localhost:8080/api/posts";

function getAuthHeaders() {
    const token = localStorage.getItem("token");
    return {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
    };
}

export async function fetchPosts() {
    const res = await fetch(API_BASE);
    return res.json();
}

export async function fetchPost(id) {
    const res = await fetch(`${API_BASE}/${id}`);
    return res.json();
}

export async function createPost({ title, content }) {
    const res = await fetch(API_BASE, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify({ title, content }),
    });
    return res.text();
}
