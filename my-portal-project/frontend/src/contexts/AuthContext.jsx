import { createContext, useContext, useState, useEffect } from 'react';
import { isAuthenticated, getToken, getUser, setUser } from '../api/authApi';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUserState] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkAuth = () => {
      if (isAuthenticated()) {
        const userData = getUser();
        if (userData) {
          setUserState(userData);
        } else {
          // 토큰은 있지만 사용자 정보가 없는 경우
          setUserState({ token: getToken() });
        }
      } else {
        setUserState(null);
      }
      setLoading(false);
    };

    checkAuth();

    // 토큰 만료 체크를 위한 인터벌 설정
    const interval = setInterval(checkAuth, 60000); // 1분마다 체크

    return () => clearInterval(interval);
  }, []);

  const updateUser = (userData) => {
    setUser(userData);
    setUserState(userData);
  };

  const value = {
    user,
    setUser: updateUser,
    loading,
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
} 