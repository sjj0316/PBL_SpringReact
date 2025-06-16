import { useState, useEffect, useCallback } from "react";
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
  Pagination,
  Menu,
  MenuItem,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  InputLabel,
  Select,
} from "@mui/material";
import {
  Delete as DeleteIcon,
  Edit as EditIcon,
  ThumbUp as ThumbUpIcon,
  ThumbUpOutlined as ThumbUpOutlinedIcon,
  MoreVert as MoreVertIcon,
  Flag as FlagIcon,
  Sort as SortIcon,
} from "@mui/icons-material";
import { useAuth } from "../contexts/AuthContext";
import {
  getComments,
  createComment,
  updateComment,
  deleteComment,
  likeComment,
  unlikeComment,
  reportComment,
} from "../api/commentApi";

const COMMENTS_PER_PAGE = 10;
const SORT_OPTIONS = [
  { value: "newest", label: "최신순" },
  { value: "oldest", label: "오래된순" },
  { value: "likes", label: "좋아요순" },
];

export default function CommentSection({ postId }) {
  const { user } = useAuth();
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [editingComment, setEditingComment] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [sortBy, setSortBy] = useState("newest");
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedComment, setSelectedComment] = useState(null);
  const [reportDialogOpen, setReportDialogOpen] = useState(false);
  const [reportReason, setReportReason] = useState("");

  const fetchComments = useCallback(async () => {
    try {
      setLoading(true);
      const data = await getComments(postId, {
        page: page - 1,
        size: COMMENTS_PER_PAGE,
        sort: sortBy,
      });
      setComments(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      setError("댓글을 불러오는데 실패했습니다.");
    } finally {
      setLoading(false);
    }
  }, [postId, page, sortBy]);

  useEffect(() => {
    fetchComments();
  }, [fetchComments]);

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
        setComments(prev => [response, ...prev]);
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

  const handlePageChange = (event, value) => {
    setPage(value);
  };

  const handleSortChange = (event) => {
    setSortBy(event.target.value);
    setPage(1);
  };

  const handleMenuOpen = (event, comment) => {
    setAnchorEl(event.currentTarget);
    setSelectedComment(comment);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedComment(null);
  };

  const handleReportClick = () => {
    setReportDialogOpen(true);
    handleMenuClose();
  };

  const handleReportSubmit = async () => {
    if (!reportReason.trim()) return;

    try {
      await reportComment(selectedComment.id, { reason: reportReason });
      setReportDialogOpen(false);
      setReportReason("");
      setError(null);
    } catch (err) {
      setError("댓글 신고에 실패했습니다.");
    }
  };

  const handleLike = async (commentId) => {
    try {
      const comment = comments.find(c => c.id === commentId);
      if (comment.liked) {
        await unlikeComment(commentId);
        setComments(prev =>
          prev.map(c =>
            c.id === commentId
              ? { ...c, liked: false, likeCount: c.likeCount - 1 }
              : c
          )
        );
      } else {
        await likeComment(commentId);
        setComments(prev =>
          prev.map(c =>
            c.id === commentId
              ? { ...c, liked: true, likeCount: c.likeCount + 1 }
              : c
          )
        );
      }
    } catch (err) {
      setError("좋아요 처리에 실패했습니다.");
    }
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
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h6">
          댓글 {comments.length}개
        </Typography>
        <FormControl size="small" sx={{ minWidth: 120 }}>
          <InputLabel>정렬</InputLabel>
          <Select
            value={sortBy}
            label="정렬"
            onChange={handleSortChange}
            startAdornment={<SortIcon sx={{ mr: 1 }} />}
          >
            {SORT_OPTIONS.map(option => (
              <MenuItem key={option.value} value={option.value}>
                {option.label}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>

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
              <ListItemSecondaryAction>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <Tooltip title={comment.liked ? "좋아요 취소" : "좋아요"}>
                    <IconButton
                      onClick={() => handleLike(comment.id)}
                      color={comment.liked ? "primary" : "default"}
                    >
                      {comment.liked ? <ThumbUpIcon /> : <ThumbUpOutlinedIcon />}
                    </IconButton>
                  </Tooltip>
                  <Typography variant="caption" sx={{ mr: 1 }}>
                    {comment.likeCount}
                  </Typography>
                  {user && (user.id === comment.author.id || user.role === "ADMIN") ? (
                    <>
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
                    </>
                  ) : (
                    <IconButton
                      edge="end"
                      aria-label="more"
                      onClick={(e) => handleMenuOpen(e, comment)}
                    >
                      <MoreVertIcon />
                    </IconButton>
                  )}
                </Box>
              </ListItemSecondaryAction>
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

      {totalPages > 1 && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
          <Pagination
            count={totalPages}
            page={page}
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
        <MenuItem onClick={handleReportClick}>
          <FlagIcon sx={{ mr: 1 }} />
          신고하기
        </MenuItem>
      </Menu>

      <Dialog
        open={reportDialogOpen}
        onClose={() => setReportDialogOpen(false)}
      >
        <DialogTitle>댓글 신고</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            multiline
            rows={3}
            placeholder="신고 사유를 입력하세요..."
            value={reportReason}
            onChange={(e) => setReportReason(e.target.value)}
            sx={{ mt: 2 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setReportDialogOpen(false)}>
            취소
          </Button>
          <Button
            onClick={handleReportSubmit}
            variant="contained"
            disabled={!reportReason.trim()}
          >
            신고하기
          </Button>
        </DialogActions>
      </Dialog>
    </Paper>
  );
}
