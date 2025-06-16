import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import {
  uploadFile,
  getFile,
  deleteFile,
  getFiles,
} from '../../api/fileApi';

// 비동기 액션 생성
export const uploadNewFile = createAsyncThunk(
  'file/uploadFile',
  async ({ file, onProgress }, { rejectWithValue }) => {
    try {
      const response = await uploadFile(file, onProgress);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

export const fetchFile = createAsyncThunk(
  'file/fetchFile',
  async (fileId, { rejectWithValue }) => {
    try {
      const response = await getFile(fileId);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

export const deleteExistingFile = createAsyncThunk(
  'file/deleteFile',
  async (fileId, { rejectWithValue }) => {
    try {
      await deleteFile(fileId);
      return fileId;
    } catch (error) {
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

export const fetchFiles = createAsyncThunk(
  'file/fetchFiles',
  async (params, { rejectWithValue }) => {
    try {
      const response = await getFiles(params);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

const initialState = {
  files: [],
  currentFile: null,
  totalPages: 0,
  currentPage: 1,
  loading: false,
  error: null,
  uploadProgress: 0,
};

const fileSlice = createSlice({
  name: 'file',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearCurrentFile: (state) => {
      state.currentFile = null;
    },
    clearUploadProgress: (state) => {
      state.uploadProgress = 0;
    },
    setUploadProgress: (state, action) => {
      state.uploadProgress = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      // 파일 업로드
      .addCase(uploadNewFile.pending, (state) => {
        state.loading = true;
        state.error = null;
        state.uploadProgress = 0;
      })
      .addCase(uploadNewFile.fulfilled, (state, action) => {
        state.loading = false;
        state.files.unshift(action.payload);
        state.uploadProgress = 100;
      })
      .addCase(uploadNewFile.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
        state.uploadProgress = 0;
      })
      // 파일 상세 조회
      .addCase(fetchFile.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchFile.fulfilled, (state, action) => {
        state.loading = false;
        state.currentFile = action.payload;
      })
      .addCase(fetchFile.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // 파일 삭제
      .addCase(deleteExistingFile.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteExistingFile.fulfilled, (state, action) => {
        state.loading = false;
        state.files = state.files.filter(file => file.id !== action.payload);
        if (state.currentFile?.id === action.payload) {
          state.currentFile = null;
        }
      })
      .addCase(deleteExistingFile.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // 파일 목록 조회
      .addCase(fetchFiles.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchFiles.fulfilled, (state, action) => {
        state.loading = false;
        state.files = action.payload.content;
        state.totalPages = action.payload.totalPages;
        state.currentPage = action.payload.number + 1;
      })
      .addCase(fetchFiles.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const {
  clearError,
  clearCurrentFile,
  clearUploadProgress,
  setUploadProgress,
} = fileSlice.actions;
export default fileSlice.reducer; 