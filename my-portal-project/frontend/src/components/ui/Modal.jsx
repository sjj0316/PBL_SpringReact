import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  IconButton,
  Typography,
} from '@mui/material';
import { Close as CloseIcon } from '@mui/icons-material';
import { useSelector, useDispatch } from 'react-redux';
import { closeModal } from '../../store/slices/uiSlice';

const Modal = () => {
  const dispatch = useDispatch();
  const { modal } = useSelector((state) => state.ui);

  const handleClose = () => {
    dispatch(closeModal());
  };

  const handleConfirm = () => {
    if (modal.onConfirm) {
      modal.onConfirm();
    }
    handleClose();
  };

  return (
    <Dialog
      open={modal.open}
      onClose={handleClose}
      maxWidth={modal.maxWidth || 'sm'}
      fullWidth
    >
      <DialogTitle>
        <Typography variant="h6" component="div">
          {modal.title}
        </Typography>
        <IconButton
          aria-label="close"
          onClick={handleClose}
          sx={{
            position: 'absolute',
            right: 8,
            top: 8,
          }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent dividers>
        {modal.content}
      </DialogContent>
      <DialogActions>
        {modal.showCancel && (
          <Button onClick={handleClose} color="inherit">
            취소
          </Button>
        )}
        <Button
          onClick={handleConfirm}
          color="primary"
          variant="contained"
          autoFocus
        >
          {modal.confirmText || '확인'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default Modal; 