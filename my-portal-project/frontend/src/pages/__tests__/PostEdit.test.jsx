import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import configureStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import PostEdit from '../PostEdit';

const mockStore = configureStore([thunk]);

describe('PostEdit 컴포넌트', () => {
  let store;

  const mockPost = {
    id: 1,
    title: '테스트 게시글',
    content: '테스트 내용',
    author: '작성자',
    authorId: 1,
    createdAt: '2024-03-15T00:00:00.000Z',
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
            <Route path="/posts/:id/edit" element={<PostEdit />} />
          </Routes>
        </BrowserRouter>
      </Provider>
    );
  };

  it('게시글 수정 폼을 렌더링한다', () => {
    renderComponent();
    
    expect(screen.getByText('게시글 수정')).toBeInTheDocument();
    expect(screen.getByLabelText('제목')).toHaveValue('테스트 게시글');
    expect(screen.getByLabelText('내용')).toHaveValue('테스트 내용');
    expect(screen.getByText('수정')).toBeInTheDocument();
    expect(screen.getByText('취소')).toBeInTheDocument();
  });

  it('필수 입력 필드가 비어있을 때 에러 메시지를 표시한다', async () => {
    renderComponent();
    
    const titleInput = screen.getByLabelText('제목');
    const contentInput = screen.getByLabelText('내용');
    const submitButton = screen.getByText('수정');

    fireEvent.change(titleInput, { target: { value: '' } });
    fireEvent.change(contentInput, { target: { value: '' } });
    fireEvent.click(submitButton);

    expect(await screen.findByText('제목을 입력해주세요')).toBeInTheDocument();
    expect(await screen.findByText('내용을 입력해주세요')).toBeInTheDocument();
  });

  it('게시글 수정이 성공하면 상세 페이지로 이동한다', async () => {
    renderComponent();
    
    const titleInput = screen.getByLabelText('제목');
    const contentInput = screen.getByLabelText('내용');
    const submitButton = screen.getByText('수정');

    fireEvent.change(titleInput, { target: { value: '수정된 제목' } });
    fireEvent.change(contentInput, { target: { value: '수정된 내용' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(store.getActions()).toContainEqual(
        expect.objectContaining({
          type: 'posts/updatePost/pending',
          payload: {
            id: 1,
            title: '수정된 제목',
            content: '수정된 내용',
          },
        })
      );
    });
  });

  it('취소 버튼 클릭 시 상세 페이지로 이동한다', () => {
    renderComponent();
    
    const cancelButton = screen.getByText('취소');
    fireEvent.click(cancelButton);

    expect(window.location.pathname).toBe('/posts/1');
  });

  it('뒤로가기 버튼 클릭 시 상세 페이지로 이동한다', () => {
    renderComponent();
    
    const backButton = screen.getByRole('button', { name: '' });
    fireEvent.click(backButton);

    expect(window.location.pathname).toBe('/posts/1');
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

  it('작성자나 관리자가 아닌 경우 접근할 수 없다', () => {
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
    expect(window.location.pathname).toBe('/posts/1');
  });
}); 