// src/api/commentApi.js

import axiosInstance from './axiosConfig';
import { getToken } from './authApi';

// JWT 토큰을 가져오는 함수
function getAuthHeaders() {
    const token = getToken();
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
export const createComment = async (postId, content) => {
    const response = await axiosInstance.post(`${API_BASE}/post/${postId}`, { content });
    return response.data;
};

// 댓글 수정
export const updateComment = async (postId, commentId, content) => {
    const response = await axiosInstance.put(`${API_BASE}/post/${postId}/${commentId}`, { content });
    return response.data;
};

// 댓글 삭제
export const deleteComment = async (postId, commentId) => {
    await axiosInstance.delete(`${API_BASE}/post/${postId}/${commentId}`);
};

export const likeComment = async (postId, commentId) => {
    const response = await axiosInstance.post(`${API_BASE}/post/${postId}/${commentId}/like`);
    return response.data;
};

export const unlikeComment = async (postId, commentId) => {
    const response = await axiosInstance.delete(`${API_BASE}/post/${postId}/${commentId}/like`);
    return response.data;
};

export const reportComment = async (postId, commentId, reportData) => {
    const response = await axiosInstance.post(`${API_BASE}/post/${postId}/${commentId}/report`, reportData);
    return response.data;
};
