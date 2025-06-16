import { useState, useRef, useCallback } from 'react';
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
  Tooltip,
} from '@mui/material';
import {
  CloudUpload as CloudUploadIcon,
  Delete as DeleteIcon,
  Image as ImageIcon,
  Description as DescriptionIcon,
  PictureAsPdf as PdfIcon,
  Pause as PauseIcon,
  PlayArrow as PlayIcon,
  Cancel as CancelIcon,
} from '@mui/icons-material';
import axiosInstance from '../api/axiosConfig';

const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
const CHUNK_SIZE = 1024 * 1024; // 1MB
const MAX_RETRIES = 3;
const RETRY_DELAY = 1000; // 1초

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
  const [uploadStatus, setUploadStatus] = useState({});
  const [error, setError] = useState(null);
  const fileInputRef = useRef(null);
  const uploadControllers = useRef({});

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
      id: Math.random().toString(36).substr(2, 9),
    }));

    setFiles(prev => [...prev, ...newFiles]);
    onFilesChange([...files, ...newFiles]);
  };

  const uploadChunk = async (file, start, end, fileId, retryCount = 0) => {
    const chunk = file.slice(start, end);
    const formData = new FormData();
    formData.append('file', chunk);
    formData.append('fileName', file.name);
    formData.append('fileId', fileId);
    formData.append('chunkIndex', Math.floor(start / CHUNK_SIZE));
    formData.append('totalChunks', Math.ceil(file.size / CHUNK_SIZE));

    try {
      await axiosInstance.post('/api/files/upload-chunk', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        onUploadProgress: (progressEvent) => {
          const chunkProgress = (progressEvent.loaded / progressEvent.total) * 100;
          const totalProgress = ((start + progressEvent.loaded) / file.size) * 100;
          setUploadProgress(prev => ({
            ...prev,
            [fileId]: totalProgress,
          }));
        },
      });
    } catch (error) {
      if (retryCount < MAX_RETRIES) {
        await new Promise(resolve => setTimeout(resolve, RETRY_DELAY));
        return uploadChunk(file, start, end, fileId, retryCount + 1);
      }
      throw error;
    }
  };

  const uploadFile = async (fileData) => {
    const { file, id: fileId } = fileData;
    const totalChunks = Math.ceil(file.size / CHUNK_SIZE);
    const controller = new AbortController();
    uploadControllers.current[fileId] = controller;

    try {
      setUploadStatus(prev => ({
        ...prev,
        [fileId]: 'uploading',
      }));

      for (let start = 0; start < file.size; start += CHUNK_SIZE) {
        if (uploadStatus[fileId] === 'paused') {
          await new Promise(resolve => {
            const checkStatus = setInterval(() => {
              if (uploadStatus[fileId] !== 'paused') {
                clearInterval(checkStatus);
                resolve();
              }
            }, 100);
          });
        }

        if (uploadStatus[fileId] === 'cancelled') {
          throw new Error('Upload cancelled');
        }

        const end = Math.min(start + CHUNK_SIZE, file.size);
        await uploadChunk(file, start, end, fileId);
      }

      // 업로드 완료 처리
      await axiosInstance.post('/api/files/complete-upload', {
        fileId,
        fileName: file.name,
        totalChunks,
      });

      setUploadStatus(prev => ({
        ...prev,
        [fileId]: 'completed',
      }));
    } catch (error) {
      if (error.message !== 'Upload cancelled') {
        setError(`파일 업로드 실패: ${error.message}`);
      }
      setUploadStatus(prev => ({
        ...prev,
        [fileId]: 'error',
      }));
    } finally {
      delete uploadControllers.current[fileId];
    }
  };

  const handleUpload = async () => {
    for (const fileData of files) {
      if (uploadStatus[fileData.id] !== 'completed') {
        await uploadFile(fileData);
      }
    }
  };

  const handlePause = (fileId) => {
    setUploadStatus(prev => ({
      ...prev,
      [fileId]: 'paused',
    }));
  };

  const handleResume = (fileId) => {
    setUploadStatus(prev => ({
      ...prev,
      [fileId]: 'uploading',
    }));
  };

  const handleCancel = (fileId) => {
    if (uploadControllers.current[fileId]) {
      uploadControllers.current[fileId].abort();
    }
    setUploadStatus(prev => ({
      ...prev,
      [fileId]: 'cancelled',
    }));
  };

  const handleFileDelete = (index) => {
    const fileToDelete = files[index];
    if (fileToDelete.preview) {
      URL.revokeObjectURL(fileToDelete.preview);
    }
    if (uploadControllers.current[fileToDelete.id]) {
      uploadControllers.current[fileToDelete.id].abort();
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

  const getStatusIcon = (fileId) => {
    const status = uploadStatus[fileId];
    switch (status) {
      case 'paused':
        return (
          <Tooltip title="업로드 재개">
            <IconButton onClick={() => handleResume(fileId)}>
              <PlayIcon />
            </IconButton>
          </Tooltip>
        );
      case 'uploading':
        return (
          <Tooltip title="업로드 일시정지">
            <IconButton onClick={() => handlePause(fileId)}>
              <PauseIcon />
            </IconButton>
          </Tooltip>
        );
      case 'error':
        return (
          <Tooltip title="업로드 재시도">
            <IconButton onClick={() => uploadFile(files.find(f => f.id === fileId))}>
              <PlayIcon />
            </IconButton>
          </Tooltip>
        );
      default:
        return null;
    }
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
      <Box sx={{ mb: 2 }}>
        <Button
          variant="outlined"
          startIcon={<CloudUploadIcon />}
          onClick={() => fileInputRef.current.click()}
          sx={{ mr: 1 }}
        >
          파일 선택
        </Button>
        <Button
          variant="contained"
          onClick={handleUpload}
          disabled={files.length === 0}
        >
          업로드 시작
        </Button>
      </Box>

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
            {files.map((fileData, index) => (
              <ListItem key={index}>
                {fileData.preview ? (
                  <img
                    src={fileData.preview}
                    alt={fileData.file.name}
                    style={{ width: 40, height: 40, objectFit: 'cover' }}
                  />
                ) : (
                  getFileIcon(fileData.file)
                )}
                <ListItemText
                  primary={fileData.file.name}
                  secondary={
                    <Box>
                      <Typography variant="body2">
                        {formatFileSize(fileData.file.size)}
                      </Typography>
                      {uploadProgress[fileData.id] !== undefined && (
                        <LinearProgress
                          variant="determinate"
                          value={uploadProgress[fileData.id]}
                          sx={{ mt: 1 }}
                        />
                      )}
                    </Box>
                  }
                  sx={{ ml: 1 }}
                />
                <ListItemSecondaryAction>
                  {getStatusIcon(fileData.id)}
                  <Tooltip title="업로드 취소">
                    <IconButton
                      edge="end"
                      aria-label="cancel"
                      onClick={() => handleCancel(fileData.id)}
                    >
                      <CancelIcon />
                    </IconButton>
                  </Tooltip>
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
    </Box>
  );
} 