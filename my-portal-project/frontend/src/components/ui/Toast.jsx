import React from 'react';
import { Snackbar, Alert } from '@mui/material';
import { useSelector, useDispatch } from 'react-redux';
import { clearToast } from '../../store/slices/uiSlice';

const Toast = () => {
  const dispatch = useDispatch();
  const { toast } = useSelector((state) => state.ui);

  const handleClose = (event, reason) => {
    if (reason === 'clickaway') return;
    dispatch(clearToast());
  };

  return (
    <Snackbar
      open={toast.open}
      autoHideDuration={6000}
      onClose={handleClose}
      anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
    >
      <Alert
        onClose={handleClose}
        severity={toast.severity}
        variant="filled"
        sx={{ width: '100%' }}
      >
        {toast.message}
      </Alert>
    </Snackbar>
  );
};

export default Toast; 