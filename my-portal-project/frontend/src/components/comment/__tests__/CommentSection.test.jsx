import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, mockInitialState } from '../../../utils/test-utils';
import CommentSection from '../CommentSection';

const mockComments = [
  {
    id: 1,
    content: '첫 번째 댓글입니다.',
    author: {
      id: 1,
      username: 'user1',
      profileImage: 'https://example.com/profile1.jpg',
    },
    createdAt: '2024-03-15T12:00:00.000Z',
    likeCount: 2,
    isLiked: false,
    isAuthor: true,
  },
  {
    id: 2,
    content: '두 번째 댓글입니다.',
    author: {
      id: 2,
      username: 'user2',
      profileImage: 'https://example.com/profile2.jpg',
    },
    createdAt: '2024-03-15T13:00:00.000Z',
    likeCount: 1,
    isLiked: true,
    isAuthor: false,
  },
];

describe('CommentSection Integration', () => {
  const initialState = {
    ...mockInitialState,
    comment: {
      ...mockInitialState.comment,
      comments: mockComments,
      totalPages: 2,
      currentPage: 1,
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

  it('renders comments correctly', async () => {
    renderWithProviders(<CommentSection postId={1} />, {
      preloadedState: initialState,
    });

    expect(screen.getByText('첫 번째 댓글입니다.')).toBeInTheDocument();
    expect(screen.getByText('두 번째 댓글입니다.')).toBeInTheDocument();
  });

  it('handles comment submission', async () => {
    const { store } = renderWithProviders(<CommentSection postId={1} />, {
      preloadedState: initialState,
    });

    const commentInput = screen.getByPlaceholderText(/댓글을 입력하세요/i);
    const submitButton = screen.getByRole('button', { name: /댓글 작성/i });

    await userEvent.type(commentInput, '새로운 댓글입니다.');
    await userEvent.click(submitButton);

    await waitFor(() => {
      const actions = store.getActions();
      expect(actions[0].type).toBe('comment/createComment/pending');
    });
  });

  it('handles pagination', async () => {
    const { store } = renderWithProviders(<CommentSection postId={1} />, {
      preloadedState: initialState,
    });

    const nextPageButton = screen.getByRole('button', { name: /다음/i });
    await userEvent.click(nextPageButton);

    await waitFor(() => {
      expect(store.getState().comment.currentPage).toBe(2);
    });
  });

  it('shows loading state', () => {
    const loadingState = {
      ...mockInitialState,
      comment: {
        ...mockInitialState.comment,
        loading: true,
      },
    };

    renderWithProviders(<CommentSection postId={1} />, {
      preloadedState: loadingState,
    });

    expect(screen.getByRole('progressbar')).toBeInTheDocument();
  });

  it('shows error state', () => {
    const errorState = {
      ...mockInitialState,
      comment: {
        ...mockInitialState.comment,
        error: '댓글을 불러오는데 실패했습니다.',
      },
    };

    renderWithProviders(<CommentSection postId={1} />, {
      preloadedState: errorState,
    });

    expect(screen.getByText('댓글을 불러오는데 실패했습니다.')).toBeInTheDocument();
  });

  it('handles unauthorized state', () => {
    const unauthorizedState = {
      ...mockInitialState,
      auth: {
        ...mockInitialState.auth,
        isAuthenticated: false,
      },
    };

    renderWithProviders(<CommentSection postId={1} />, {
      preloadedState: unauthorizedState,
    });

    expect(screen.getByText(/로그인이 필요합니다/i)).toBeInTheDocument();
  });
}); 