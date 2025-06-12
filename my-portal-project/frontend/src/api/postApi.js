// src/api/postApi.js

import axiosInstance from './axiosConfig';

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

// 게시글 목록 조회
export async function getPosts(page = 0, size = 10) {
    const response = await axiosInstance.get(`/api/posts?page=${page}&size=${size}`);
    return response.data;
}

// 게시글 상세 조회
export async function getPost(id) {
    const response = await axiosInstance.get(`/api/posts/${id}`);
    return response.data;
}

// 게시글 작성
export async function createPost(postData, files) {
    const formData = new FormData();
    formData.append('request', new Blob([JSON.stringify(postData)], { type: 'application/json' }));
    if (files) {
        files.forEach(file => formData.append('files', file));
    }
    const response = await axiosInstance.post('/api/posts', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    });
    return response.data;
}

// 게시글 수정
export async function updatePost(id, postData) {
    const response = await axiosInstance.put(`/api/posts/${id}`, postData);
    return response.data;
}

// 게시글 삭제
export async function deletePost(id) {
    await axiosInstance.delete(`/api/posts/${id}`);
}

// 게시글 검색
export async function searchPosts(keyword, page = 0, size = 10) {
    const response = await axiosInstance.get(`/api/posts/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
    return response.data;
}

// 카테고리별 게시글 검색
export async function searchPostsByCategory(categoryId, keyword, page = 0, size = 10) {
    const response = await axiosInstance.get(`/api/posts/search/category/${categoryId}?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
    return response.data;
}

// 작성자별 게시글 조회
export async function getPostsByUsername(username, page = 0, size = 10) {
    const response = await axiosInstance.get(`/api/posts/user/${username}?page=${page}&size=${size}`);
    return response.data;
}

// 최근 게시글 조회
export async function getRecentPosts(page = 0, size = 10) {
    const response = await axiosInstance.get(`/api/posts/recent?page=${page}&size=${size}`);
    return response.data;
}

// 인기 게시글 조회
export async function getPopularPosts(page = 0, size = 10) {
    const response = await axiosInstance.get(`/api/posts/popular?page=${page}&size=${size}`);
    return response.data;
}

// 카테고리별 최근 게시글 조회
export async function getRecentPostsByCategory(categoryName, page = 0, size = 10) {
    const response = await axiosInstance.get(`/api/posts/category/${categoryName}/recent?page=${page}&size=${size}`);
    return response.data;
}

// 게시글 좋아요
export async function addLike(postId) {
    await axiosInstance.post(`/api/posts/${postId}/like`);
}

// 게시글 좋아요 취소
export async function removeLike(postId) {
    await axiosInstance.delete(`/api/posts/${postId}/like`);
}
