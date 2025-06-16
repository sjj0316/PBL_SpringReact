import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, mockInitialState } from '../../../utils/test-utils';
import PostList from '../PostList';

const mockPosts = [
  {
    id: 1,
    title: '첫 번째 게시물',
    content: '첫 번째 게시물 내용입니다.',
    author: {
      id: 1,
      username: 'user1',
    },
    createdAt: '2024-03-15T12:00:00.000Z',
    likeCount: 5,
    commentCount: 3,
  },
  {
    id: 2,
    title: '두 번째 게시물',
    content: '두 번째 게시물 내용입니다.',
    author: {
      id: 2,
      username: 'user2',
    },
    createdAt: '2024-03-15T13:00:00.000Z',
    likeCount: 3,
    commentCount: 1,
  },
];

describe('PostList Integration', () => {
  const initialState = {
    ...mockInitialState,
    post: {
      ...mockInitialState.post,
      posts: mockPosts,
      totalPages: 2,
      currentPage: 1,
    },
  };

  it('renders posts correctly', async () => {
    renderWithProviders(<PostList />, { preloadedState: initialState });

    expect(screen.getByText('첫 번째 게시물')).toBeInTheDocument();
    expect(screen.getByText('두 번째 게시물')).toBeInTheDocument();
  });

  it('handles search functionality', async () => {
    const { store } = renderWithProviders(<PostList />, {
      preloadedState: initialState,
    });

    const searchInput = screen.getByPlaceholderText(/검색/i);
    await userEvent.type(searchInput, '첫 번째');

    expect(screen.getByText('첫 번째 게시물')).toBeInTheDocument();
    expect(screen.queryByText('두 번째 게시물')).not.toBeInTheDocument();
  });

  it('handles pagination', async () => {
    const { store } = renderWithProviders(<PostList />, {
      preloadedState: initialState,
    });

    const nextPageButton = screen.getByRole('button', { name: /다음/i });
    await userEvent.click(nextPageButton);

    await waitFor(() => {
      expect(store.getState().post.currentPage).toBe(2);
    });
  });

  it('navigates to post detail when clicking a post', async () => {
    renderWithProviders(<PostList />, { preloadedState: initialState });

    const firstPost = screen.getByText('첫 번째 게시물');
    await userEvent.click(firstPost);

    expect(window.location.pathname).toBe('/posts/1');
  });

  it('shows loading state', () => {
    const loadingState = {
      ...mockInitialState,
      post: {
        ...mockInitialState.post,
        loading: true,
      },
    };

    renderWithProviders(<PostList />, { preloadedState: loadingState });

    expect(screen.getByRole('progressbar')).toBeInTheDocument();
  });

  it('shows error state', () => {
    const errorState = {
      ...mockInitialState,
      post: {
        ...mockInitialState.post,
        error: '게시물을 불러오는데 실패했습니다.',
      },
    };

    renderWithProviders(<PostList />, { preloadedState: errorState });

    expect(screen.getByText('게시물을 불러오는데 실패했습니다.')).toBeInTheDocument();
  });
}); 