import React from 'react';
import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { configureStore } from '@reduxjs/toolkit';
import authReducer from '../store/slices/authSlice';
import postReducer from '../store/slices/postSlice';
import commentReducer from '../store/slices/commentSlice';
import fileReducer from '../store/slices/fileSlice';
import uiReducer from '../store/slices/uiSlice';

export function renderWithProviders(
  ui,
  {
    preloadedState = {},
    store = configureStore({
      reducer: {
        auth: authReducer,
        post: postReducer,
        comment: commentReducer,
        file: fileReducer,
        ui: uiReducer,
      },
      preloadedState,
    }),
    ...renderOptions
  } = {}
) {
  function Wrapper({ children }) {
    return (
      <Provider store={store}>
        <BrowserRouter>{children}</BrowserRouter>
      </Provider>
    );
  }

  return { store, ...render(ui, { wrapper: Wrapper, ...renderOptions }) };
}

export const mockInitialState = {
  auth: {
    user: null,
    token: null,
    refreshToken: null,
    isAuthenticated: false,
    loading: false,
    error: null,
  },
  post: {
    posts: [],
    currentPost: null,
    loading: false,
    error: null,
    totalPages: 0,
    currentPage: 1,
  },
  comment: {
    comments: [],
    loading: false,
    error: null,
    totalPages: 0,
    currentPage: 1,
  },
  file: {
    files: [],
    loading: false,
    error: null,
    uploadProgress: {},
  },
  ui: {
    toast: {
      open: false,
      message: '',
      severity: 'info',
    },
    modal: {
      open: false,
      title: '',
      content: null,
      onConfirm: null,
      showCancel: true,
      confirmText: '확인',
    },
    loading: {
      open: false,
      message: '',
      subMessage: '',
    },
  },
}; 