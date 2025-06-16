import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  isDarkMode: localStorage.getItem('darkMode') === 'true',
  isDrawerOpen: false,
  toast: {
    open: false,
    message: '',
    severity: 'info',
  },
  modal: {
    open: false,
    title: '',
    content: null,
  },
};

const uiSlice = createSlice({
  name: 'ui',
  initialState,
  reducers: {
    toggleDarkMode: (state) => {
      state.isDarkMode = !state.isDarkMode;
      localStorage.setItem('darkMode', state.isDarkMode);
    },
    toggleDrawer: (state) => {
      state.isDrawerOpen = !state.isDrawerOpen;
    },
    showToast: (state, action) => {
      state.toast = {
        open: true,
        message: action.payload.message,
        severity: action.payload.severity || 'info',
      };
    },
    hideToast: (state) => {
      state.toast.open = false;
    },
    showModal: (state, action) => {
      state.modal = {
        open: true,
        title: action.payload.title,
        content: action.payload.content,
      };
    },
    hideModal: (state) => {
      state.modal.open = false;
    },
  },
});

export const {
  toggleDarkMode,
  toggleDrawer,
  showToast,
  hideToast,
  showModal,
  hideModal,
} = uiSlice.actions;

export default uiSlice.reducer; 