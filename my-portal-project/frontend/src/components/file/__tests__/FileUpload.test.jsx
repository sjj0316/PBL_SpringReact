import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, mockInitialState } from '../../../utils/test-utils';
import FileUpload from '../FileUpload';

describe('FileUpload Integration', () => {
  const initialState = {
    ...mockInitialState,
    file: {
      ...mockInitialState.file,
      uploadProgress: 0,
      uploading: false,
      error: null,
    },
    auth: {
      ...mockInitialState.auth,
      isAuthenticated: true,
      user: {
        id: 1,
        username: 'user1',
      },
    },
  };

  it('renders file upload interface correctly', () => {
    renderWithProviders(<FileUpload postId={1} />, {
      preloadedState: initialState,
    });

    expect(screen.getByText(/파일 업로드/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /파일 선택/i })).toBeInTheDocument();
  });

  it('handles file selection', async () => {
    const { store } = renderWithProviders(<FileUpload postId={1} />, {
      preloadedState: initialState,
    });

    const file = new File(['test content'], 'test.txt', { type: 'text/plain' });
    const input = screen.getByLabelText(/파일 선택/i);

    await userEvent.upload(input, file);

    await waitFor(() => {
      const actions = store.getActions();
      expect(actions[0].type).toBe('file/uploadFile/pending');
    });
  });

  it('shows upload progress', () => {
    const progressState = {
      ...mockInitialState,
      file: {
        ...mockInitialState.file,
        uploadProgress: 50,
        uploading: true,
      },
    };

    renderWithProviders(<FileUpload postId={1} />, {
      preloadedState: progressState,
    });

    expect(screen.getByRole('progressbar')).toBeInTheDocument();
    expect(screen.getByText(/50%/i)).toBeInTheDocument();
  });

  it('shows error state', () => {
    const errorState = {
      ...mockInitialState,
      file: {
        ...mockInitialState.file,
        error: '파일 업로드에 실패했습니다.',
      },
    };

    renderWithProviders(<FileUpload postId={1} />, {
      preloadedState: errorState,
    });

    expect(screen.getByText('파일 업로드에 실패했습니다.')).toBeInTheDocument();
  });

  it('handles file size limit', async () => {
    const { store } = renderWithProviders(<FileUpload postId={1} />, {
      preloadedState: initialState,
    });

    // 11MB 파일 생성 (10MB 제한 초과)
    const largeFile = new File(['x'.repeat(11 * 1024 * 1024)], 'large.txt', {
      type: 'text/plain',
    });
    const input = screen.getByLabelText(/파일 선택/i);

    await userEvent.upload(input, largeFile);

    await waitFor(() => {
      const actions = store.getActions();
      expect(actions[0].type).toBe('file/uploadFile/rejected');
    });
  });

  it('handles file type validation', async () => {
    const { store } = renderWithProviders(<FileUpload postId={1} />, {
      preloadedState: initialState,
    });

    // 허용되지 않은 파일 타입
    const invalidFile = new File(['test'], 'test.exe', { type: 'application/x-msdownload' });
    const input = screen.getByLabelText(/파일 선택/i);

    await userEvent.upload(input, invalidFile);

    await waitFor(() => {
      const actions = store.getActions();
      expect(actions[0].type).toBe('file/uploadFile/rejected');
    });
  });

  it('handles unauthorized state', () => {
    const unauthorizedState = {
      ...mockInitialState,
      auth: {
        ...mockInitialState.auth,
        isAuthenticated: false,
      },
    };

    renderWithProviders(<FileUpload postId={1} />, {
      preloadedState: unauthorizedState,
    });

    expect(screen.getByText(/로그인이 필요합니다/i)).toBeInTheDocument();
  });
}); 