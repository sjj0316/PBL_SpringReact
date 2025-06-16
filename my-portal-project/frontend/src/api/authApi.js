import axiosInstance from './axiosConfig';

const TOKEN_KEY = 'auth_token';
const REFRESH_TOKEN_KEY = 'refresh_token';
const USER_KEY = 'user_info';
const TOKEN_TIMESTAMP_KEY = 'token_timestamp';

export const login = async (credentials) => {
  const response = await axiosInstance.post('/api/auth/login', credentials);
  const { accessToken, refreshToken, user } = response.data;
  setToken(accessToken);
  setRefreshToken(refreshToken);
  setUser(user);
  setTokenTimestamp();
  return response.data;
};

export const signup = async (userData) => {
  const response = await axiosInstance.post('/api/auth/signup', userData);
  return response.data;
};

export const logout = () => {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
  localStorage.removeItem(TOKEN_TIMESTAMP_KEY);
};

export const getToken = () => {
  return localStorage.getItem(TOKEN_KEY);
};

export const getRefreshToken = () => {
  return localStorage.getItem(REFRESH_TOKEN_KEY);
};

export const setToken = (token) => {
  localStorage.setItem(TOKEN_KEY, token);
};

export const setRefreshToken = (token) => {
  localStorage.setItem(REFRESH_TOKEN_KEY, token);
};

export const setTokenTimestamp = () => {
  localStorage.setItem(TOKEN_TIMESTAMP_KEY, new Date().getTime().toString());
};

export const getUser = () => {
  const userStr = localStorage.getItem(USER_KEY);
  return userStr ? JSON.parse(userStr) : null;
};

export const setUser = (user) => {
  localStorage.setItem(USER_KEY, JSON.stringify(user));
};

export const isAuthenticated = () => {
  return !!getToken() && !!getRefreshToken();
};

export const refreshToken = async () => {
  try {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    const response = await axiosInstance.post('/api/auth/refresh', { refreshToken });
    const { accessToken, newRefreshToken } = response.data;
    
    setToken(accessToken);
    if (newRefreshToken) {
      setRefreshToken(newRefreshToken);
    }
    setTokenTimestamp();
    
    return accessToken;
  } catch (error) {
    logout();
    throw error;
  }
}; 