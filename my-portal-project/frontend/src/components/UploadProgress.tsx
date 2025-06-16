import React from 'react';
import {
  Box,
  LinearProgress,
  Typography,
  IconButton,
  Tooltip,
  Paper,
} from '@mui/material';
import {
  Pause as PauseIcon,
  PlayArrow as PlayIcon,
  Cancel as CancelIcon,
  Refresh as RetryIcon,
} from '@mui/icons-material';
import { styled } from '@mui/material/styles';

interface UploadProgressProps {
  fileName: string;
  progress: number;
  status: 'uploading' | 'paused' | 'error' | 'completed';
  speed?: number;
  onPause?: () => void;
  onResume?: () => void;
  onCancel?: () => void;
  onRetry?: () => void;
  error?: string;
}

const ProgressPaper = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(2),
  marginBottom: theme.spacing(2),
}));

const ProgressBar = styled(LinearProgress)(({ theme }) => ({
  height: 8,
  borderRadius: 4,
  marginTop: theme.spacing(1),
  marginBottom: theme.spacing(1),
}));

const UploadProgress: React.FC<UploadProgressProps> = ({
  fileName,
  progress,
  status,
  speed,
  onPause,
  onResume,
  onCancel,
  onRetry,
  error,
}) => {
  const getStatusColor = () => {
    switch (status) {
      case 'uploading':
        return 'primary';
      case 'paused':
        return 'warning';
      case 'error':
        return 'error';
      case 'completed':
        return 'success';
      default:
        return 'primary';
    }
  };

  const formatSpeed = (bytesPerSecond?: number) => {
    if (!bytesPerSecond) return '';
    const units = ['B/s', 'KB/s', 'MB/s', 'GB/s'];
    let speed = bytesPerSecond;
    let unitIndex = 0;
    while (speed >= 1024 && unitIndex < units.length - 1) {
      speed /= 1024;
      unitIndex++;
    }
    return `${speed.toFixed(1)} ${units[unitIndex]}`;
  };

  return (
    <ProgressPaper elevation={2}>
      <Box display="flex" alignItems="center" justifyContent="space-between">
        <Typography variant="subtitle1" noWrap>
          {fileName}
        </Typography>
        <Box>
          {status === 'uploading' && onPause && (
            <Tooltip title="Pause">
              <IconButton onClick={onPause} size="small">
                <PauseIcon />
              </IconButton>
            </Tooltip>
          )}
          {status === 'paused' && onResume && (
            <Tooltip title="Resume">
              <IconButton onClick={onResume} size="small">
                <PlayIcon />
              </IconButton>
            </Tooltip>
          )}
          {status === 'error' && onRetry && (
            <Tooltip title="Retry">
              <IconButton onClick={onRetry} size="small">
                <RetryIcon />
              </IconButton>
            </Tooltip>
          )}
          {onCancel && (
            <Tooltip title="Cancel">
              <IconButton onClick={onCancel} size="small">
                <CancelIcon />
              </IconButton>
            </Tooltip>
          )}
        </Box>
      </Box>

      <Box display="flex" alignItems="center" mt={1}>
        <Box flexGrow={1}>
          <ProgressBar
            variant="determinate"
            value={progress}
            color={getStatusColor()}
          />
        </Box>
        <Typography variant="body2" color="textSecondary" ml={2}>
          {Math.round(progress)}%
        </Typography>
      </Box>

      <Box display="flex" justifyContent="space-between" mt={1}>
        <Typography variant="body2" color="textSecondary">
          {status === 'uploading' && speed ? formatSpeed(speed) : status}
        </Typography>
        {error && (
          <Typography variant="body2" color="error">
            {error}
          </Typography>
        )}
      </Box>
    </ProgressPaper>
  );
};

export default UploadProgress; 