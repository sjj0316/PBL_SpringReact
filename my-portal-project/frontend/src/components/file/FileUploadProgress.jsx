import React from 'react';
import {
  Box,
  LinearProgress,
  Typography,
  IconButton,
  Paper,
} from '@mui/material';
import { Close as CloseIcon } from '@mui/icons-material';

const FileUploadProgress = ({ file, progress, onCancel }) => {
  return (
    <Paper
      elevation={1}
      sx={{
        p: 2,
        mb: 2,
        display: 'flex',
        alignItems: 'center',
        gap: 2,
      }}
    >
      <Box sx={{ flexGrow: 1 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
          <Typography variant="body2" noWrap>
            {file.name}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {progress}%
          </Typography>
        </Box>
        <LinearProgress
          variant="determinate"
          value={progress}
          sx={{ height: 8, borderRadius: 4 }}
        />
      </Box>
      <IconButton
        size="small"
        onClick={onCancel}
        sx={{ flexShrink: 0 }}
      >
        <CloseIcon />
      </IconButton>
    </Paper>
  );
};

export default FileUploadProgress; 