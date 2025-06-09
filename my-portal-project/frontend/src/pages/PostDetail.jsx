// src/pages/PostDetail.jsx
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Button,
  Divider,
  Chip,
} from '@mui/material';
import {
  ArrowBack as ArrowBackIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import { getPost, deletePost } from '../api/postApi';
import CommentSection from '../components/CommentSection';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import PageContainer from '../components/common/PageContainer';

export default function PostDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [post, setPost] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchPost();
  }, [id]);

  const fetchPost = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await getPost(id);
      setPost(data);
    } catch (err) {
      setError(err.message || '게시글을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (window.confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
      try {
        await deletePost(id);
        navigate('/posts');
      } catch (err) {
        setError(err.message || '게시글 삭제에 실패했습니다.');
      }
    }
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <ErrorMessage
        title="게시글 로딩 실패"
        message={error}
        onRetry={fetchPost}
      />
    );
  }

  if (!post) {
    return (
      <ErrorMessage
        title="게시글을 찾을 수 없습니다"
        message="요청하신 게시글이 존재하지 않거나 삭제되었을 수 있습니다."
        onRetry={() => navigate('/posts')}
      />
    );
  }

  const isAuthor = user && post.author === user.username;

  return (
    <PageContainer>
      <Box sx={{ mb: 4 }}>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/posts')}
          sx={{ mb: 2 }}
        >
          목록으로 돌아가기
        </Button>

        <Box sx={{ mb: 3 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
            <Typography variant="h4" component="h1" sx={{ flex: 1 }}>
              {post.title}
            </Typography>
            <Chip
              label={post.category}
              color="primary"
              size="small"
              sx={{ ml: 2 }}
            />
          </Box>

          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
            <Typography variant="body2" color="text.secondary">
              작성자: {post.author} | 작성일: {new Date(post.createdAt).toLocaleDateString()}
            </Typography>
            {isAuthor && (
              <Box>
                <Button
                  startIcon={<EditIcon />}
                  onClick={() => navigate(`/posts/${id}/edit`)}
                  sx={{ mr: 1 }}
                >
                  수정
                </Button>
                <Button
                  startIcon={<DeleteIcon />}
                  color="error"
                  onClick={handleDelete}
                >
                  삭제
                </Button>
              </Box>
            )}
          </Box>
        </Box>

        <Divider sx={{ my: 3 }} />

        <Typography
          variant="body1"
          sx={{
            whiteSpace: 'pre-wrap',
            minHeight: '200px',
            mb: 4,
          }}
        >
          {post.content}
        </Typography>

        <Divider sx={{ my: 3 }} />

        <CommentSection postId={id} />
      </Box>
    </PageContainer>
  );
}
