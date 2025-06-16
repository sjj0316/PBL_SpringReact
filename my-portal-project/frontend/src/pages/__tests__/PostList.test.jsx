import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import configureStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import PostList from '../PostList';

const mockStore = configureStore([thunk]);

describe('PostList 컴포넌트', () => {
  let store;

  const mockPosts = [
    {
      id: 1,
      title: '테스트 게시글 1',
      content: '테스트 내용 1',
      author: '작성자1',
      createdAt: '2024-03-15T00:00:00.000Z',
      likeCount: 5,
      commentCount: 3,
    },
    {
      id: 2,
      title: '테스트 게시글 2',
      content: '테스트 내용 2',
      author: '작성자2',
      createdAt: '2024-03-16T00:00:00.000Z',
      likeCount: 3,
      commentCount: 1,
    },
  ];

  beforeEach(() => {
    store = mockStore({
      posts: {
        posts: mockPosts,
        loading: false,
        error: null,
        searchKeyword: '',
        currentPage: 1,
        totalPages: 1,
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
          <PostList />
        </BrowserRouter>
      </Provider>
    );
  };

  it('게시글 목록을 렌더링한다', () => {
    renderComponent();
    
    expect(screen.getByText('게시글 목록')).toBeInTheDocument();
    expect(screen.getByText('테스트 게시글 1')).toBeInTheDocument();
    expect(screen.getByText('테스트 게시글 2')).toBeInTheDocument();
  });

  it('게시글 검색 기능이 동작한다', async () => {
    renderComponent();
    
    const searchInput = screen.getByPlaceholderText('검색어를 입력하세요');
    fireEvent.change(searchInput, { target: { value: '테스트 게시글 1' } });
    fireEvent.submit(searchInput);

    await waitFor(() => {
      expect(store.getActions()).toContainEqual(
        expect.objectContaining({
          type: 'posts/setSearchKeyword',
          payload: '테스트 게시글 1',
        })
      );
    });
  });

  it('게시글 작성 버튼이 로그인한 사용자에게만 보인다', () => {
    renderComponent();
    
    expect(screen.getByText('글쓰기')).toBeInTheDocument();

    // 로그아웃 상태로 다시 렌더링
    store = mockStore({
      posts: {
        posts: mockPosts,
        loading: false,
        error: null,
        searchKeyword: '',
        currentPage: 1,
        totalPages: 1,
      },
      auth: {
        user: null,
      },
    });

    renderComponent();
    expect(screen.queryByText('글쓰기')).not.toBeInTheDocument();
  });

  it('게시글 클릭 시 상세 페이지로 이동한다', () => {
    renderComponent();
    
    const postLink = screen.getByText('테스트 게시글 1');
    fireEvent.click(postLink);

    expect(window.location.pathname).toBe('/posts/1');
  });

  it('로딩 중일 때 로딩 컴포넌트를 표시한다', () => {
    store = mockStore({
      posts: {
        posts: [],
        loading: true,
        error: null,
        searchKeyword: '',
        currentPage: 1,
        totalPages: 1,
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
        posts: [],
        loading: false,
        error: '게시글을 불러오는데 실패했습니다.',
        searchKeyword: '',
        currentPage: 1,
        totalPages: 1,
      },
      auth: {
        user: null,
      },
    });

    renderComponent();
    expect(screen.getByText('게시글을 불러오는데 실패했습니다.')).toBeInTheDocument();
  });
}); 