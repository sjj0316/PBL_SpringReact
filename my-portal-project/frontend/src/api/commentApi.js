// src/api/commentApi.js

import axiosInstance from './axiosConfig';

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

// 댓글 목록 조회
export const getComments = async (postId) => {
    const response = await axiosInstance.get(`/api/posts/${postId}/comments`);
    return response.data;
};

// 댓글 작성
export const createComment = async (postId, commentData) => {
    const response = await axiosInstance.post(`/api/posts/${postId}/comments`, commentData);
    return response.data;
};

// 댓글 수정
export const updateComment = async (commentId, commentData) => {
    const response = await axiosInstance.put(`/api/comments/${commentId}`, commentData);
    return response.data;
};

// 댓글 삭제
export const deleteComment = async (commentId) => {
    const response = await axiosInstance.delete(`/api/comments/${commentId}`);
    return response.data;
};
