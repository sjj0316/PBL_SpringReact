import { useState, useEffect } from "react";
import {
  Box,
  TextField,
  Button,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Typography,
  Divider,
  Paper,
  Alert,
} from "@mui/material";
import {
  Send as SendIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
} from "@mui/icons-material";
import { useAuth } from "../contexts/AuthContext";
import { getComments, createComment, updateComment, deleteComment } from "../api/commentApi";
import LoadingSpinner from "./common/LoadingSpinner";
import ErrorMessage from "./common/ErrorMessage";

// 로그인한 사용자명 가져오기 (예: localStorage 저장)
function getUsername() {
    return localStorage.getItem("username"); // 로그인 시 저장했다고 가정
}

export default function CommentSection({ postId }) {
  const { user } = useAuth();
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [editingComment, setEditingComment] = useState(null);
  const [editText, setEditText] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const username = getUsername(); // 현재 로그인 사용자

  useEffect(() => {
    fetchComments();
  }, [postId]);

  const fetchComments = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await getComments(postId);
      setComments(data);
    } catch (err) {
      setError(err.message || '댓글을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;

    try {
      const comment = await createComment(postId, newComment);
      setComments([...comments, comment]);
      setNewComment('');
    } catch (err) {
      setError(err.message || '댓글 작성에 실패했습니다.');
    }
  };

  const handleEdit = async (commentId) => {
    if (!editText.trim()) return;

    try {
      const updatedComment = await updateComment(commentId, editText);
      setComments(comments.map(comment =>
        comment.id === commentId ? updatedComment : comment
      ));
      setEditingComment(null);
      setEditText('');
    } catch (err) {
      setError(err.message || '댓글 수정에 실패했습니다.');
    }
  };

  const handleDelete = async (commentId) => {
    if (!window.confirm('정말로 이 댓글을 삭제하시겠습니까?')) return;

    try {
      await deleteComment(commentId);
      setComments(comments.filter(comment => comment.id !== commentId));
    } catch (err) {
      setError(err.message || '댓글 삭제에 실패했습니다.');
    }
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <ErrorMessage
        title="댓글 로딩 실패"
        message={error}
        onRetry={fetchComments}
      />
    );
  }

  return (
    <Box>
      <Typography variant="h6" sx={{ mb: 2 }}>
        댓글 {comments.length}개
      </Typography>

      {user && (
        <Box
          component="form"
          onSubmit={handleSubmit}
          sx={{
            display: 'flex',
            gap: 1,
            mb: 3,
          }}
        >
          <TextField
            fullWidth
            multiline
            rows={2}
            placeholder="댓글을 작성하세요"
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            variant="outlined"
            size="small"
          />
          <Button
            type="submit"
            variant="contained"
            endIcon={<SendIcon />}
            disabled={!newComment.trim()}
          >
            작성
          </Button>
        </Box>
      )}

      <List>
        {comments.map((comment) => (
          <Box key={comment.id}>
            <ListItem alignItems="flex-start">
              <ListItemText
                primary={
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Typography variant="subtitle2">
                      {comment.author}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {new Date(comment.createdAt).toLocaleDateString()}
                    </Typography>
                  </Box>
                }
                secondary={
                  editingComment === comment.id ? (
                    <Box sx={{ mt: 1 }}>
                      <TextField
                        fullWidth
                        multiline
                        rows={2}
                        value={editText}
                        onChange={(e) => setEditText(e.target.value)}
                        variant="outlined"
                        size="small"
                        sx={{ mb: 1 }}
                      />
                      <Box sx={{ display: 'flex', gap: 1 }}>
                        <Button
                          size="small"
                          onClick={() => handleEdit(comment.id)}
                          disabled={!editText.trim()}
                        >
                          저장
                        </Button>
                        <Button
                          size="small"
                          onClick={() => {
                            setEditingComment(null);
                            setEditText('');
                          }}
                        >
                          취소
                        </Button>
                      </Box>
                    </Box>
                  ) : (
                    <Typography
                      variant="body2"
                      sx={{ mt: 1, whiteSpace: 'pre-wrap' }}
                    >
                      {comment.content}
                    </Typography>
                  )
                }
              />
              {user && (user.username === comment.author || user.isAdmin) && (
                <ListItemSecondaryAction>
                  {editingComment !== comment.id && (
                    <>
                      <IconButton
                        edge="end"
                        size="small"
                        onClick={() => {
                          setEditingComment(comment.id);
                          setEditText(comment.content);
                        }}
                        sx={{ mr: 1 }}
                      >
                        <EditIcon fontSize="small" />
                      </IconButton>
                      <IconButton
                        edge="end"
                        size="small"
                        onClick={() => handleDelete(comment.id)}
                      >
                        <DeleteIcon fontSize="small" />
                      </IconButton>
                    </>
                  )}
                </ListItemSecondaryAction>
              )}
            </ListItem>
            <Divider variant="inset" component="li" />
          </Box>
        ))}
      </List>
    </Box>
  );
}
