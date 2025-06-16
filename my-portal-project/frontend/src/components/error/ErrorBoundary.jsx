import React from 'react';
import { Box, Typography, Button, Paper } from '@mui/material';
import { useDispatch } from 'react-redux';
import { clearError } from '../../store/slices/uiSlice';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null, errorInfo: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    this.setState({ errorInfo });
    console.error('Error caught by boundary:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            minHeight: '100vh',
            p: 3,
          }}
        >
          <Paper
            elevation={3}
            sx={{
              p: 4,
              maxWidth: 600,
              width: '100%',
              textAlign: 'center',
            }}
          >
            <Typography variant="h4" color="error" gutterBottom>
              오류가 발생했습니다
            </Typography>
            <Typography variant="body1" color="text.secondary" paragraph>
              죄송합니다. 예기치 않은 오류가 발생했습니다.
              잠시 후 다시 시도해 주세요.
            </Typography>
            {process.env.NODE_ENV === 'development' && (
              <Box sx={{ mt: 2, textAlign: 'left' }}>
                <Typography variant="subtitle2" color="error">
                  {this.state.error?.toString()}
                </Typography>
                <Typography
                  variant="body2"
                  component="pre"
                  sx={{
                    mt: 1,
                    p: 2,
                    bgcolor: 'grey.100',
                    borderRadius: 1,
                    overflow: 'auto',
                    maxHeight: 200,
                  }}
                >
                  {this.state.errorInfo?.componentStack}
                </Typography>
              </Box>
            )}
            <Button
              variant="contained"
              color="primary"
              onClick={() => window.location.reload()}
              sx={{ mt: 3 }}
            >
              페이지 새로고침
            </Button>
          </Paper>
        </Box>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary; 