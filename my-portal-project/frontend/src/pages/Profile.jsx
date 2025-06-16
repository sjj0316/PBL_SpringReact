import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  Container,
  Box,
  Typography,
  TextField,
  Button,
  Paper,
  Grid,
  Divider,
  Avatar,
} from '@mui/material';
import { updateProfile, changePassword } from '../store/slices/authSlice';
import { showToast } from '../store/slices/uiSlice';

const Profile = () => {
  const dispatch = useDispatch();
  const { user, loading } = useSelector((state) => state.auth);
  const [profileData, setProfileData] = useState({
    username: user?.username || '',
    email: user?.email || '',
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProfileData((prev) => ({
      ...prev,
      [name]: value,
    }));
    // Clear error when user starts typing
    if (errors[name]) {
      setErrors((prev) => ({
        ...prev,
        [name]: '',
      }));
    }
  };

  const validateProfileForm = () => {
    const newErrors = {};
    if (!profileData.username) {
      newErrors.username = '아이디를 입력해주세요.';
    }
    if (!profileData.email) {
      newErrors.email = '이메일을 입력해주세요.';
    } else if (!/\S+@\S+\.\S+/.test(profileData.email)) {
      newErrors.email = '유효한 이메일 주소를 입력해주세요.';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const validatePasswordForm = () => {
    const newErrors = {};
    if (!profileData.currentPassword) {
      newErrors.currentPassword = '현재 비밀번호를 입력해주세요.';
    }
    if (!profileData.newPassword) {
      newErrors.newPassword = '새 비밀번호를 입력해주세요.';
    } else if (profileData.newPassword.length < 6) {
      newErrors.newPassword = '비밀번호는 6자 이상이어야 합니다.';
    }
    if (!profileData.confirmPassword) {
      newErrors.confirmPassword = '비밀번호 확인을 입력해주세요.';
    } else if (profileData.newPassword !== profileData.confirmPassword) {
      newErrors.confirmPassword = '비밀번호가 일치하지 않습니다.';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleProfileSubmit = async (e) => {
    e.preventDefault();
    if (!validateProfileForm()) return;

    try {
      await dispatch(
        updateProfile({
          username: profileData.username,
          email: profileData.email,
        })
      ).unwrap();
      dispatch(showToast({ message: '프로필이 업데이트되었습니다.', severity: 'success' }));
    } catch (error) {
      dispatch(
        showToast({
          message: error.message || '프로필 업데이트에 실패했습니다.',
          severity: 'error',
        })
      );
    }
  };

  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    if (!validatePasswordForm()) return;

    try {
      await dispatch(
        changePassword({
          currentPassword: profileData.currentPassword,
          newPassword: profileData.newPassword,
        })
      ).unwrap();
      dispatch(showToast({ message: '비밀번호가 변경되었습니다.', severity: 'success' }));
      // Clear password fields
      setProfileData((prev) => ({
        ...prev,
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
      }));
    } catch (error) {
      dispatch(
        showToast({
          message: error.message || '비밀번호 변경에 실패했습니다.',
          severity: 'error',
        })
      );
    }
  };

  return (
    <Container component="main" maxWidth="md">
      <Box sx={{ mt: 4, mb: 4 }}>
        <Paper elevation={3} sx={{ p: 4 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 4 }}>
            <Avatar
              sx={{ width: 64, height: 64, mr: 2 }}
              alt={user?.username}
              src={user?.avatar}
            />
            <Typography variant="h4" component="h1">
              프로필 설정
            </Typography>
          </Box>

          <Grid container spacing={4}>
            <Grid item xs={12} md={6}>
              <Typography variant="h6" gutterBottom>
                기본 정보
              </Typography>
              <Box component="form" onSubmit={handleProfileSubmit}>
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  id="username"
                  label="아이디"
                  name="username"
                  value={profileData.username}
                  onChange={handleChange}
                  error={!!errors.username}
                  helperText={errors.username}
                />
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  id="email"
                  label="이메일"
                  name="email"
                  type="email"
                  value={profileData.email}
                  onChange={handleChange}
                  error={!!errors.email}
                  helperText={errors.email}
                />
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  sx={{ mt: 3 }}
                  disabled={loading}
                >
                  {loading ? '저장 중...' : '프로필 업데이트'}
                </Button>
              </Box>
            </Grid>

            <Grid item xs={12} md={6}>
              <Typography variant="h6" gutterBottom>
                비밀번호 변경
              </Typography>
              <Box component="form" onSubmit={handlePasswordSubmit}>
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  name="currentPassword"
                  label="현재 비밀번호"
                  type="password"
                  value={profileData.currentPassword}
                  onChange={handleChange}
                  error={!!errors.currentPassword}
                  helperText={errors.currentPassword}
                />
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  name="newPassword"
                  label="새 비밀번호"
                  type="password"
                  value={profileData.newPassword}
                  onChange={handleChange}
                  error={!!errors.newPassword}
                  helperText={errors.newPassword}
                />
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  name="confirmPassword"
                  label="새 비밀번호 확인"
                  type="password"
                  value={profileData.confirmPassword}
                  onChange={handleChange}
                  error={!!errors.confirmPassword}
                  helperText={errors.confirmPassword}
                />
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  sx={{ mt: 3 }}
                  disabled={loading}
                >
                  {loading ? '변경 중...' : '비밀번호 변경'}
                </Button>
              </Box>
            </Grid>
          </Grid>
        </Paper>
      </Box>
    </Container>
  );
};

export default Profile; 