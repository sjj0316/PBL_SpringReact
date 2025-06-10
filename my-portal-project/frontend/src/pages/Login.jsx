import { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import {
  Container,
  Box,
  Typography,
  TextField,
  Button,
  Paper,
  Link,
  Alert,
} from '@mui/material';
import { login } from '../api/authApi';
import SocialLoginButtons from '../components/SocialLoginButtons';
import useAuthStore from '../store/authStore';

export default function Login() {
  const navigate = useNavigate();
  const setUser = useAuthStore((state) => state.setUser);
  const [formData, setFormData] = useState({
    username: '',
    password: '',
  });
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const response = await login(formData);
      setUser(response.user);
      navigate('/');
    } catch (err) {
      setError(err.message || '로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.');
    }
  };

  return (
    <Container maxWidth="sm">
      <Box sx={{ mt: 8, mb: 4 }}>
        <Paper elevation={3} sx={{ p: 4 }}>
          <Typography variant="h4" component="h1" align="center" gutterBottom>
            로그인
          </Typography>
          
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
            <TextField
              margin="normal"
              required
              fullWidth
              id="username"
              label="아이디"
              name="username"
              autoComplete="username"
              autoFocus
              value={formData.username}
              onChange={handleChange}
            />
            <TextField
              margin="normal"
              required
              fullWidth
              name="password"
              label="비밀번호"
              type="password"
              id="password"
              autoComplete="current-password"
              value={formData.password}
              onChange={handleChange}
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
            >
              로그인
            </Button>
          </Box>

          <SocialLoginButtons />

          <Box sx={{ mt: 3, textAlign: 'center' }}>
            <Link component={RouterLink} to="/register" variant="body2">
              계정이 없으신가요? 회원가입
            </Link>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
} 