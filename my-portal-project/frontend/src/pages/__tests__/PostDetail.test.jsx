import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import configureStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import PostDetail from '../PostDetail';

const mockStore = configureStore([thunk]);

describe('PostDetail 컴포넌트', () => {
  let store;

  const mockPost = {
    id: 1,
    title: '테스트 게시글',
    content: '테스트 내용',
    author: '작성자',
    authorId: 1,
    createdAt: '2024-03-15T00:00:00.000Z',
    likeCount: 5,
    commentCount: 3,
  };

  beforeEach(() => {
    store = mockStore({
      posts: {
        currentPost: mockPost,
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
        <BrowserRouter>
          <Routes>
            <Route path="/posts/:id" element={<PostDetail />} />
          </Routes>
        </BrowserRouter>
      </Provider>
    );
  };

  it('게시글 상세 내용을 렌더링한다', () => {
    renderComponent();
    
    expect(screen.getByText('테스트 게시글')).toBeInTheDocument();
    expect(screen.getByText('테스트 내용')).toBeInTheDocument();
    expect(screen.getByText('작성자: 작성자')).toBeInTheDocument();
  });

  it('작성자 또는 관리자에게만 수정/삭제 버튼이 보인다', () => {
    renderComponent();
    
    expect(screen.getByText('수정')).toBeInTheDocument();
    expect(screen.getByText('삭제')).toBeInTheDocument();

    // 다른 사용자로 다시 렌더링
    store = mockStore({
      posts: {
        currentPost: mockPost,
        loading: false,
        error: null,
      },
      auth: {
        user: {
          id: 2,
          username: 'otheruser',
          role: 'USER',
        },
      },
    });

    renderComponent();
    expect(screen.queryByText('수정')).not.toBeInTheDocument();
    expect(screen.queryByText('삭제')).not.toBeInTheDocument();
  });

  it('수정 버튼 클릭 시 수정 페이지로 이동한다', () => {
    renderComponent();
    
    const editButton = screen.getByText('수정');
    fireEvent.click(editButton);

    expect(window.location.pathname).toBe('/posts/1/edit');
  });

  it('삭제 버튼 클릭 시 확인 다이얼로그가 표시된다', () => {
    renderComponent();
    
    const deleteButton = screen.getByText('삭제');
    fireEvent.click(deleteButton);

    expect(screen.getByText('게시글 삭제')).toBeInTheDocument();
    expect(screen.getByText('정말로 이 게시글을 삭제하시겠습니까?')).toBeInTheDocument();
  });

  it('삭제 확인 시 게시글을 삭제하고 목록 페이지로 이동한다', async () => {
    renderComponent();
    
    const deleteButton = screen.getByText('삭제');
    fireEvent.click(deleteButton);

    const confirmButton = screen.getByText('삭제');
    fireEvent.click(confirmButton);

    await waitFor(() => {
      expect(store.getActions()).toContainEqual(
        expect.objectContaining({
          type: 'posts/deletePost/pending',
        })
      );
    });
  });

  it('로딩 중일 때 로딩 컴포넌트를 표시한다', () => {
    store = mockStore({
      posts: {
        currentPost: null,
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
      posts: {
        currentPost: null,
        loading: false,
        error: '게시글을 불러오는데 실패했습니다.',
      },
      auth: {
        user: null,
      },
    });

    renderComponent();
    expect(screen.getByText('게시글을 불러오는데 실패했습니다.')).toBeInTheDocument();
  });

  it('게시글이 없을 때 메시지를 표시한다', () => {
    store = mockStore({
      posts: {
        currentPost: null,
        loading: false,
        error: null,
      },
      auth: {
        user: null,
      },
    });

    renderComponent();
    expect(screen.getByText('게시글을 찾을 수 없습니다.')).toBeInTheDocument();
  });
}); 