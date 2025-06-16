import React from 'react';
import { Backdrop, CircularProgress, Typography, Box } from '@mui/material';
import { useSelector } from 'react-redux';

const Loading = () => {
  const { loading } = useSelector((state) => state.ui);

  return (
    <Backdrop
      sx={{
        color: '#fff',
        zIndex: (theme) => theme.zIndex.drawer + 1,
        flexDirection: 'column',
        gap: 2,
      }}
      open={loading.open}
    >
      <CircularProgress color="inherit" />
      {loading.message && (
        <Box sx={{ textAlign: 'center' }}>
          <Typography variant="h6" gutterBottom>
            {loading.message}
          </Typography>
          {loading.subMessage && (
            <Typography variant="body2" color="inherit">
              {loading.subMessage}
            </Typography>
          )}
        </Box>
      )}
    </Backdrop>
  );
};

export default Loading; 