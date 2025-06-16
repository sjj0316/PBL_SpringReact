import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import configureStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import PostList from '../../pages/PostList';
import PostDetail from '../../pages/PostDetail';
import PostCreate from '../../pages/PostCreate';
import PostEdit from '../../pages/PostEdit';
import { fetchPosts, createPost, updatePost, deletePost } from '../../store/slices/postSlice';

const mockStore = configureStore([thunk]);

describe('게시글 통합 테스트', () => {
  let store;

  const mockPosts = [
    {
      id: 1,
      title: '첫 번째 게시글',
      content: '첫 번째 게시글 내용입니다.',
      author: 'user1',
      authorId: 1,
      createdAt: '2024-03-15T00:00:00.000Z',
    },
    {
      id: 2,
      title: '두 번째 게시글',
      content: '두 번째 게시글 내용입니다.',
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
      post: {
        posts: mockPosts,
        currentPost: null,
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

  describe('게시글 목록', () => {
    it('게시글 목록을 성공적으로 불러온다', async () => {
      renderWithProviders(<PostList />);

      await waitFor(() => {
        expect(store.getActions()).toContainEqual(
          expect.objectContaining({
            type: fetchPosts.pending.type,
          })
        );
      });

      expect(screen.getByText('첫 번째 게시글')).toBeInTheDocument();
      expect(screen.getByText('두 번째 게시글')).toBeInTheDocument();
    });

    it('검색 기능이 정상적으로 작동한다', async () => {
      renderWithProviders(<PostList />);

      const searchInput = screen.getByPlaceholderText('검색어를 입력하세요');
      fireEvent.change(searchInput, { target: { value: '첫 번째' } });

      await waitFor(() => {
        expect(screen.getByText('첫 번째 게시글')).toBeInTheDocument();
        expect(screen.queryByText('두 번째 게시글')).not.toBeInTheDocument();
      });
    });
  });

  describe('게시글 상세', () => {
    beforeEach(() => {
      store = mockStore({
        auth: {
          user: {
            id: 1,
            username: 'user1',
            role: 'USER',
          },
        },
        post: {
          posts: mockPosts,
          currentPost: mockPosts[0],
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

    it('게시글 상세 정보를 성공적으로 불러온다', async () => {
      renderWithProviders(<PostDetail />);

      expect(screen.getByText('첫 번째 게시글')).toBeInTheDocument();
      expect(screen.getByText('첫 번째 게시글 내용입니다.')).toBeInTheDocument();
      expect(screen.getByText('user1')).toBeInTheDocument();
    });

    it('작성자만 수정/삭제 버튼이 보인다', () => {
      renderWithProviders(<PostDetail />);

      expect(screen.getByText('수정')).toBeInTheDocument();
      expect(screen.getByText('삭제')).toBeInTheDocument();

      // 다른 사용자로 다시 렌더링
      store = mockStore({
        auth: {
          user: {
            id: 2,
            username: 'user2',
            role: 'USER',
          },
        },
        post: {
          posts: mockPosts,
          currentPost: mockPosts[0],
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

      renderWithProviders(<PostDetail />);
      expect(screen.queryByText('수정')).not.toBeInTheDocument();
      expect(screen.queryByText('삭제')).not.toBeInTheDocument();
    });
  });

  describe('게시글 작성', () => {
    it('새 게시글을 성공적으로 작성한다', async () => {
      renderWithProviders(<PostCreate />);

      const titleInput = screen.getByLabelText('제목');
      const contentInput = screen.getByLabelText('내용');
      const submitButton = screen.getByRole('button', { name: '작성' });

      fireEvent.change(titleInput, { target: { value: '새 게시글' } });
      fireEvent.change(contentInput, { target: { value: '새 게시글 내용입니다.' } });
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(store.getActions()).toContainEqual(
          expect.objectContaining({
            type: createPost.pending.type,
            payload: {
              title: '새 게시글',
              content: '새 게시글 내용입니다.',
            },
          })
        );
      });
    });

    it('필수 입력 필드가 비어있을 때 에러 메시지를 표시한다', async () => {
      renderWithProviders(<PostCreate />);

      const submitButton = screen.getByRole('button', { name: '작성' });
      fireEvent.click(submitButton);

      expect(screen.getByText('제목을 입력해주세요.')).toBeInTheDocument();
      expect(screen.getByText('내용을 입력해주세요.')).toBeInTheDocument();
    });
  });

  describe('게시글 수정', () => {
    beforeEach(() => {
      store = mockStore({
        auth: {
          user: {
            id: 1,
            username: 'user1',
            role: 'USER',
          },
        },
        post: {
          posts: mockPosts,
          currentPost: mockPosts[0],
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

    it('게시글을 성공적으로 수정한다', async () => {
      renderWithProviders(<PostEdit />);

      const titleInput = screen.getByLabelText('제목');
      const contentInput = screen.getByLabelText('내용');
      const submitButton = screen.getByRole('button', { name: '수정' });

      fireEvent.change(titleInput, { target: { value: '수정된 게시글' } });
      fireEvent.change(contentInput, { target: { value: '수정된 게시글 내용입니다.' } });
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(store.getActions()).toContainEqual(
          expect.objectContaining({
            type: updatePost.pending.type,
            payload: {
              id: 1,
              title: '수정된 게시글',
              content: '수정된 게시글 내용입니다.',
            },
          })
        );
      });
    });

    it('작성자가 아닌 경우 수정 페이지에 접근할 수 없다', () => {
      store = mockStore({
        auth: {
          user: {
            id: 2,
            username: 'user2',
            role: 'USER',
          },
        },
        post: {
          posts: mockPosts,
          currentPost: mockPosts[0],
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

      renderWithProviders(<PostEdit />);
      expect(screen.getByText('접근 권한이 없습니다.')).toBeInTheDocument();
    });
  });
}); 