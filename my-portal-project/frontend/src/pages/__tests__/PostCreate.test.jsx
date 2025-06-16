import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import configureStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import PostCreate from '../PostCreate';

const mockStore = configureStore([thunk]);

describe('PostCreate 컴포넌트', () => {
  let store;

  beforeEach(() => {
    store = mockStore({
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
          <PostCreate />
        </BrowserRouter>
      </Provider>
    );
  };

  it('게시글 작성 폼을 렌더링한다', () => {
    renderComponent();
    
    expect(screen.getByText('새 게시글 작성')).toBeInTheDocument();
    expect(screen.getByLabelText('제목')).toBeInTheDocument();
    expect(screen.getByLabelText('내용')).toBeInTheDocument();
    expect(screen.getByText('작성')).toBeInTheDocument();
    expect(screen.getByText('취소')).toBeInTheDocument();
  });

  it('필수 입력 필드가 비어있을 때 에러 메시지를 표시한다', async () => {
    renderComponent();
    
    const submitButton = screen.getByText('작성');
    fireEvent.click(submitButton);

    expect(await screen.findByText('제목을 입력해주세요')).toBeInTheDocument();
    expect(await screen.findByText('내용을 입력해주세요')).toBeInTheDocument();
  });

  it('게시글 작성이 성공하면 목록 페이지로 이동한다', async () => {
    renderComponent();
    
    const titleInput = screen.getByLabelText('제목');
    const contentInput = screen.getByLabelText('내용');
    const submitButton = screen.getByText('작성');

    fireEvent.change(titleInput, { target: { value: '테스트 제목' } });
    fireEvent.change(contentInput, { target: { value: '테스트 내용' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(store.getActions()).toContainEqual(
        expect.objectContaining({
          type: 'posts/createPost/pending',
          payload: {
            title: '테스트 제목',
            content: '테스트 내용',
          },
        })
      );
    });
  });

  it('취소 버튼 클릭 시 목록 페이지로 이동한다', () => {
    renderComponent();
    
    const cancelButton = screen.getByText('취소');
    fireEvent.click(cancelButton);

    expect(window.location.pathname).toBe('/posts');
  });

  it('뒤로가기 버튼 클릭 시 목록 페이지로 이동한다', () => {
    renderComponent();
    
    const backButton = screen.getByRole('button', { name: '' });
    fireEvent.click(backButton);

    expect(window.location.pathname).toBe('/posts');
  });
}); 