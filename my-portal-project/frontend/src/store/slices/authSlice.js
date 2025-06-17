import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import {
  login as loginApi,
  signup as signupApi,
  logout as logoutApi,
  refreshToken as refreshTokenApi,
  // getCurrentUser as getCurrentUserApi,
  // updateProfile as updateProfileApi,
  // changePassword as changePasswordApi,
} from '../../api/authApi';

const initialState = {
  user: null,
  token: localStorage.getItem('token'),
  loading: false,
  error: null,
};

export const login = createAsyncThunk(
  'auth/login',
  async (credentials) => {
    const response = await loginApi(credentials);
    const { token, user } = response.data;
    localStorage.setItem('token', token);
    return { token, user };
  }
);

export const signup = createAsyncThunk(
  'auth/signup',
  async (userData) => {
    const response = await signupApi(userData);
    return response.data;
  }
);

export const logout = createAsyncThunk(
  'auth/logout',
  async () => {
    await logoutApi();
    localStorage.removeItem('token');
  }
);

export const refreshToken = createAsyncThunk(
  'auth/refreshToken',
  async () => {
    const response = await refreshTokenApi();
    const { token } = response.data;
    localStorage.setItem('token', token);
    return token;
  }
);

// export const getCurrentUser = createAsyncThunk(
//   'auth/getCurrentUser',
//   async () => {
//     const response = await getCurrentUserApi();
//     return response.data;
//   }
// );

// export const updateProfile = createAsyncThunk(
//   'auth/updateProfile',
//   async (userData) => {
//     const response = await updateProfileApi(userData);
//     return response.data;
//   }
// );

// export const changePassword = createAsyncThunk(
//   'auth/changePassword',
//   async (passwordData) => {
//     await changePasswordApi(passwordData);
//   }
// );

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload.user;
        state.token = action.payload.token;
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(signup.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(signup.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(signup.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(logout.fulfilled, (state) => {
        state.user = null;
        state.token = null;
      })
      .addCase(refreshToken.fulfilled, (state, action) => {
        state.token = action.payload;
      });
      // .addCase(getCurrentUser.pending, ...)
      // .addCase(getCurrentUser.fulfilled, ...)
      // .addCase(getCurrentUser.rejected, ...)
      // .addCase(updateProfile.fulfilled, ...)
  },
});

export const { clearError } = authSlice.actions;
export default authSlice.reducer; 