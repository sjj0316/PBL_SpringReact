import { useState, useRef } from 'react';
import {
  Box,
  Button,
  IconButton,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  Typography,
  LinearProgress,
  Paper,
} from '@mui/material';
import {
  CloudUpload as CloudUploadIcon,
  Delete as DeleteIcon,
  Image as ImageIcon,
  Description as DescriptionIcon,
  PictureAsPdf as PdfIcon,
} from '@mui/icons-material';

const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
const ALLOWED_FILE_TYPES = [
  'image/jpeg',
  'image/png',
  'image/gif',
  'application/pdf',
  'application/msword',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
];

export default function FileUpload({ onFilesChange, existingFiles = [], onExistingFilesChange }) {
  const [files, setFiles] = useState([]);
  const [uploadProgress, setUploadProgress] = useState({});
  const [error, setError] = useState(null);
  const fileInputRef = useRef(null);

  const handleFileSelect = (event) => {
    const selectedFiles = Array.from(event.target.files);
    setError(null);

    const validFiles = selectedFiles.filter(file => {
      if (file.size > MAX_FILE_SIZE) {
        setError(`파일 크기는 ${MAX_FILE_SIZE / 1024 / 1024}MB를 초과할 수 없습니다.`);
        return false;
      }
      if (!ALLOWED_FILE_TYPES.includes(file.type)) {
        setError("지원하지 않는 파일 형식입니다.");
        return false;
      }
      return true;
    });

    const newFiles = validFiles.map(file => ({
      file,
      preview: file.type.startsWith('image/') ? URL.createObjectURL(file) : null,
    }));

    setFiles(prev => [...prev, ...newFiles]);
    onFilesChange([...files, ...newFiles]);
  };

  const handleFileDelete = (index) => {
    const fileToDelete = files[index];
    if (fileToDelete.preview) {
      URL.revokeObjectURL(fileToDelete.preview);
    }
    const newFiles = files.filter((_, i) => i !== index);
    setFiles(newFiles);
    onFilesChange(newFiles);
  };

  const handleExistingFileDelete = (index) => {
    const newExistingFiles = existingFiles.filter((_, i) => i !== index);
    onExistingFilesChange(newExistingFiles);
  };

  const getFileIcon = (file) => {
    if (file.type?.startsWith('image/')) return <ImageIcon />;
    if (file.type === 'application/pdf') return <PdfIcon />;
    return <DescriptionIcon />;
  };

  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  return (
    <Box>
      <input
        type="file"
        multiple
        onChange={handleFileSelect}
        style={{ display: 'none' }}
        ref={fileInputRef}
      />
      <Button
        variant="outlined"
        startIcon={<CloudUploadIcon />}
        onClick={() => fileInputRef.current.click()}
        sx={{ mb: 2 }}
      >
        파일 선택
      </Button>

      {error && (
        <Typography color="error" sx={{ mb: 2 }}>
          {error}
        </Typography>
      )}

      {existingFiles.length > 0 && (
        <Box sx={{ mb: 2 }}>
          <Typography variant="subtitle2" sx={{ mb: 1 }}>
            기존 첨부파일
          </Typography>
          <List>
            {existingFiles.map((file, index) => (
              <ListItem key={index}>
                {getFileIcon(file)}
                <ListItemText
                  primary={file.originalName}
                  secondary={formatFileSize(file.size)}
                  sx={{ ml: 1 }}
                />
                <ListItemSecondaryAction>
                  <IconButton
                    edge="end"
                    aria-label="delete"
                    onClick={() => handleExistingFileDelete(index)}
                  >
                    <DeleteIcon />
                  </IconButton>
                </ListItemSecondaryAction>
              </ListItem>
            ))}
          </List>
        </Box>
      )}

      {files.length > 0 && (
        <Box>
          <Typography variant="subtitle2" sx={{ mb: 1 }}>
            새로 첨부할 파일
          </Typography>
          <List>
            {files.map((file, index) => (
              <ListItem key={index}>
                {file.preview ? (
                  <img
                    src={file.preview}
                    alt={file.file.name}
                    style={{ width: 40, height: 40, objectFit: 'cover' }}
                  />
                ) : (
                  getFileIcon(file.file)
                )}
                <ListItemText
                  primary={file.file.name}
                  secondary={formatFileSize(file.file.size)}
                  sx={{ ml: 1 }}
                />
                <ListItemSecondaryAction>
                  <IconButton
                    edge="end"
                    aria-label="delete"
                    onClick={() => handleFileDelete(index)}
                  >
                    <DeleteIcon />
                  </IconButton>
                </ListItemSecondaryAction>
              </ListItem>
            ))}
          </List>
        </Box>
      )}

      {Object.keys(uploadProgress).length > 0 && (
        <Box sx={{ mt: 2 }}>
          {Object.entries(uploadProgress).map(([fileName, progress]) => (
            <Box key={fileName} sx={{ mb: 1 }}>
              <Typography variant="caption">{fileName}</Typography>
              <LinearProgress variant="determinate" value={progress} />
            </Box>
          ))}
        </Box>
      )}
    </Box>
  );
} 