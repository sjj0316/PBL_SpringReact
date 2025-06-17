import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const Home = () => {
  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4 }}>
        <Typography variant="h3" component="h1" gutterBottom>
          Welcome to Portal
        </Typography>
        <Typography variant="h5" component="h2" gutterBottom>
          Your one-stop platform for managing content and collaboration
        </Typography>
        <Typography variant="body1" paragraph>
          Browse through posts, share your thoughts, and connect with others.
        </Typography>
      </Box>
    </Container>
  );
};

export default Home; 