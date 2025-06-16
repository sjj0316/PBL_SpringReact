import React, { useState } from 'react';
import {
  Container,
  Typography,
  Box,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Alert,
} from '@mui/material';
import FileList from '../components/FileList';
import FileUpload from '../components/FileUpload';

const FileManagementPage: React.FC = () => {
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [selectedFileId, setSelectedFileId] = useState<string | null>(null);
  const [editForm, setEditForm] = useState({
    description: '',
    tags: '',
    category: '',
    accessLevel: '',
  });

  const handleDownload = async (fileId: string) => {
    try {
      const response = await fetch(`/api/files/${fileId}/download`);
      if (response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = ''; // 서버에서 제공하는 파일명 사용
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        setSuccess('File downloaded successfully');
      } else {
        setError('Failed to download file');
      }
    } catch (error) {
      setError('Failed to download file');
    }
  };

  const handleDelete = async (fileId: string, reason?: string) => {
    try {
      const response = await fetch(`/api/files/${fileId}${reason ? `?reason=${reason}` : ''}`, {
        method: 'DELETE',
      });
      if (response.ok) {
        setSuccess('File deleted successfully');
      } else {
        setError('Failed to delete file');
      }
    } catch (error) {
      setError('Failed to delete file');
    }
  };

  const handleRestore = async (fileId: string) => {
    try {
      const response = await fetch(`/api/files/${fileId}/restore`, {
        method: 'POST',
      });
      if (response.ok) {
        setSuccess('File restored successfully');
      } else {
        setError('Failed to restore file');
      }
    } catch (error) {
      setError('Failed to restore file');
    }
  };

  const handleEdit = async (fileId: string) => {
    try {
      const response = await fetch(`/api/files/${fileId}`);
      if (response.ok) {
        const fileData = await response.json();
        setEditForm({
          description: fileData.description || '',
          tags: fileData.tags || '',
          category: fileData.category || '',
          accessLevel: fileData.accessLevel || '',
        });
        setSelectedFileId(fileId);
        setEditDialogOpen(true);
      }
    } catch (error) {
      setError('Failed to load file data');
    }
  };

  const handleEditSubmit = async () => {
    if (!selectedFileId) return;

    try {
      const response = await fetch(`/api/files/${selectedFileId}/metadata`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(editForm),
      });

      if (response.ok) {
        setSuccess('File metadata updated successfully');
        setEditDialogOpen(false);
      } else {
        setError('Failed to update file metadata');
      }
    } catch (error) {
      setError('Failed to update file metadata');
    }
  };

  const handleUploadComplete = (file: File) => {
    setSuccess(`File ${file.name} uploaded successfully`);
  };

  const handleUploadError = (error: Error) => {
    setError(error.message);
  };

  return (
    <Container maxWidth="lg">
      <Box py={4}>
        <Typography variant="h4" component="h1" gutterBottom>
          File Management
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
            {error}
          </Alert>
        )}

        {success && (
          <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess(null)}>
            {success}
          </Alert>
        )}

        <Box mb={4}>
          <Typography variant="h6" gutterBottom>
            Upload Files
          </Typography>
          <FileUpload
            onUploadComplete={handleUploadComplete}
            onUploadError={handleUploadError}
            maxSize={100 * 1024 * 1024} // 100MB
            accept={['image/*', 'application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document']}
          />
        </Box>

        <Box>
          <Typography variant="h6" gutterBottom>
            File List
          </Typography>
          <FileList
            onDownload={handleDownload}
            onDelete={handleDelete}
            onRestore={handleRestore}
            onEdit={handleEdit}
          />
        </Box>

        <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)}>
          <DialogTitle>Edit File Metadata</DialogTitle>
          <DialogContent>
            <TextField
              fullWidth
              label="Description"
              value={editForm.description}
              onChange={(e) => setEditForm({ ...editForm, description: e.target.value })}
              margin="normal"
            />
            <TextField
              fullWidth
              label="Tags"
              value={editForm.tags}
              onChange={(e) => setEditForm({ ...editForm, tags: e.target.value })}
              margin="normal"
            />
            <TextField
              fullWidth
              label="Category"
              value={editForm.category}
              onChange={(e) => setEditForm({ ...editForm, category: e.target.value })}
              margin="normal"
            />
            <TextField
              fullWidth
              label="Access Level"
              value={editForm.accessLevel}
              onChange={(e) => setEditForm({ ...editForm, accessLevel: e.target.value })}
              margin="normal"
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setEditDialogOpen(false)}>Cancel</Button>
            <Button onClick={handleEditSubmit} variant="contained" color="primary">
              Save
            </Button>
          </DialogActions>
        </Dialog>
      </Box>
    </Container>
  );
};

export default FileManagementPage; 