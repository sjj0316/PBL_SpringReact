import { Alert, AlertTitle, Box, Button } from '@mui/material';
import { Refresh as RefreshIcon } from '@mui/icons-material';

export default function ErrorMessage({ 
  title = '오류가 발생했습니다',
  message = '잠시 후 다시 시도해주세요',
  onRetry,
  severity = 'error'
}) {
  return (
    <Box sx={{ my: 2 }}>
      <Alert 
        severity={severity}
        action={
          onRetry && (
            <Button
              color="inherit"
              size="small"
              startIcon={<RefreshIcon />}
              onClick={onRetry}
            >
              다시 시도
            </Button>
          )
        }
      >
        <AlertTitle>{title}</AlertTitle>
        {message}
      </Alert>
    </Box>
  );
} 