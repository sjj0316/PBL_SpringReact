import axiosInstance from './axiosConfig';
import useAuthStore from '../store/authStore';

export async function login(credentials) {
  try {
    const response = await axiosInstance.post('/api/auth/login', credentials);
    const { accessToken, refreshToken, user } = response.data;
    
    useAuthStore.getState().setTokens(accessToken, refreshToken);
    useAuthStore.getState().setUser(user);
    
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || '로그인에 실패했습니다.');
  }
}

export async function register(userData) {
  try {
    const response = await axiosInstance.post('/api/auth/register', userData);
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || '회원가입에 실패했습니다.');
  }
}

export async function logout() {
  try {
    await axiosInstance.post('/api/auth/logout');
  } catch (error) {
    console.error('로그아웃 중 오류 발생:', error);
  } finally {
    useAuthStore.getState().logout();
  }
}

export async function refreshToken() {
  try {
    const response = await axiosInstance.post('/api/auth/refresh');
    const { accessToken, refreshToken } = response.data;
    useAuthStore.getState().setTokens(accessToken, refreshToken);
    return response.data;
  } catch (error) {
    useAuthStore.getState().logout();
    throw new Error('토큰 갱신에 실패했습니다.');
  }
}

export function isAuthenticated() {
  return useAuthStore.getState().isAuthenticated;
}

export function getUser() {
  return useAuthStore.getState().user;
} 