// src/api/postApi.js

import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const fetchPosts = async (page = 1, search = '') => {
  const response = await axios.get(`${API_URL}/posts`, {
    params: { page, search },
  });
  return response;
};

export const fetchPost = async (id) => {
  const response = await axios.get(`${API_URL}/posts/${id}`);
  return response;
};

export const createPost = async (postData) => {
  const response = await axios.post(`${API_URL}/posts`, postData);
  return response;
};

export const updatePost = async (id, postData) => {
  const response = await axios.put(`${API_URL}/posts/${id}`, postData);
  return response;
};

export const deletePost = async (id) => {
  const response = await axios.delete(`${API_URL}/posts/${id}`);
  return response;
};

export const likePost = async (id) => {
  const response = await axios.post(`${API_URL}/posts/${id}/like`);
  return response;
};

export const unlikePost = async (id) => {
  const response = await axios.post(`${API_URL}/posts/${id}/unlike`);
  return response;
};

// 게시글 검색
export async function searchPosts(keyword, page = 0, size = 10) {
    const response = await axios.get(`${API_URL}/posts/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
    return response;
}

// 카테고리별 게시글 검색
export async function searchPostsByCategory(categoryId, keyword, page = 0, size = 10) {
    const response = await axios.get(`${API_URL}/posts/search/category/${categoryId}?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
    return response;
}

// 작성자별 게시글 조회
export async function getPostsByUsername(username, page = 0, size = 10) {
    const response = await axios.get(`${API_URL}/posts/user/${username}?page=${page}&size=${size}`);
    return response;
}

// 최근 게시글 조회
export async function getRecentPosts(page = 0, size = 10) {
    const response = await axios.get(`${API_URL}/posts/recent?page=${page}&size=${size}`);
    return response;
}

// 인기 게시글 조회
export async function getPopularPosts(page = 0, size = 10) {
    const response = await axios.get(`${API_URL}/posts/popular?page=${page}&size=${size}`);
    return response;
}

// 카테고리별 최근 게시글 조회
export async function getRecentPostsByCategory(categoryName, page = 0, size = 10) {
    const response = await axios.get(`${API_URL}/posts/category/${categoryName}/recent?page=${page}&size=${size}`);
    return response;
}
