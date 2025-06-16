import React, { memo } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Card,
  CardContent,
  CardActions,
  Typography,
  Button,
  Chip,
  Box,
} from '@mui/material';
import {
  ThumbUp as ThumbUpIcon,
  Comment as CommentIcon,
  AccessTime as AccessTimeIcon,
} from '@mui/icons-material';
import { formatDistanceToNow } from 'date-fns';
import { ko } from 'date-fns/locale';

const PostItem = memo(({ post }) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/posts/${post.id}`);
  };

  return (
    <Card
      sx={{
        mb: 2,
        cursor: 'pointer',
        '&:hover': {
          boxShadow: 6,
        },
      }}
      onClick={handleClick}
    >
      <CardContent>
        <Typography variant="h6" gutterBottom noWrap>
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
        <Box sx={{ mt: 2, display: 'flex', gap: 1, flexWrap: 'wrap' }}>
          <Chip
            icon={<AccessTimeIcon />}
            label={formatDistanceToNow(new Date(post.createdAt), {
              addSuffix: true,
              locale: ko,
            })}
            size="small"
          />
          <Chip
            icon={<ThumbUpIcon />}
            label={`좋아요 ${post.likeCount}`}
            size="small"
          />
          <Chip
            icon={<CommentIcon />}
            label={`댓글 ${post.commentCount}`}
            size="small"
          />
        </Box>
      </CardContent>
      <CardActions>
        <Button size="small" color="primary">
          자세히 보기
        </Button>
      </CardActions>
    </Card>
  );
});

PostItem.displayName = 'PostItem';

export default PostItem; 