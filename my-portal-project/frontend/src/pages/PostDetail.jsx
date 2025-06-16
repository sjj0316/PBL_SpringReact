// src/pages/PostDetail.jsx
import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import {
  Container,
  Box,
  Typography,
  Button,
  Paper,
  Divider,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from '@mui/material';
import {
  Edit as EditIcon,
  Delete as DeleteIcon,
  ArrowBack as ArrowBackIcon,
} from '@mui/icons-material';
import { fetchPost, deletePost } from '../store/slices/postSlice';
import { showToast } from '../store/slices/uiSlice';
import Loading from '../components/common/Loading';
import CommentSection from '../components/comments/CommentSection';

const PostDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { currentPost: post, loading, error } = useSelector((state) => state.posts);
  const { user } = useSelector((state) => state.auth);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

  useEffect(() => {
    dispatch(fetchPost(id));
  }, [dispatch, id]);

  const handleBack = () => {
    navigate('/posts');
  };

  const handleEdit = () => {
    navigate(`/posts/${id}/edit`);
  };

  const handleDelete = async () => {
    try {
      await dispatch(deletePost(id)).unwrap();
      showToast('게시글이 삭제되었습니다.', 'success');
      navigate('/posts');
    } catch (error) {
      showToast(error.message || '게시글 삭제에 실패했습니다.', 'error');
    }
    setDeleteDialogOpen(false);
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

  if (!post) {
    return (
      <Container>
        <Box sx={{ mt: 4, textAlign: 'center' }}>
          <Typography>게시글을 찾을 수 없습니다.</Typography>
        </Box>
      </Container>
    );
  }

  return (
    <Container>
      <Box sx={{ mt: 4, mb: 4 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          <IconButton onClick={handleBack} sx={{ mr: 2 }}>
            <ArrowBackIcon />
          </IconButton>
          <Typography variant="h4" component="h1">
            {post.title}
          </Typography>
        </Box>

        <Paper sx={{ p: 3, mb: 3 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
            <Typography variant="subtitle1">
              작성자: {post.author}
            </Typography>
            <Typography variant="subtitle1">
              작성일: {new Date(post.createdAt).toLocaleDateString()}
            </Typography>
          </Box>
          <Divider sx={{ my: 2 }} />
          <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap' }}>
            {post.content}
          </Typography>
        </Paper>

        {user && (user.id === post.authorId || user.role === 'ADMIN') && (
          <Box sx={{ display: 'flex', gap: 1, mb: 3 }}>
            <Button
              variant="outlined"
              startIcon={<EditIcon />}
              onClick={handleEdit}
            >
              수정
            </Button>
            <Button
              variant="outlined"
              color="error"
              startIcon={<DeleteIcon />}
              onClick={() => setDeleteDialogOpen(true)}
            >
              삭제
            </Button>
          </Box>
        )}

        <CommentSection postId={id} />
      </Box>

      <Dialog
        open={deleteDialogOpen}
        onClose={() => setDeleteDialogOpen(false)}
      >
        <DialogTitle>게시글 삭제</DialogTitle>
        <DialogContent>
          <Typography>
            정말로 이 게시글을 삭제하시겠습니까?
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)}>취소</Button>
          <Button onClick={handleDelete} color="error">
            삭제
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default PostDetail;
