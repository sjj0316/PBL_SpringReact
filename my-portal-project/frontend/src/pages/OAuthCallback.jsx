import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Box, CircularProgress, Typography } from '@mui/material';
import { useAuth } from '../contexts/AuthContext';

export default function OAuthCallback() {
  const navigate = useNavigate();
  const location = useLocation();
  const { setUser } = useAuth();
  const [error, setError] = useState(null);

  useEffect(() => {
    const handleCallback = async () => {
      try {
        // URL에서 토큰 추출
        const params = new URLSearchParams(location.search);
        const token = params.get('token');
        const error = params.get('error');

        if (error) {
          throw new Error(error);
        }

        if (token) {
          // 토큰 저장
          localStorage.setItem('token', token);
          
          // 사용자 정보 설정
          setUser({ token });
          
          // 메인 페이지로 리다이렉트
          navigate('/');
        } else {
          throw new Error('인증 토큰을 받지 못했습니다.');
        }
      } catch (err) {
        setError(err.message);
        setTimeout(() => {
          navigate('/login');
        }, 3000);
      }
    };

    handleCallback();
  }, [location, navigate, setUser]);

  if (error) {
    return (
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '100vh',
          gap: 2,
        }}
      >
        <Typography color="error" variant="h6">
          {error}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          3초 후 로그인 페이지로 이동합니다...
        </Typography>
      </Box>
    );
  }

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
        gap: 2,
      }}
    >
      <CircularProgress />
      <Typography variant="body1" color="text.secondary">
        로그인 처리 중...
      </Typography>
    </Box>
  );
} 