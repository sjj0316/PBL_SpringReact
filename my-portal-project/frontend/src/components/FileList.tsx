import React, { useState, useEffect } from 'react';
import {
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Typography,
  Tooltip,
  TextField,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
} from '@mui/material';
import {
  Download as DownloadIcon,
  Delete as DeleteIcon,
  Restore as RestoreIcon,
  Search as SearchIcon,
  Edit as EditIcon,
} from '@mui/icons-material';

interface FileMetadata {
  fileId: string;
  fileName: string;
  fileType: string;
  fileSize: number;
  uploadTime: string;
  status: string;
  isDeleted: boolean;
  downloadCount: number;
  viewCount: number;
}

interface FileListProps {
  onDownload: (fileId: string) => void;
  onDelete: (fileId: string, reason?: string) => void;
  onRestore: (fileId: string) => void;
  onEdit: (fileId: string) => void;
}

const FileList: React.FC<FileListProps> = ({
  onDownload,
  onDelete,
  onRestore,
  onEdit,
}) => {
  const [files, setFiles] = useState<FileMetadata[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [category, setCategory] = useState('');
  const [accessLevel, setAccessLevel] = useState('');
  const [includeDeleted, setIncludeDeleted] = useState(false);

  useEffect(() => {
    fetchFiles();
  }, [category, accessLevel, includeDeleted]);

  const fetchFiles = async () => {
    try {
      const params = new URLSearchParams();
      if (category) params.append('category', category);
      if (accessLevel) params.append('accessLevel', accessLevel);
      params.append('includeDeleted', includeDeleted.toString());

      const response = await fetch(`/api/files?${params.toString()}`);
      if (response.ok) {
        const data = await response.json();
        setFiles(data);
      }
    } catch (error) {
      console.error('Failed to fetch files:', error);
    }
  };

  const handleSearch = async () => {
    try {
      const response = await fetch(`/api/files/search?query=${searchQuery}`);
      if (response.ok) {
        const data = await response.json();
        setFiles(data);
      }
    } catch (error) {
      console.error('Failed to search files:', error);
    }
  };

  const formatFileSize = (bytes: number) => {
    const units = ['B', 'KB', 'MB', 'GB'];
    let size = bytes;
    let unitIndex = 0;
    while (size >= 1024 && unitIndex < units.length - 1) {
      size /= 1024;
      unitIndex++;
    }
    return `${size.toFixed(1)} ${units[unitIndex]}`;
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  return (
    <Box>
      <Box display="flex" gap={2} mb={3}>
        <TextField
          label="Search"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          InputProps={{
            endAdornment: (
              <IconButton onClick={handleSearch}>
                <SearchIcon />
              </IconButton>
            ),
          }}
        />
        <FormControl sx={{ minWidth: 120 }}>
          <InputLabel>Category</InputLabel>
          <Select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            label="Category"
          >
            <MenuItem value="">All</MenuItem>
            <MenuItem value="document">Document</MenuItem>
            <MenuItem value="image">Image</MenuItem>
            <MenuItem value="video">Video</MenuItem>
            <MenuItem value="audio">Audio</MenuItem>
          </Select>
        </FormControl>
        <FormControl sx={{ minWidth: 120 }}>
          <InputLabel>Access Level</InputLabel>
          <Select
            value={accessLevel}
            onChange={(e) => setAccessLevel(e.target.value)}
            label="Access Level"
          >
            <MenuItem value="">All</MenuItem>
            <MenuItem value="public">Public</MenuItem>
            <MenuItem value="private">Private</MenuItem>
          </Select>
        </FormControl>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>File Name</TableCell>
              <TableCell>Type</TableCell>
              <TableCell>Size</TableCell>
              <TableCell>Upload Time</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Downloads</TableCell>
              <TableCell>Views</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {files.map((file) => (
              <TableRow key={file.fileId}>
                <TableCell>{file.fileName}</TableCell>
                <TableCell>{file.fileType}</TableCell>
                <TableCell>{formatFileSize(file.fileSize)}</TableCell>
                <TableCell>{formatDate(file.uploadTime)}</TableCell>
                <TableCell>{file.status}</TableCell>
                <TableCell>{file.downloadCount}</TableCell>
                <TableCell>{file.viewCount}</TableCell>
                <TableCell>
                  <Tooltip title="Download">
                    <IconButton onClick={() => onDownload(file.fileId)}>
                      <DownloadIcon />
                    </IconButton>
                  </Tooltip>
                  {file.isDeleted ? (
                    <Tooltip title="Restore">
                      <IconButton onClick={() => onRestore(file.fileId)}>
                        <RestoreIcon />
                      </IconButton>
                    </Tooltip>
                  ) : (
                    <>
                      <Tooltip title="Edit">
                        <IconButton onClick={() => onEdit(file.fileId)}>
                          <EditIcon />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Delete">
                        <IconButton onClick={() => onDelete(file.fileId)}>
                          <DeleteIcon />
                        </IconButton>
                      </Tooltip>
                    </>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};

export default FileList; 