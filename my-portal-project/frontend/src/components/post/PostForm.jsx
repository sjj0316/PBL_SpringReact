import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import {
  Container,
  Paper,
  Typography,
  Box,
  TextField,
  Button,
  IconButton,
} from '@mui/material';
import { Delete as DeleteIcon } from '@mui/icons-material';
import { createNewPost, updateExistingPost, fetchPost } from '../../store/slices/postSlice';
import { uploadNewFile } from '../../store/slices/fileSlice';
import { showToast } from '../../store/slices/uiSlice';
import FileUpload from '../file/FileUpload';

const PostForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const isEdit = Boolean(id);
  const { currentPost: post, loading } = useSelector((state) => state.post);
  const [formData, setFormData] = useState({
    title: '',
    content: '',
  });
  const [files, setFiles] = useState([]);
  const [uploadedFiles, setUploadedFiles] = useState([]);

  useEffect(() => {
    if (isEdit) {
      loadPost();
    }
  }, [id]);

  useEffect(() => {
    if (post) {
      setFormData({
        title: post.title,
        content: post.content,
      });
      setUploadedFiles(post.files || []);
    }
  }, [post]);

  const loadPost = async () => {
    try {
      await dispatch(fetchPost(id)).unwrap();
    } catch (error) {
      dispatch(
        showToast({
          message: error.message || '게시글을 불러오는데 실패했습니다.',
          type: 'error',
        })
      );
      navigate('/posts');
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleFileUpload = async (file) => {
    try {
      const result = await dispatch(
        uploadNewFile({
          file,
          onProgress: (progress) => {
            // 업로드 진행률 처리
          },
        })
      ).unwrap();
      setUploadedFiles((prev) => [...prev, result]);
      return result;
    } catch (error) {
      dispatch(
        showToast({
          message: error.message || '파일 업로드에 실패했습니다.',
          type: 'error',
        })
      );
      throw error;
    }
  };

  const handleFileDelete = (fileId) => {
    setUploadedFiles((prev) => prev.filter((file) => file.id !== fileId));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const postData = {
        ...formData,
        fileIds: uploadedFiles.map((file) => file.id),
      };

      if (isEdit) {
        await dispatch(updateExistingPost({ id, postData })).unwrap();
        dispatch(
          showToast({
            message: '게시글이 수정되었습니다.',
            type: 'success',
          })
        );
      } else {
        await dispatch(createNewPost(postData)).unwrap();
        dispatch(
          showToast({
            message: '게시글이 작성되었습니다.',
            type: 'success',
          })
        );
      }

      navigate('/posts');
    } catch (error) {
      dispatch(
        showToast({
          message: error.message || '게시글 저장에 실패했습니다.',
          type: 'error',
        })
      );
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Paper sx={{ p: 3 }}>
        <Typography variant="h4" gutterBottom>
          {isEdit ? '게시글 수정' : '게시글 작성'}
        </Typography>

        <Box component="form" onSubmit={handleSubmit}>
          <TextField
            fullWidth
            label="제목"
            name="title"
            value={formData.title}
            onChange={handleChange}
            required
            margin="normal"
          />

          <TextField
            fullWidth
            label="내용"
            name="content"
            value={formData.content}
            onChange={handleChange}
            required
            multiline
            rows={10}
            margin="normal"
          />

          <Box sx={{ mt: 3 }}>
            <Typography variant="h6" gutterBottom>
              첨부파일
            </Typography>
            <FileUpload onUpload={handleFileUpload} />
            {uploadedFiles.length > 0 && (
              <Box sx={{ mt: 2 }}>
                {uploadedFiles.map((file) => (
                  <Box
                    key={file.id}
                    sx={{
                      display: 'flex',
                      alignItems: 'center',
                      mb: 1,
                    }}
                  >
                    <Typography variant="body2" sx={{ flexGrow: 1 }}>
                      {file.originalName}
                    </Typography>
                    <IconButton
                      size="small"
                      color="error"
                      onClick={() => handleFileDelete(file.id)}
                    >
                      <DeleteIcon />
                    </IconButton>
                  </Box>
                ))}
              </Box>
            )}
          </Box>

          <Box sx={{ mt: 3, display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
            <Button
              variant="outlined"
              onClick={() => navigate('/posts')}
            >
              취소
            </Button>
            <Button
              type="submit"
              variant="contained"
              disabled={loading}
            >
              {isEdit ? '수정' : '작성'}
            </Button>
          </Box>
        </Box>
      </Paper>
    </Container>
  );
};

export default PostForm; 