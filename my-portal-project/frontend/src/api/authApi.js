import axiosInstance from './axiosConfig';

const TOKEN_KEY = 'auth_token';
const USER_KEY = 'user_info';

export const login = async (credentials) => {
  const response = await axiosInstance.post('/api/auth/login', credentials);
  const { accessToken, refreshToken, user } = response.data;
  setToken(accessToken);
  setUser(user);
  return response.data;
};

export const signup = async (userData) => {
  const response = await axiosInstance.post('/api/auth/signup', userData);
  return response.data;
};

export const logout = () => {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
};

export const getToken = () => {
  return localStorage.getItem(TOKEN_KEY);
};

export const setToken = (token) => {
  localStorage.setItem(TOKEN_KEY, token);
};

export const getUser = () => {
  const userStr = localStorage.getItem(USER_KEY);
  return userStr ? JSON.parse(userStr) : null;
};

export const setUser = (user) => {
  localStorage.setItem(USER_KEY, JSON.stringify(user));
};

export const isAuthenticated = () => {
  return !!getToken();
};

export const refreshToken = async () => {
  const response = await axiosInstance.post('/api/auth/refresh');
  const { accessToken } = response.data;
  setToken(accessToken);
  return accessToken;
}; 