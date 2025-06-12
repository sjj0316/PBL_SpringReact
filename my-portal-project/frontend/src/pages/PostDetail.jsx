// src/pages/PostDetail.jsx
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Paper,
  Typography,
  Button,
  IconButton,
  Divider,
  Chip,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  ArrowBack as ArrowBackIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  ThumbUp as ThumbUpIcon,
  Share as ShareIcon,
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import { getPost, deletePost, addLike, removeLike } from '../api/postApi';
import CommentSection from '../components/CommentSection';

export default function PostDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [post, setPost] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [liked, setLiked] = useState(false);

  useEffect(() => {
    fetchPost();
  }, [id]);

  const fetchPost = async () => {
    try {
      setLoading(true);
      const data = await getPost(id);
      setPost(data);
      setLiked(data.liked);
    } catch (err) {
      setError("게시글을 불러오는데 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (window.confirm("정말로 이 게시글을 삭제하시겠습니까?")) {
      try {
        await deletePost(id);
        navigate("/");
      } catch (err) {
        setError("게시글 삭제에 실패했습니다.");
      }
    }
  };

  const handleLike = async () => {
    try {
      if (liked) {
        await removeLike(id);
      } else {
        await addLike(id);
      }
      setLiked(!liked);
      setPost(prev => ({
        ...prev,
        likeCount: liked ? prev.likeCount - 1 : prev.likeCount + 1
      }));
    } catch (err) {
      setError("좋아요 처리에 실패했습니다.");
    }
  };

  const handleShare = () => {
    navigator.clipboard.writeText(window.location.href);
    alert("링크가 클립보드에 복사되었습니다.");
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">{error}</Alert>
      </Box>
    );
  }

  if (!post) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="info">게시글을 찾을 수 없습니다.</Alert>
      </Box>
    );
  }

  return (
    <Box>
      <Box sx={{ mb: 3, display: 'flex', alignItems: 'center', gap: 2 }}>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate(-1)}
        >
          뒤로가기
        </Button>
        {user && user.id === post.author.id && (
          <Box sx={{ display: 'flex', gap: 1 }}>
            <Button
              variant="outlined"
              startIcon={<EditIcon />}
              onClick={() => navigate(`/edit/${id}`)}
            >
              수정
            </Button>
            <Button
              variant="outlined"
              color="error"
              startIcon={<DeleteIcon />}
              onClick={handleDelete}
            >
              삭제
            </Button>
          </Box>
        )}
      </Box>

      <Paper elevation={2} sx={{ p: 3, mb: 3 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          {post.title}
        </Typography>

        <Box sx={{ mb: 2, display: 'flex', alignItems: 'center', gap: 1 }}>
          <Chip label={post.category} color="primary" size="small" />
          <Typography variant="body2" color="text.secondary">
            작성자: {post.author.username}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            작성일: {new Date(post.createdAt).toLocaleDateString()}
          </Typography>
        </Box>

        <Divider sx={{ my: 2 }} />

        <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap', mb: 3 }}>
          {post.content}
        </Typography>

        {post.files && post.files.length > 0 && (
          <Box sx={{ mb: 3 }}>
            <Typography variant="subtitle1" gutterBottom>
              첨부파일
            </Typography>
            {post.files.map((file) => (
              <Box
                key={file.id}
                sx={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: 1,
                  mb: 1,
                }}
              >
                <a
                  href={file.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  style={{ textDecoration: 'none' }}
                >
                  {file.originalName}
                </a>
              </Box>
            ))}
          </Box>
        )}

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <IconButton onClick={handleLike} color={liked ? "primary" : "default"}>
            <ThumbUpIcon />
            <Typography variant="body2" sx={{ ml: 0.5 }}>
              {post.likeCount}
            </Typography>
          </IconButton>
          <IconButton onClick={handleShare}>
            <ShareIcon />
          </IconButton>
        </Box>
      </Paper>

      <CommentSection postId={id} />
    </Box>
  );
}
