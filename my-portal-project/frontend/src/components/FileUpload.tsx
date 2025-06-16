import React, { useCallback, useState } from 'react';
import { useDropzone } from 'react-dropzone';
import {
  Box,
  Button,
  Typography,
  Paper,
  CircularProgress,
  Alert,
} from '@mui/material';
import { styled } from '@mui/material/styles';
import UploadProgress from './UploadProgress';

interface FileUploadProps {
  onUploadComplete?: (file: File) => void;
  onUploadError?: (error: Error) => void;
  maxSize?: number;
  accept?: string[];
}

const DropzonePaper = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(3),
  textAlign: 'center',
  cursor: 'pointer',
  border: `2px dashed ${theme.palette.primary.main}`,
  backgroundColor: theme.palette.background.default,
  '&:hover': {
    backgroundColor: theme.palette.action.hover,
  },
}));

const FileUpload: React.FC<FileUploadProps> = ({
  onUploadComplete,
  onUploadError,
  maxSize = 100 * 1024 * 1024, // 100MB
  accept = ['*/*'],
}) => {
  const [files, setFiles] = useState<File[]>([]);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const onDrop = useCallback(
    (acceptedFiles: File[]) => {
      const validFiles = acceptedFiles.filter((file) => {
        if (file.size > maxSize) {
          setError(`File ${file.name} is too large. Maximum size is ${maxSize / 1024 / 1024}MB`);
          return false;
        }
        return true;
      });

      setFiles((prevFiles) => [...prevFiles, ...validFiles]);
      setError(null);
    },
    [maxSize]
  );

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: accept.join(','),
    maxSize,
  });

  const handleUpload = async () => {
    if (files.length === 0) return;

    setUploading(true);
    setError(null);

    try {
      for (const file of files) {
        const formData = new FormData();
        formData.append('file', file);

        const response = await fetch('/api/files/upload', {
          method: 'POST',
          body: formData,
        });

        if (!response.ok) {
          throw new Error(`Upload failed: ${response.statusText}`);
        }

        onUploadComplete?.(file);
      }

      setFiles([]);
    } catch (err) {
      const error = err as Error;
      setError(error.message);
      onUploadError?.(error);
    } finally {
      setUploading(false);
    }
  };

  const handleRemoveFile = (index: number) => {
    setFiles((prevFiles) => prevFiles.filter((_, i) => i !== index));
  };

  return (
    <Box>
      <DropzonePaper {...getRootProps()}>
        <input {...getInputProps()} />
        {isDragActive ? (
          <Typography>Drop the files here ...</Typography>
        ) : (
          <Typography>
            Drag and drop files here, or click to select files
          </Typography>
        )}
      </DropzonePaper>

      {error && (
        <Alert severity="error" sx={{ mt: 2 }}>
          {error}
        </Alert>
      )}

      {files.length > 0 && (
        <Box mt={2}>
          <Typography variant="h6" gutterBottom>
            Selected Files
          </Typography>
          {files.map((file, index) => (
            <UploadProgress
              key={index}
              fileName={file.name}
              progress={0}
              status="uploading"
              onCancel={() => handleRemoveFile(index)}
            />
          ))}
          <Button
            variant="contained"
            color="primary"
            onClick={handleUpload}
            disabled={uploading}
            startIcon={uploading && <CircularProgress size={20} />}
            sx={{ mt: 2 }}
          >
            {uploading ? 'Uploading...' : 'Upload Files'}
          </Button>
        </Box>
      )}
    </Box>
  );
};

export default FileUpload; 