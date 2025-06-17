import React, { useCallback, useMemo } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  TextField,
  Box,
  Pagination,
  CircularProgress,
  Alert,
} from '@mui/material';
import { fetchPosts, searchPostsByKeyword } from '../../store/slices/postSlice';
import { showToast } from '../../store/slices/uiSlice';

const PostList = React.memo(() => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { posts, loading, error, currentPage, totalPages, searchKeyword } = useSelector(
    (state) => state.post
  );

  const handleSearch = useCallback(
    (event) => {
      dispatch(setSearchKeyword(event.target.value));
      dispatch(setCurrentPage(1));
      dispatch(fetchPosts({ page: 1, search: event.target.value }));
    },
    [dispatch]
  );

  const handlePageChange = useCallback(
    (event, value) => {
      dispatch(setCurrentPage(value));
      dispatch(fetchPosts({ page: value, search: searchKeyword }));
    },
    [dispatch, searchKeyword]
  );

  const handlePostClick = useCallback(
    (postId) => {
      navigate(`/posts/${postId}`);
    },
    [navigate]
  );

  const filteredPosts = useMemo(() => {
    return posts.filter((post) =>
      post.title.toLowerCase().includes(searchKeyword.toLowerCase())
    );
  }, [posts, searchKeyword]);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ mt: 2 }}>
        {error}
      </Alert>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Box sx={{ mb: 4 }}>
        <TextField
          fullWidth
          label="검색"
          variant="outlined"
          value={searchKeyword}
          onChange={handleSearch}
          sx={{ mb: 2 }}
        />
      </Box>

      <Grid container spacing={3}>
        {filteredPosts.map((post) => (
          <Grid item xs={12} key={post.id}>
            <Card>
              <CardContent>
                <Typography variant="h5" component="h2" gutterBottom>
                  {post.title}
                </Typography>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  작성자: {post.author} | 작성일: {new Date(post.createdAt).toLocaleDateString()}
                </Typography>
                <Typography variant="body1" paragraph>
                  {post.content}
                </Typography>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography variant="body2" color="text.secondary">
                    좋아요: {post.likeCount} | 댓글: {post.commentCount}
                  </Typography>
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={() => handlePostClick(post.id)}
                  >
                    자세히 보기
                  </Button>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <Pagination
          count={totalPages}
          page={currentPage}
          onChange={handlePageChange}
          color="primary"
        />
      </Box>
    </Container>
  );
});

PostList.displayName = 'PostList';

export default PostList; 