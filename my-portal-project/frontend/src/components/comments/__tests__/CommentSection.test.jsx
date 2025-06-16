import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import CommentSection from '../CommentSection';

const mockStore = configureStore([thunk]);

describe('CommentSection 컴포넌트', () => {
  let store;

  const mockComments = [
    {
      id: 1,
      content: '테스트 댓글 1',
      author: '작성자1',
      authorId: 1,
      createdAt: '2024-03-15T00:00:00.000Z',
    },
    {
      id: 2,
      content: '테스트 댓글 2',
      author: '작성자2',
      authorId: 2,
      createdAt: '2024-03-16T00:00:00.000Z',
    },
  ];

  beforeEach(() => {
    store = mockStore({
      comments: {
        comments: mockComments,
        loading: false,
        error: null,
      },
      auth: {
        user: {
          id: 1,
          username: 'testuser',
          role: 'USER',
        },
      },
    });
  });

  const renderComponent = () => {
    return render(
      <Provider store={store}>
        <CommentSection postId={1} />
      </Provider>
    );
  };

  it('댓글 목록을 렌더링한다', () => {
    renderComponent();
    
    expect(screen.getByText('댓글 2개')).toBeInTheDocument();
    expect(screen.getByText('테스트 댓글 1')).toBeInTheDocument();
    expect(screen.getByText('테스트 댓글 2')).toBeInTheDocument();
  });

  it('댓글이 없을 때 메시지를 표시한다', () => {
    store = mockStore({
      comments: {
        comments: [],
        loading: false,
        error: null,
      },
      auth: {
        user: null,
      },
    });

    renderComponent();
    expect(screen.getByText('아직 댓글이 없습니다.')).toBeInTheDocument();
  });

  it('로그인한 사용자에게만 댓글 작성 폼이 보인다', () => {
    renderComponent();
    
    expect(screen.getByPlaceholderText('댓글을 입력하세요')).toBeInTheDocument();
    expect(screen.getByText('댓글 작성')).toBeInTheDocument();

    // 로그아웃 상태로 다시 렌더링
    store = mockStore({
      comments: {
        comments: mockComments,
        loading: false,
        error: null,
      },
      auth: {
        user: null,
      },
    });

    renderComponent();
    expect(screen.queryByPlaceholderText('댓글을 입력하세요')).not.toBeInTheDocument();
    expect(screen.queryByText('댓글 작성')).not.toBeInTheDocument();
  });

  it('댓글 작성이 성공하면 입력 필드가 초기화된다', async () => {
    renderComponent();
    
    const commentInput = screen.getByPlaceholderText('댓글을 입력하세요');
    const submitButton = screen.getByText('댓글 작성');

    fireEvent.change(commentInput, { target: { value: '새로운 댓글' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(store.getActions()).toContainEqual(
        expect.objectContaining({
          type: 'comments/createComment/pending',
          payload: {
            postId: 1,
            content: '새로운 댓글',
          },
        })
      );
    });

    expect(commentInput).toHaveValue('');
  });

  it('댓글 내용이 비어있을 때 에러 메시지를 표시한다', async () => {
    renderComponent();
    
    const submitButton = screen.getByText('댓글 작성');
    fireEvent.click(submitButton);

    expect(await screen.findByText('댓글 내용을 입력해주세요')).toBeInTheDocument();
  });

  it('로딩 중일 때 로딩 컴포넌트를 표시한다', () => {
    store = mockStore({
      comments: {
        comments: [],
        loading: true,
        error: null,
      },
      auth: {
        user: null,
      },
    });

    renderComponent();
    expect(screen.getByText('로딩 중...')).toBeInTheDocument();
  });

  it('에러 발생 시 에러 메시지를 표시한다', () => {
    store = mockStore({
      comments: {
        comments: [],
        loading: false,
        error: '댓글을 불러오는데 실패했습니다.',
      },
      auth: {
        user: null,
      },
    });

    renderComponent();
    expect(screen.getByText('댓글을 불러오는데 실패했습니다.')).toBeInTheDocument();
  });
}); 