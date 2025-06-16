import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import {
  getPosts,
  getPost,
  createPost,
  updatePost,
  deletePost,
  likePost,
  unlikePost,
  searchPosts,
} from '../../api/postApi';

// 비동기 액션 생성
export const fetchPosts = createAsyncThunk(
  'post/fetchPosts',
  async (params, { rejectWithValue }) => {
    try {
      const response = await getPosts(params);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

export const fetchPost = createAsyncThunk(
  'post/fetchPost',
  async (id, { rejectWithValue }) => {
    try {
      const response = await getPost(id);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

export const createNewPost = createAsyncThunk(
  'post/createPost',
  async (postData, { rejectWithValue }) => {
    try {
      const response = await createPost(postData);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

export const updateExistingPost = createAsyncThunk(
  'post/updatePost',
  async ({ id, postData }, { rejectWithValue }) => {
    try {
      const response = await updatePost(id, postData);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

export const deleteExistingPost = createAsyncThunk(
  'post/deletePost',
  async (id, { rejectWithValue }) => {
    try {
      await deletePost(id);
      return id;
    } catch (error) {
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

export const togglePostLike = createAsyncThunk(
  'post/toggleLike',
  async (id, { getState, rejectWithValue }) => {
    try {
      const { post } = getState();
      const postData = post.currentPost;
      
      if (postData.liked) {
        await unlikePost(id);
        return { id, liked: false };
      } else {
        await likePost(id);
        return { id, liked: true };
      }
    } catch (error) {
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

export const searchPostsByKeyword = createAsyncThunk(
  'post/searchPosts',
  async ({ keyword, params }, { rejectWithValue }) => {
    try {
      const response = await searchPosts(keyword, params);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

const initialState = {
  posts: [],
  currentPost: null,
  totalPages: 0,
  currentPage: 1,
  loading: false,
  error: null,
  searchResults: [],
  searchLoading: false,
  searchError: null,
};

const postSlice = createSlice({
  name: 'post',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
      state.searchError = null;
    },
    clearCurrentPost: (state) => {
      state.currentPost = null;
    },
    clearSearchResults: (state) => {
      state.searchResults = [];
    },
  },
  extraReducers: (builder) => {
    builder
      // 게시글 목록 조회
      .addCase(fetchPosts.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchPosts.fulfilled, (state, action) => {
        state.loading = false;
        state.posts = action.payload.content;
        state.totalPages = action.payload.totalPages;
        state.currentPage = action.payload.number + 1;
      })
      .addCase(fetchPosts.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // 게시글 상세 조회
      .addCase(fetchPost.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchPost.fulfilled, (state, action) => {
        state.loading = false;
        state.currentPost = action.payload;
      })
      .addCase(fetchPost.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // 게시글 작성
      .addCase(createNewPost.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createNewPost.fulfilled, (state, action) => {
        state.loading = false;
        state.posts.unshift(action.payload);
      })
      .addCase(createNewPost.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // 게시글 수정
      .addCase(updateExistingPost.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateExistingPost.fulfilled, (state, action) => {
        state.loading = false;
        state.currentPost = action.payload;
        state.posts = state.posts.map(post =>
          post.id === action.payload.id ? action.payload : post
        );
      })
      .addCase(updateExistingPost.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // 게시글 삭제
      .addCase(deleteExistingPost.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteExistingPost.fulfilled, (state, action) => {
        state.loading = false;
        state.posts = state.posts.filter(post => post.id !== action.payload);
        if (state.currentPost?.id === action.payload) {
          state.currentPost = null;
        }
      })
      .addCase(deleteExistingPost.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // 좋아요 토글
      .addCase(togglePostLike.fulfilled, (state, action) => {
        const { id, liked } = action.payload;
        if (state.currentPost?.id === id) {
          state.currentPost.liked = liked;
          state.currentPost.likeCount += liked ? 1 : -1;
        }
        state.posts = state.posts.map(post =>
          post.id === id
            ? { ...post, liked, likeCount: post.likeCount + (liked ? 1 : -1) }
            : post
        );
      })
      // 게시글 검색
      .addCase(searchPostsByKeyword.pending, (state) => {
        state.searchLoading = true;
        state.searchError = null;
      })
      .addCase(searchPostsByKeyword.fulfilled, (state, action) => {
        state.searchLoading = false;
        state.searchResults = action.payload.content;
      })
      .addCase(searchPostsByKeyword.rejected, (state, action) => {
        state.searchLoading = false;
        state.searchError = action.payload;
      });
  },
});

export const { clearError, clearCurrentPost, clearSearchResults } = postSlice.actions;
export default postSlice.reducer; 