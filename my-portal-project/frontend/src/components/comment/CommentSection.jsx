import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  Box,
  Typography,
  TextField,
  Button,
  List,
  Divider,
  Pagination,
  IconButton,
  Menu,
  MenuItem,
} from '@mui/material';
import {
  MoreVert as MoreVertIcon,
  ThumbUp as ThumbUpIcon,
  ThumbUpOutlined as ThumbUpOutlinedIcon,
} from '@mui/icons-material';
import { fetchComments, createComment } from '../../store/slices/commentSlice';
import { showToast } from '../../store/slices/uiSlice';
import CommentItem from './CommentItem';

const CommentSection = ({ postId }) => {
  const dispatch = useDispatch();
  const { comments, totalPages, currentPage, loading } = useSelector(
    (state) => state.comment
  );
  const { user } = useSelector((state) => state.auth);
  const [comment, setComment] = useState('');
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedComment, setSelectedComment] = useState(null);

  useEffect(() => {
    loadComments();
  }, [postId, currentPage]);

  const loadComments = async () => {
    try {
      await dispatch(
        fetchComments({
          postId,
          params: {
            page: currentPage - 1,
            size: 10,
            sort: 'createdAt,desc',
          },
        })
      ).unwrap();
    } catch (error) {
      dispatch(
        showToast({
          message: error.message || '댓글을 불러오는데 실패했습니다.',
          type: 'error',
        })
      );
    }
  };

  const handleCommentChange = (e) => {
    setComment(e.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!comment.trim()) return;

    try {
      await dispatch(
        createComment({
          postId,
          commentData: { content: comment },
        })
      ).unwrap();
      setComment('');
      dispatch(
        showToast({
          message: '댓글이 작성되었습니다.',
          type: 'success',
        })
      );
    } catch (error) {
      dispatch(
        showToast({
          message: error.message || '댓글 작성에 실패했습니다.',
          type: 'error',
        })
      );
    }
  };

  const handlePageChange = (event, value) => {
    // 페이지 변경 로직
  };

  const handleMenuOpen = (event, comment) => {
    setAnchorEl(event.currentTarget);
    setSelectedComment(comment);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedComment(null);
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <Box>
      <Typography variant="h6" gutterBottom>
        댓글 {comments.length}개
      </Typography>

      {user && (
        <Box component="form" onSubmit={handleSubmit} sx={{ mb: 3 }}>
          <TextField
            fullWidth
            multiline
            rows={3}
            placeholder="댓글을 작성하세요"
            value={comment}
            onChange={handleCommentChange}
            sx={{ mb: 1 }}
          />
          <Button
            type="submit"
            variant="contained"
            disabled={!comment.trim()}
          >
            댓글 작성
          </Button>
        </Box>
      )}

      <List>
        {comments.map((comment) => (
          <React.Fragment key={comment.id}>
            <CommentItem
              comment={comment}
              onMenuOpen={handleMenuOpen}
              onMenuClose={handleMenuClose}
            />
            <Divider />
          </React.Fragment>
        ))}
      </List>

      {totalPages > 1 && (
        <Box sx={{ mt: 2, display: 'flex', justifyContent: 'center' }}>
          <Pagination
            count={totalPages}
            page={currentPage}
            onChange={handlePageChange}
            color="primary"
          />
        </Box>
      )}

      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
      >
        {selectedComment?.author.id === user?.id ? (
          <>
            <MenuItem onClick={handleMenuClose}>수정</MenuItem>
            <MenuItem onClick={handleMenuClose}>삭제</MenuItem>
          </>
        ) : (
          <MenuItem onClick={handleMenuClose}>신고</MenuItem>
        )}
      </Menu>
    </Box>
  );
};

export default CommentSection; 