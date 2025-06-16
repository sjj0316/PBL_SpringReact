import React, { useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import { Box, Typography, LinearProgress } from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';

const FileUpload = ({ onUpload }) => {
  const onDrop = useCallback(
    async (acceptedFiles) => {
      for (const file of acceptedFiles) {
        try {
          await onUpload(file);
        } catch (error) {
          console.error('File upload failed:', error);
        }
      }
    },
    [onUpload]
  );

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    multiple: true,
  });

  return (
    <Box
      {...getRootProps()}
      sx={{
        border: '2px dashed',
        borderColor: isDragActive ? 'primary.main' : 'grey.300',
        borderRadius: 1,
        p: 3,
        textAlign: 'center',
        cursor: 'pointer',
        bgcolor: isDragActive ? 'action.hover' : 'background.paper',
        '&:hover': {
          bgcolor: 'action.hover',
        },
      }}
    >
      <input {...getInputProps()} />
      <CloudUploadIcon sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
      <Typography variant="h6" gutterBottom>
        파일을 드래그하거나 클릭하여 업로드
      </Typography>
      <Typography variant="body2" color="text.secondary">
        여러 파일을 한 번에 업로드할 수 있습니다.
      </Typography>
    </Box>
  );
};

export default FileUpload; 