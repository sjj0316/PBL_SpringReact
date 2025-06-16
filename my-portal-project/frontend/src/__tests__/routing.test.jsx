import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, mockInitialState } from '../utils/test-utils';
import App from '../App';

describe('Routing Integration', () => {
  const initialState = {
    ...mockInitialState,
    auth: {
      ...mockInitialState.auth,
      isAuthenticated: false,
      user: null,
    },
  };

  it('renders home page by default', () => {
    renderWithProviders(<App />, {
      preloadedState: initialState,
    });

    expect(screen.getByText(/홈/i)).toBeInTheDocument();
  });

  it('navigates to login page', async () => {
    renderWithProviders(<App />, {
      preloadedState: initialState,
    });

    const loginLink = screen.getByRole('link', { name: /로그인/i });
    await userEvent.click(loginLink);

    expect(screen.getByText(/로그인/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /로그인/i })).toBeInTheDocument();
  });

  it('navigates to signup page', async () => {
    renderWithProviders(<App />, {
      preloadedState: initialState,
    });

    const signupLink = screen.getByRole('link', { name: /회원가입/i });
    await userEvent.click(signupLink);

    expect(screen.getByText(/회원가입/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /가입/i })).toBeInTheDocument();
  });

  it('redirects to login when accessing protected route', async () => {
    renderWithProviders(<App />, {
      preloadedState: initialState,
      initialEntries: ['/posts/new'],
    });

    await waitFor(() => {
      expect(screen.getByText(/로그인이 필요합니다/i)).toBeInTheDocument();
    });
  });

  it('allows access to protected route when authenticated', async () => {
    const authenticatedState = {
      ...mockInitialState,
      auth: {
        ...mockInitialState.auth,
        isAuthenticated: true,
        user: {
          id: 1,
          username: 'testuser',
        },
      },
    };

    renderWithProviders(<App />, {
      preloadedState: authenticatedState,
      initialEntries: ['/posts/new'],
    });

    expect(screen.getByText(/새 게시글 작성/i)).toBeInTheDocument();
  });

  it('handles 404 page', async () => {
    renderWithProviders(<App />, {
      preloadedState: initialState,
      initialEntries: ['/non-existent-page'],
    });

    expect(screen.getByText(/페이지를 찾을 수 없습니다/i)).toBeInTheDocument();
  });

  it('navigates to post detail page', async () => {
    const postState = {
      ...mockInitialState,
      post: {
        ...mockInitialState.post,
        currentPost: {
          id: 1,
          title: '테스트 게시글',
          content: '내용',
          author: { id: 1, username: 'testuser' },
        },
      },
    };

    renderWithProviders(<App />, {
      preloadedState: postState,
      initialEntries: ['/posts/1'],
    });

    expect(screen.getByText('테스트 게시글')).toBeInTheDocument();
  });

  it('navigates to post edit page', async () => {
    const authenticatedState = {
      ...mockInitialState,
      auth: {
        ...mockInitialState.auth,
        isAuthenticated: true,
        user: {
          id: 1,
          username: 'testuser',
        },
      },
      post: {
        ...mockInitialState.post,
        currentPost: {
          id: 1,
          title: '테스트 게시글',
          content: '내용',
          author: { id: 1, username: 'testuser' },
        },
      },
    };

    renderWithProviders(<App />, {
      preloadedState: authenticatedState,
      initialEntries: ['/posts/1/edit'],
    });

    expect(screen.getByText(/게시글 수정/i)).toBeInTheDocument();
  });

  it('prevents unauthorized access to edit page', async () => {
    const authenticatedState = {
      ...mockInitialState,
      auth: {
        ...mockInitialState.auth,
        isAuthenticated: true,
        user: {
          id: 2,
          username: 'otheruser',
        },
      },
      post: {
        ...mockInitialState.post,
        currentPost: {
          id: 1,
          title: '테스트 게시글',
          content: '내용',
          author: { id: 1, username: 'testuser' },
        },
      },
    };

    renderWithProviders(<App />, {
      preloadedState: authenticatedState,
      initialEntries: ['/posts/1/edit'],
    });

    await waitFor(() => {
      expect(screen.getByText(/권한이 없습니다/i)).toBeInTheDocument();
    });
  });
}); 