import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import configureStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import CommentSection from '../../components/comments/CommentSection';
import { fetchComments, createComment, updateComment, deleteComment } from '../../store/slices/commentSlice';

const mockStore = configureStore([thunk]);

describe('댓글 통합 테스트', () => {
  let store;

  const mockComments = [
    {
      id: 1,
      content: '첫 번째 댓글',
      author: 'user1',
      authorId: 1,
      createdAt: '2024-03-15T00:00:00.000Z',
    },
    {
      id: 2,
      content: '두 번째 댓글',
      author: 'user2',
      authorId: 2,
      createdAt: '2024-03-16T00:00:00.000Z',
    },
  ];

  beforeEach(() => {
    store = mockStore({
      auth: {
        user: {
          id: 1,
          username: 'user1',
          role: 'USER',
        },
      },
      comment: {
        comments: mockComments,
        loading: false,
        error: null,
      },
      ui: {
        toast: {
          open: false,
          message: '',
          severity: 'info',
        },
      },
    });
  });

  const renderWithProviders = (component) => {
    return render(
      <Provider store={store}>
        <BrowserRouter>
          {component}
        </BrowserRouter>
      </Provider>
    );
  };

  describe('댓글 목록', () => {
    it('댓글 목록을 성공적으로 불러온다', async () => {
      renderWithProviders(<CommentSection postId={1} />);

      await waitFor(() => {
        expect(store.getActions()).toContainEqual(
          expect.objectContaining({
            type: fetchComments.pending.type,
            payload: 1,
          })
        );
      });

      expect(screen.getByText('첫 번째 댓글')).toBeInTheDocument();
      expect(screen.getByText('두 번째 댓글')).toBeInTheDocument();
    });

    it('댓글이 없을 때 메시지를 표시한다', () => {
      store = mockStore({
        auth: {
          user: {
            id: 1,
            username: 'user1',
            role: 'USER',
          },
        },
        comment: {
          comments: [],
          loading: false,
          error: null,
        },
        ui: {
          toast: {
            open: false,
            message: '',
            severity: 'info',
          },
        },
      });

      renderWithProviders(<CommentSection postId={1} />);
      expect(screen.getByText('아직 댓글이 없습니다.')).toBeInTheDocument();
    });
  });

  describe('댓글 작성', () => {
    it('새 댓글을 성공적으로 작성한다', async () => {
      renderWithProviders(<CommentSection postId={1} />);

      const commentInput = screen.getByPlaceholderText('댓글을 입력하세요');
      const submitButton = screen.getByRole('button', { name: '댓글 작성' });

      fireEvent.change(commentInput, { target: { value: '새 댓글' } });
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(store.getActions()).toContainEqual(
          expect.objectContaining({
            type: createComment.pending.type,
            payload: {
              postId: 1,
              content: '새 댓글',
            },
          })
        );
      });
    });

    it('로그인하지 않은 사용자는 댓글을 작성할 수 없다', () => {
      store = mockStore({
        auth: {
          user: null,
        },
        comment: {
          comments: mockComments,
          loading: false,
          error: null,
        },
        ui: {
          toast: {
            open: false,
            message: '',
            severity: 'info',
          },
        },
      });

      renderWithProviders(<CommentSection postId={1} />);
      expect(screen.queryByPlaceholderText('댓글을 입력하세요')).not.toBeInTheDocument();
      expect(screen.getByText('댓글을 작성하려면 로그인이 필요합니다.')).toBeInTheDocument();
    });
  });

  describe('댓글 수정', () => {
    it('작성자만 댓글을 수정할 수 있다', async () => {
      renderWithProviders(<CommentSection postId={1} />);

      const menuButton = screen.getAllByRole('button', { name: '' })[0];
      fireEvent.click(menuButton);
      const editButton = screen.getByText('수정');
      fireEvent.click(editButton);

      const commentInput = screen.getByDisplayValue('첫 번째 댓글');
      const submitButton = screen.getByRole('button', { name: '수정' });

      fireEvent.change(commentInput, { target: { value: '수정된 댓글' } });
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(store.getActions()).toContainEqual(
          expect.objectContaining({
            type: updateComment.pending.type,
            payload: {
              postId: 1,
              commentId: 1,
              content: '수정된 댓글',
            },
          })
        );
      });
    });

    it('작성자가 아닌 경우 수정 버튼이 보이지 않는다', () => {
      store = mockStore({
        auth: {
          user: {
            id: 2,
            username: 'user2',
            role: 'USER',
          },
        },
        comment: {
          comments: mockComments,
          loading: false,
          error: null,
        },
        ui: {
          toast: {
            open: false,
            message: '',
            severity: 'info',
          },
        },
      });

      renderWithProviders(<CommentSection postId={1} />);
      expect(screen.queryByRole('button', { name: '' })).not.toBeInTheDocument();
    });
  });

  describe('댓글 삭제', () => {
    it('작성자만 댓글을 삭제할 수 있다', async () => {
      renderWithProviders(<CommentSection postId={1} />);

      const menuButton = screen.getAllByRole('button', { name: '' })[0];
      fireEvent.click(menuButton);
      const deleteButton = screen.getByText('삭제');
      fireEvent.click(deleteButton);

      await waitFor(() => {
        expect(store.getActions()).toContainEqual(
          expect.objectContaining({
            type: deleteComment.pending.type,
            payload: {
              postId: 1,
              commentId: 1,
            },
          })
        );
      });
    });

    it('작성자가 아닌 경우 삭제 버튼이 보이지 않는다', () => {
      store = mockStore({
        auth: {
          user: {
            id: 2,
            username: 'user2',
            role: 'USER',
          },
        },
        comment: {
          comments: mockComments,
          loading: false,
          error: null,
        },
        ui: {
          toast: {
            open: false,
            message: '',
            severity: 'info',
          },
        },
      });

      renderWithProviders(<CommentSection postId={1} />);
      expect(screen.queryByRole('button', { name: '' })).not.toBeInTheDocument();
    });
  });
}); 