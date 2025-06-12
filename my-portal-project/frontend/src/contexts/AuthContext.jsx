import { createContext, useContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { login, logout, signup, getToken, getUser, setUser, isAuthenticated } from '../api/authApi';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUserState] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const initAuth = () => {
      const token = getToken();
      if (token) {
        const userData = getUser();
        setUserState(userData);
      }
      setLoading(false);
    };

    initAuth();
  }, []);

  const handleLogin = async (credentials) => {
    try {
      const response = await login(credentials);
      setUserState(response.user);
      navigate('/');
      return response;
    } catch (error) {
      throw error;
    }
  };

  const handleSignup = async (userData) => {
    try {
      const response = await signup(userData);
      return response;
    } catch (error) {
      throw error;
    }
  };

  const handleLogout = () => {
    logout();
    setUserState(null);
    navigate('/login');
  };

  const value = {
    user,
    loading,
    isAuthenticated: isAuthenticated(),
    login: handleLogin,
    logout: handleLogout,
    signup: handleSignup,
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}; 