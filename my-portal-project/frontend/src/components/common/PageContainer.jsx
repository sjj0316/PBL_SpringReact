import { Box, Container, Paper } from '@mui/material';

export default function PageContainer({ 
  children,
  maxWidth = 'lg',
  disableGutters = false,
  elevation = 0,
  sx = {}
}) {
  return (
    <Container maxWidth={maxWidth} disableGutters={disableGutters}>
      <Box
        sx={{
          py: 3,
          ...sx
        }}
      >
        {elevation > 0 ? (
          <Paper elevation={elevation} sx={{ p: 3 }}>
            {children}
          </Paper>
        ) : (
          children
        )}
      </Box>
    </Container>
  );
} 