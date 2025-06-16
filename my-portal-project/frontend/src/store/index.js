import { configureStore } from '@reduxjs/toolkit';
import { authMiddleware, validationMiddleware, errorMiddleware, apiMiddleware } from '../middleware/security';
import authReducer from './slices/authSlice';
import postReducer from './slices/postSlice';
import commentReducer from './slices/commentSlice';
import fileReducer from './slices/fileSlice';
import uiReducer from './slices/uiSlice';

const store = configureStore({
  reducer: {
    auth: authReducer,
    posts: postReducer,
    comments: commentReducer,
    file: fileReducer,
    ui: uiReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        // Date 객체 직렬화 허용
        ignoredActions: ['posts/fetchPosts/fulfilled', 'posts/fetchPost/fulfilled'],
        ignoredPaths: ['posts.posts', 'posts.currentPost'],
      },
    }).concat(authMiddleware, validationMiddleware, errorMiddleware, apiMiddleware),
});

export default store; 