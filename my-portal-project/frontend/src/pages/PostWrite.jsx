// src/pages/PostWrite.jsx
import { useState, useEffect } from "react";
import { createPost } from "../api/postApi";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  Alert,
  Snackbar,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from "@mui/material";
import { ArrowBack as ArrowBackIcon } from "@mui/icons-material";
import { useAuth } from "../contexts/AuthContext";
import FileUpload from "../components/FileUpload";

export default function PostWrite() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [category, setCategory] = useState("");
  const [files, setFiles] = useState([]);
  const [error, setError] = useState("");
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const navigate = useNavigate();
  const { user } = useAuth();

  useEffect(() => {
    if (!user) {
      navigate('/login', { state: { from: '/write' } });
    }
  }, [user, navigate]);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");

    if (!title.trim() || !content.trim()) {
      setError("제목과 내용을 모두 입력해주세요.");
      return;
    }

    if (!category) {
      setError("카테고리를 선택해주세요.");
      return;
    }

    try {
      await createPost({ title, content, category }, files);
      setOpenSnackbar(true);
      setTimeout(() => navigate("/"), 1500);
    } catch (err) {
      setError("게시글 등록에 실패했습니다. 다시 시도해주세요.");
    }
  }

  if (!user) {
    return null;
  }

  return (
    <Box>
      <Box sx={{ mb: 3, display: 'flex', alignItems: 'center' }}>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate(-1)}
          sx={{ mr: 2 }}
        >
          뒤로가기
        </Button>
        <Typography variant="h5" component="h1">
          새 글 작성
        </Typography>
      </Box>

      <Paper elevation={2} sx={{ p: 3 }}>
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <form onSubmit={handleSubmit}>
          <TextField
            fullWidth
            label="제목"
            variant="outlined"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            sx={{ mb: 2 }}
            required
          />

          <FormControl fullWidth sx={{ mb: 2 }}>
            <InputLabel>카테고리</InputLabel>
            <Select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              label="카테고리"
              required
            >
              <MenuItem value="notice">공지사항</MenuItem>
              <MenuItem value="free">자유게시판</MenuItem>
              <MenuItem value="qna">Q&A</MenuItem>
            </Select>
          </FormControl>

          <TextField
            fullWidth
            label="내용"
            variant="outlined"
            multiline
            rows={10}
            value={content}
            onChange={(e) => setContent(e.target.value)}
            sx={{ mb: 3 }}
            required
          />

          <Box sx={{ mb: 3 }}>
            <Typography variant="subtitle1" sx={{ mb: 1 }}>
              첨부파일
            </Typography>
            <FileUpload onFilesChange={setFiles} />
          </Box>

          <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
            <Button
              variant="outlined"
              onClick={() => navigate(-1)}
            >
              취소
            </Button>
            <Button
              type="submit"
              variant="contained"
              color="primary"
            >
              등록
            </Button>
          </Box>
        </form>
      </Paper>

      <Snackbar
        open={openSnackbar}
        autoHideDuration={1500}
        message="게시글이 등록되었습니다."
      />
    </Box>
  );
}
