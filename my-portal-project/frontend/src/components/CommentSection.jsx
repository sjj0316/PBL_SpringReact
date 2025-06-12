import { useState, useEffect } from "react";
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Divider,
  CircularProgress,
  Alert,
} from "@mui/material";
import {
  Delete as DeleteIcon,
  Edit as EditIcon,
} from "@mui/icons-material";
import { useAuth } from "../contexts/AuthContext";
import {
  getComments,
  createComment,
  updateComment,
  deleteComment,
} from "../api/commentApi";

export default function CommentSection({ postId }) {
  const { user } = useAuth();
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [editingComment, setEditingComment] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchComments();
  }, [postId]);

  const fetchComments = async () => {
    try {
      setLoading(true);
      const data = await getComments(postId);
      setComments(data);
    } catch (err) {
      setError("댓글을 불러오는데 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;

    try {
      if (editingComment) {
        await updateComment(editingComment.id, { content: newComment });
        setComments(prev =>
          prev.map(comment =>
            comment.id === editingComment.id
              ? { ...comment, content: newComment }
              : comment
          )
        );
        setEditingComment(null);
      } else {
        const response = await createComment(postId, { content: newComment });
        setComments(prev => [...prev, response]);
      }
      setNewComment("");
    } catch (err) {
      setError("댓글 저장에 실패했습니다.");
    }
  };

  const handleEdit = (comment) => {
    setEditingComment(comment);
    setNewComment(comment.content);
  };

  const handleDelete = async (commentId) => {
    if (!window.confirm("댓글을 삭제하시겠습니까?")) return;

    try {
      await deleteComment(commentId);
      setComments(prev => prev.filter(comment => comment.id !== commentId));
    } catch (err) {
      setError("댓글 삭제에 실패했습니다.");
    }
  };

  const handleCancel = () => {
    setEditingComment(null);
    setNewComment("");
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Paper elevation={2} sx={{ p: 3 }}>
      <Typography variant="h6" gutterBottom>
        댓글 {comments.length}개
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {user && (
        <Box component="form" onSubmit={handleSubmit} sx={{ mb: 3 }}>
          <TextField
            fullWidth
            multiline
            rows={3}
            placeholder="댓글을 작성하세요..."
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            sx={{ mb: 1 }}
          />
          <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
            {editingComment && (
              <Button onClick={handleCancel}>
                취소
              </Button>
            )}
            <Button
              type="submit"
              variant="contained"
              disabled={!newComment.trim()}
            >
              {editingComment ? "수정" : "작성"}
            </Button>
          </Box>
        </Box>
      )}

      <List>
        {comments.map((comment) => (
          <Box key={comment.id}>
            <ListItem alignItems="flex-start">
              <ListItemText
                primary={
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Typography variant="subtitle2">
                      {comment.author.username}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {new Date(comment.createdAt).toLocaleString()}
                    </Typography>
                  </Box>
                }
                secondary={comment.content}
              />
              {user && (user.id === comment.author.id || user.role === "ADMIN") && (
                <ListItemSecondaryAction>
                  <IconButton
                    edge="end"
                    aria-label="edit"
                    onClick={() => handleEdit(comment)}
                    sx={{ mr: 1 }}
                  >
                    <EditIcon />
                  </IconButton>
                  <IconButton
                    edge="end"
                    aria-label="delete"
                    onClick={() => handleDelete(comment.id)}
                  >
                    <DeleteIcon />
                  </IconButton>
                </ListItemSecondaryAction>
              )}
            </ListItem>
            <Divider variant="inset" component="li" />
          </Box>
        ))}
      </List>

      {comments.length === 0 && (
        <Box sx={{ textAlign: 'center', py: 3 }}>
          <Typography color="text.secondary">
            아직 댓글이 없습니다. 첫 댓글을 작성해보세요!
          </Typography>
        </Box>
      )}
    </Paper>
  );
}
