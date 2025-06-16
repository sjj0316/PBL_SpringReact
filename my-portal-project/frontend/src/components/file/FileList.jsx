import React from 'react';
import {
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  IconButton,
  Typography,
  Box,
} from '@mui/material';
import {
  InsertDriveFile as FileIcon,
  Download as DownloadIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';
import { useDispatch } from 'react-redux';
import { deleteFile } from '../../store/slices/fileSlice';

const FileList = ({ files, onDelete }) => {
  const dispatch = useDispatch();

  const handleDelete = async (fileId) => {
    try {
      await dispatch(deleteFile(fileId)).unwrap();
      if (onDelete) {
        onDelete(fileId);
      }
    } catch (error) {
      console.error('File deletion failed:', error);
    }
  };

  const handleDownload = (file) => {
    window.open(file.downloadUrl, '_blank');
  };

  if (!files || files.length === 0) {
    return (
      <Box sx={{ textAlign: 'center', py: 2 }}>
        <Typography color="text.secondary">첨부된 파일이 없습니다.</Typography>
      </Box>
    );
  }

  return (
    <List>
      {files.map((file) => (
        <ListItem
          key={file.id}
          secondaryAction={
            <Box>
              <IconButton
                edge="end"
                aria-label="download"
                onClick={() => handleDownload(file)}
                sx={{ mr: 1 }}
              >
                <DownloadIcon />
              </IconButton>
              <IconButton
                edge="end"
                aria-label="delete"
                onClick={() => handleDelete(file.id)}
              >
                <DeleteIcon />
              </IconButton>
            </Box>
          }
        >
          <ListItemIcon>
            <FileIcon />
          </ListItemIcon>
          <ListItemText
            primary={file.originalName}
            secondary={`${(file.size / 1024 / 1024).toFixed(2)} MB`}
          />
        </ListItem>
      ))}
    </List>
  );
};

export default FileList; 