import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import {
  Container,
  Box,
  Typography,
  TextField,
  Button,
  Paper,
  IconButton,
} from "@mui/material";
import { ArrowBack as ArrowBackIcon } from "@mui/icons-material";
import { fetchPost, updatePost } from "../store/slices/postSlice";
import { showToast } from "../store/slices/uiSlice";
import Loading from "../components/common/Loading";

const PostEdit = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { currentPost: post, loading, error } = useSelector((state) => state.posts);
  const [formData, setFormData] = useState({
    title: '',
    content: '',
  });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    dispatch(fetchPost(id));
  }, [dispatch, id]);

  useEffect(() => {
    if (post) {
      setFormData({
        title: post.title,
        content: post.content,
      });
    }
  }, [post]);

  const validateForm = () => {
    const newErrors = {};
    if (!formData.title.trim()) {
      newErrors.title = '제목을 입력해주세요';
    }
    if (!formData.content.trim()) {
      newErrors.content = '내용을 입력해주세요';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    // 에러 메시지 초기화
    if (errors[name]) {
      setErrors((prev) => ({
        ...prev,
        [name]: '',
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
      await dispatch(updatePost({ id, ...formData })).unwrap();
      showToast('게시글이 수정되었습니다.', 'success');
      navigate(`/posts/${id}`);
    } catch (error) {
      showToast(error.message || '게시글 수정에 실패했습니다.', 'error');
    }
  };

  const handleBack = () => {
    navigate(`/posts/${id}`);
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
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
          <IconButton onClick={handleBack} sx={{ mr: 2 }}>
            <ArrowBackIcon />
          </IconButton>
          <Typography variant="h4" component="h1">
            게시글 수정
          </Typography>
        </Box>

        <Paper sx={{ p: 3 }}>
          <Box component="form" onSubmit={handleSubmit}>
            <TextField
              fullWidth
              label="제목"
              name="title"
              value={formData.title}
              onChange={handleChange}
              error={!!errors.title}
              helperText={errors.title}
              sx={{ mb: 3 }}
            />
            <TextField
              fullWidth
              label="내용"
              name="content"
              value={formData.content}
              onChange={handleChange}
              error={!!errors.content}
              helperText={errors.content}
              multiline
              rows={10}
              sx={{ mb: 3 }}
            />
            <Box sx={{ display: 'flex', gap: 1, justifyContent: 'flex-end' }}>
              <Button
                variant="outlined"
                onClick={handleBack}
              >
                취소
              </Button>
              <Button
                type="submit"
                variant="contained"
              >
                수정
              </Button>
            </Box>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
};

export default PostEdit; 