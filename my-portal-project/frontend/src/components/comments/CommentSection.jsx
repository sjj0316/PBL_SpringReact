import { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  Box,
  Typography,
  TextField,
  Button,
  Paper,
  Divider,
  IconButton,
} from '@mui/material';
import { Send as SendIcon } from '@mui/icons-material';
import { fetchComments, createComment } from '../../store/slices/commentSlice';
import { showToast } from '../../store/slices/uiSlice';
import CommentItem from './CommentItem';
import Loading from '../common/Loading';

const CommentSection = ({ postId }) => {
  const dispatch = useDispatch();
  const { comments, loading, error: commentsError } = useSelector((state) => state.comments);
  const { user } = useSelector((state) => state.auth);
  const [content, setContent] = useState('');
  const [formError, setFormError] = useState('');

  useEffect(() => {
    dispatch(fetchComments(postId));
  }, [dispatch, postId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content.trim()) {
      setFormError('댓글 내용을 입력해주세요');
      return;
    }

    try {
      await dispatch(createComment({ postId, content })).unwrap();
      setContent('');
      setFormError('');
      showToast('댓글이 작성되었습니다.', 'success');
    } catch (error) {
      showToast(error.message || '댓글 작성에 실패했습니다.', 'error');
    }
  };

  if (loading) {
    return <Loading />;
  }

  return (
    <Box>
      <Typography variant="h6" gutterBottom>
        댓글 {comments.length}개
      </Typography>

      {user && (
        <Paper sx={{ p: 2, mb: 3 }}>
          <Box component="form" onSubmit={handleSubmit}>
            <TextField
              fullWidth
              multiline
              rows={3}
              placeholder="댓글을 입력하세요"
              value={content}
              onChange={(e) => {
                setContent(e.target.value);
                if (formError) setFormError('');
              }}
              error={!!formError}
              helperText={formError}
              sx={{ mb: 2 }}
            />
            <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
              <Button
                type="submit"
                variant="contained"
                endIcon={<SendIcon />}
                disabled={!content.trim()}
              >
                댓글 작성
              </Button>
            </Box>
          </Box>
        </Paper>
      )}

      <Divider sx={{ my: 2 }} />

      {comments.length > 0 ? (
        comments.map((comment) => (
          <CommentItem
            key={comment.id}
            comment={comment}
            postId={postId}
          />
        ))
      ) : (
        <Typography color="text.secondary" align="center" sx={{ py: 3 }}>
          아직 댓글이 없습니다.
        </Typography>
      )}
    </Box>
  );
};

export default CommentSection; 