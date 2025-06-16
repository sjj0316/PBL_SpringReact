import React from 'react';
import { screen, fireEvent, waitFor } from '@testing-library/react';
import { renderWithProviders, createMockPost, createMockUser } from '../../../utils/test-utils';
import PostDetail from '../PostDetail';

describe('PostDetail 컴포넌트', () => {
  const mockPost = createMockPost({
    id: 1,
    title: '테스트 게시글',
    content: '테스트 내용',
    author: 'testuser',
    authorId: 1,
    likeCount: 5,
    commentCount: 3,
    isLiked: false,
  });

  const mockUser = createMockUser({ id: 1 });

  const initialState = {
    post: {
      currentPost: mockPost,
      loading: false,
      error: null,
    },
    auth: {
      user: mockUser,
      isAuthenticated: true,
    },
  };

  it('게시글 상세 정보를 올바르게 렌더링한다', () => {
    renderWithProviders(<PostDetail />, { initialState });

    expect(screen.getByText(mockPost.title)).toBeInTheDocument();
    expect(screen.getByText(mockPost.content)).toBeInTheDocument();
    expect(screen.getByText(`작성자: ${mockPost.author}`)).toBeInTheDocument();
    expect(screen.getByText(`좋아요: ${mockPost.likeCount}`)).toBeInTheDocument();
    expect(screen.getByText(`댓글: ${mockPost.commentCount}`)).toBeInTheDocument();
  });

  it('좋아요 기능이 올바르게 동작한다', async () => {
    const { store } = renderWithProviders(<PostDetail />, { initialState });

    const likeButton = screen.getByRole('button', { name: /좋아요/i });
    fireEvent.click(likeButton);

    await waitFor(() => {
      expect(store.getState().post.currentPost.isLiked).toBe(true);
      expect(store.getState().post.currentPost.likeCount).toBe(mockPost.likeCount + 1);
    });
  });

  it('게시글 삭제 기능이 올바르게 동작한다', async () => {
    const { store, history } = renderWithProviders(<PostDetail />, { initialState });

    const deleteButton = screen.getByRole('button', { name: /삭제/i });
    fireEvent.click(deleteButton);

    // 삭제 확인 대화상자
    const confirmButton = screen.getByRole('button', { name: /확인/i });
    fireEvent.click(confirmButton);

    await waitFor(() => {
      expect(history.location.pathname).toBe('/posts');
    });
  });

  it('게시글 수정 버튼이 올바르게 동작한다', () => {
    const { history } = renderWithProviders(<PostDetail />, { initialState });

    const editButton = screen.getByRole('button', { name: /수정/i });
    fireEvent.click(editButton);

    expect(history.location.pathname).toBe('/posts/1/edit');
  });

  it('로딩 상태를 올바르게 표시한다', () => {
    renderWithProviders(<PostDetail />, {
      initialState: {
        post: {
          ...initialState.post,
          loading: true,
        },
      },
    });

    expect(screen.getByRole('progressbar')).toBeInTheDocument();
  });

  it('에러 상태를 올바르게 표시한다', () => {
    const errorMessage = '게시글을 불러오는데 실패했습니다.';
    renderWithProviders(<PostDetail />, {
      initialState: {
        post: {
          ...initialState.post,
          error: errorMessage,
        },
      },
    });

    expect(screen.getByText(errorMessage)).toBeInTheDocument();
  });

  it('게시글이 없는 경우 올바르게 표시한다', () => {
    renderWithProviders(<PostDetail />, {
      initialState: {
        post: {
          ...initialState.post,
          currentPost: null,
        },
      },
    });

    expect(screen.getByText('게시글을 찾을 수 없습니다.')).toBeInTheDocument();
  });

  it('다른 사용자의 게시글에서는 수정/삭제 버튼이 보이지 않는다', () => {
    renderWithProviders(<PostDetail />, {
      initialState: {
        ...initialState,
        auth: {
          user: createMockUser({ id: 2 }), // 다른 사용자
          isAuthenticated: true,
        },
      },
    });

    expect(screen.queryByRole('button', { name: /수정/i })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /삭제/i })).not.toBeInTheDocument();
  });
}); 