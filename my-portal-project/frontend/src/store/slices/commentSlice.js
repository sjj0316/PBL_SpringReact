import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import {
  fetchComments as fetchCommentsApi,
  createComment as createCommentApi,
  updateComment as updateCommentApi,
  deleteComment as deleteCommentApi,
  likeComment as likeCommentApi,
  unlikeComment as unlikeCommentApi,
  reportComment,
} from '../../api/commentApi';

const initialState = {
  comments: [],
  loading: false,
  error: null,
  currentPage: 1,
  totalPages: 1,
};

export const fetchComments = createAsyncThunk(
  'comments/fetchComments',
  async ({ postId, page }) => {
    const response = await fetchCommentsApi(postId, page);
    return response.data;
  }
);

export const createComment = createAsyncThunk(
  'comments/createComment',
  async ({ postId, content }) => {
    const response = await createCommentApi(postId, content);
    return response.data;
  }
);

export const updateComment = createAsyncThunk(
  'comments/updateComment',
  async ({ postId, commentId, content }) => {
    const response = await updateCommentApi(postId, commentId, content);
    return response.data;
  }
);

export const deleteComment = createAsyncThunk(
  'comments/deleteComment',
  async ({ postId, commentId }) => {
    await deleteCommentApi(postId, commentId);
    return commentId;
  }
);

export const toggleCommentLike = createAsyncThunk(
  'comments/toggleCommentLike',
  async ({ postId, commentId }) => {
    const response = await likeCommentApi(postId, commentId);
    return response.data;
  }
);

export const reportExistingComment = createAsyncThunk(
  'comment/reportComment',
  async ({ postId, commentId, reportData }, { rejectWithValue }) => {
    try {
      const response = await reportComment(postId, commentId, reportData);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

const commentSlice = createSlice({
  name: 'comments',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchComments.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchComments.fulfilled, (state, action) => {
        state.loading = false;
        state.comments = action.payload.comments;
        state.currentPage = action.payload.currentPage;
        state.totalPages = action.payload.totalPages;
      })
      .addCase(fetchComments.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(createComment.fulfilled, (state, action) => {
        state.comments.unshift(action.payload);
      })
      .addCase(updateComment.fulfilled, (state, action) => {
        const index = state.comments.findIndex(comment => comment.id === action.payload.id);
        if (index !== -1) {
          state.comments[index] = action.payload;
        }
      })
      .addCase(deleteComment.fulfilled, (state, action) => {
        state.comments = state.comments.filter(comment => comment.id !== action.payload);
      })
      .addCase(toggleCommentLike.fulfilled, (state, action) => {
        const comment = state.comments.find(c => c.id === action.payload.id);
        if (comment) {
          comment.likeCount = action.payload.likeCount;
          comment.isLiked = action.payload.isLiked;
        }
      })
      .addCase(reportExistingComment.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(reportExistingComment.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(reportExistingComment.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { clearError } = commentSlice.actions;
export default commentSlice.reducer; 