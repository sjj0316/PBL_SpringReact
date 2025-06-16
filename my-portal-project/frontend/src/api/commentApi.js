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

const API_BASE = '/api/comments';

// 댓글 목록 조회
export const getComments = async (postId, params = {}) => {
    const response = await axiosInstance.get(`${API_BASE}/post/${postId}`, { params });
    return response.data;
};

// 댓글 작성
export const createComment = async (postId, data) => {
    const response = await axiosInstance.post(`${API_BASE}/post/${postId}`, data);
    return response.data;
};

// 댓글 수정
export const updateComment = async (commentId, data) => {
    const response = await axiosInstance.put(`${API_BASE}/${commentId}`, data);
    return response.data;
};

// 댓글 삭제
export const deleteComment = async (commentId) => {
    await axiosInstance.delete(`${API_BASE}/${commentId}`);
};

export const likeComment = async (commentId) => {
    const response = await axiosInstance.post(`${API_BASE}/${commentId}/like`);
    return response.data;
};

export const unlikeComment = async (commentId) => {
    const response = await axiosInstance.delete(`${API_BASE}/${commentId}/like`);
    return response.data;
};

export const reportComment = async (commentId, data) => {
    const response = await axiosInstance.post(`${API_BASE}/${commentId}/report`, data);
    return response.data;
};
