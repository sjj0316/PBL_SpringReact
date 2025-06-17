import React, { useCallback, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import {
  Container,
  Paper,
  Typography,
  Box,
  Button,
  CircularProgress,
  Alert,
  Chip,
  IconButton,
} from '@mui/material';
import { Delete as DeleteIcon, Edit as EditIcon, ThumbUp as ThumbUpIcon } from '@mui/icons-material';
import { fetchPost, deleteExistingPost, togglePostLike } from '../../store/slices/postSlice';
import { showToast } from '../../store/slices/uiSlice';
import CommentSection from '../comment/CommentSection';

const PostDetail = React.memo(() => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { currentPost: post, loading, error } = useSelector((state) => state.post);
  const { user } = useSelector((state) => state.auth);

  const loadPost = useCallback(async () => {
    try {
      await dispatch(fetchPost(id)).unwrap();
    } catch (error) {
      dispatch(showToast({ message: error.message, severity: 'error' }));
    }
  }, [dispatch, id]);

  useEffect(() => {
    loadPost();
  }, [loadPost]);

  const handleDelete = useCallback(async () => {
    if (window.confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
      try {
        await dispatch(deleteExistingPost(id)).unwrap();
        dispatch(showToast({ message: '게시글이 삭제되었습니다.', severity: 'success' }));
        navigate('/posts');
      } catch (error) {
        dispatch(showToast({ message: error.message, severity: 'error' }));
      }
    }
  }, [dispatch, id, navigate]);

  const handleLike = useCallback(async () => {
    try {
      await dispatch(togglePostLike(id)).unwrap();
    } catch (error) {
      dispatch(showToast({ message: error.message, severity: 'error' }));
    }
  }, [dispatch, id]);

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

  if (!post) {
    return (
      <Alert severity="info" sx={{ mt: 2 }}>
        게시글을 찾을 수 없습니다.
      </Alert>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Paper elevation={3} sx={{ p: 3 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h4" component="h1">
            {post.title}
          </Typography>
          <Box>
            {user && user.id === post.authorId && (
              <>
                <IconButton onClick={() => navigate(`/posts/${id}/edit`)} color="primary">
                  <EditIcon />
                </IconButton>
                <IconButton onClick={handleDelete} color="error">
                  <DeleteIcon />
                </IconButton>
              </>
            )}
          </Box>
        </Box>

        <Box sx={{ mb: 2 }}>
          <Typography variant="body2" color="text.secondary">
            작성자: {post.author} | 작성일: {new Date(post.createdAt).toLocaleDateString()}
          </Typography>
        </Box>

        <Typography variant="body1" paragraph>
          {post.content}
        </Typography>

        {post.files && post.files.length > 0 && (
          <Box sx={{ mb: 2 }}>
            <Typography variant="h6" gutterBottom>
              첨부파일
            </Typography>
            {post.files.map((file) => (
              <Chip
                key={file.id}
                label={file.originalName}
                onClick={() => window.open(file.url)}
                sx={{ mr: 1, mb: 1 }}
              />
            ))}
          </Box>
        )}

        <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
          <IconButton onClick={handleLike} color={post.isLiked ? 'primary' : 'default'}>
            <ThumbUpIcon />
          </IconButton>
          <Typography variant="body2" sx={{ ml: 1 }}>
            {post.likeCount}
          </Typography>
        </Box>

        <CommentSection postId={id} />
      </Paper>
    </Container>
  );
});

PostDetail.displayName = 'PostDetail';

export default PostDetail; 