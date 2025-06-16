import React, { memo, useCallback } from 'react';
import { useDispatch } from 'react-redux';
import {
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
  IconButton,
  Typography,
  Box,
  Chip,
} from '@mui/material';
import {
  MoreVert as MoreVertIcon,
  ThumbUp as ThumbUpIcon,
  ThumbUpOutlined as ThumbUpOutlinedIcon,
} from '@mui/icons-material';
import { toggleCommentLike } from '../../store/slices/commentSlice';
import { showToast } from '../../store/slices/uiSlice';
import { formatDistanceToNow } from 'date-fns';
import { ko } from 'date-fns/locale';

const CommentItem = memo(({ comment, onMenuOpen, onMenuClose }) => {
  const dispatch = useDispatch();

  const handleLike = useCallback(async () => {
    try {
      await dispatch(toggleCommentLike(comment.id)).unwrap();
    } catch (error) {
      dispatch(
        showToast({
          message: error.message || '좋아요 처리에 실패했습니다.',
          type: 'error',
        })
      );
    }
  }, [dispatch, comment.id]);

  const handleMenuClick = useCallback(
    (event) => {
      event.stopPropagation();
      onMenuOpen(event, comment);
    },
    [onMenuOpen, comment]
  );

  return (
    <ListItem
      alignItems="flex-start"
      sx={{
        '&:hover': {
          bgcolor: 'action.hover',
        },
      }}
    >
      <ListItemAvatar>
        <Avatar alt={comment.author.name} src={comment.author.profileImage} />
      </ListItemAvatar>
      <ListItemText
        primary={
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Typography component="span" variant="subtitle2">
              {comment.author.name}
            </Typography>
            <Typography
              component="span"
              variant="caption"
              color="text.secondary"
            >
              {formatDistanceToNow(new Date(comment.createdAt), {
                addSuffix: true,
                locale: ko,
              })}
            </Typography>
          </Box>
        }
        secondary={
          <React.Fragment>
            <Typography
              component="span"
              variant="body2"
              color="text.primary"
              sx={{ display: 'block', my: 1 }}
            >
              {comment.content}
            </Typography>
            <Box sx={{ display: 'flex', gap: 1 }}>
              <Chip
                icon={comment.liked ? <ThumbUpIcon /> : <ThumbUpOutlinedIcon />}
                label={comment.likeCount}
                onClick={handleLike}
                size="small"
                color={comment.liked ? 'primary' : 'default'}
              />
            </Box>
          </React.Fragment>
        }
      />
    </ListItem>
  );
});

CommentItem.displayName = 'CommentItem';

export default CommentItem; 