// src/pages/PostList.jsx
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import {
  Container,
  Box,
  Typography,
  TextField,
  Button,
  Grid,
  Card,
  CardContent,
  CardActions,
  Chip,
  IconButton,
  InputAdornment,
  Pagination,
} from '@mui/material';
import {
  Search as SearchIcon,
  Add as AddIcon,
  ThumbUp as ThumbUpIcon,
  Comment as CommentIcon,
} from '@mui/icons-material';
import { fetchPosts, setSearchKeyword, setCurrentPage } from '../store/slices/postSlice';
import { showToast } from '../store/slices/uiSlice';
import Loading from '../components/common/Loading';

const PostList = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { posts, loading, error, searchKeyword, currentPage, totalPages } = useSelector(
    (state) => state.posts
  );
  const { user } = useSelector((state) => state.auth);
  const [searchInput, setSearchInput] = useState(searchKeyword);

  useEffect(() => {
    dispatch(fetchPosts({ page: currentPage, search: searchKeyword }));
  }, [dispatch, currentPage, searchKeyword]);

  const handleSearch = (e) => {
    e.preventDefault();
    dispatch(setSearchKeyword(searchInput));
    dispatch(setCurrentPage(1));
  };

  const handlePageChange = (event, value) => {
    dispatch(setCurrentPage(value));
  };

  const handlePostClick = (postId) => {
    navigate(`/posts/${postId}`);
  };

  const handleCreateClick = () => {
    navigate('/posts/create');
  };

  if (loading) {
    return <Loading />;
  }

  if (error) {
    return (
      <Container>
        <Box sx={{ mt: 4, textAlign: 'center' }}>
          <Typography color="error">{error}</Typography>
        </Box>
      </Container>
    );
  }

  return (
    <Container>
      <Box sx={{ mt: 4, mb: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4 }}>
          <Typography variant="h4" component="h1">
            게시글 목록
          </Typography>
          {user && (
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={handleCreateClick}
            >
              글쓰기
            </Button>
          )}
        </Box>

        <Box component="form" onSubmit={handleSearch} sx={{ mb: 4 }}>
          <TextField
            fullWidth
            placeholder="검색어를 입력하세요"
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton type="submit">
                    <SearchIcon />
                  </IconButton>
                </InputAdornment>
              ),
            }}
          />
        </Box>

        <Grid container spacing={3}>
          {posts.map((post) => (
            <Grid item xs={12} key={post.id}>
              <Card
                sx={{
                  cursor: 'pointer',
                  '&:hover': {
                    boxShadow: 6,
                  },
                }}
                onClick={() => handlePostClick(post.id)}
              >
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {post.title}
                  </Typography>
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      display: '-webkit-box',
                      WebkitLineClamp: 2,
                      WebkitBoxOrient: 'vertical',
                    }}
                  >
                    {post.content}
                  </Typography>
                  <Box sx={{ mt: 2, display: 'flex', gap: 1 }}>
                    <Chip
                      size="small"
                      icon={<ThumbUpIcon />}
                      label={post.likeCount}
                    />
                    <Chip
                      size="small"
                      icon={<CommentIcon />}
                      label={post.commentCount}
                    />
                  </Box>
                </CardContent>
                <CardActions>
                  <Typography variant="caption" color="text.secondary">
                    작성자: {post.author}
                  </Typography>
                  <Typography variant="caption" color="text.secondary" sx={{ ml: 'auto' }}>
                    {new Date(post.createdAt).toLocaleDateString()}
                  </Typography>
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>

        {totalPages > 1 && (
          <Box sx={{ mt: 4, display: 'flex', justifyContent: 'center' }}>
            <Pagination
              count={totalPages}
              page={currentPage}
              onChange={handlePageChange}
              color="primary"
            />
          </Box>
        )}
      </Box>
    </Container>
  );
};

export default PostList;
