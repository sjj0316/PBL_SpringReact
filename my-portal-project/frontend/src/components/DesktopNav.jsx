import { Box, Button, useMediaQuery } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const pages = [
  { title: '홈', path: '/' },
  { title: '게시판', path: '/' },
];

const authPages = [
  { title: '글쓰기', path: '/write' },
];

export default function DesktopNav() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const isMobile = useMediaQuery('(max-width:600px)');

  if (isMobile) {
    return null;
  }

  return (
    <Box sx={{ display: 'flex', gap: 2 }}>
      {pages.map((page) => (
        <Button
          key={page.title}
          color="inherit"
          onClick={() => navigate(page.path)}
        >
          {page.title}
        </Button>
      ))}
      {user && authPages.map((page) => (
        <Button
          key={page.title}
          color="inherit"
          onClick={() => navigate(page.path)}
        >
          {page.title}
        </Button>
      ))}
    </Box>
  );
} 