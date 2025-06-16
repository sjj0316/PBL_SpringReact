import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import CommentItem from '../CommentItem';

const mockStore = configureStore([thunk]);

describe('CommentItem 컴포넌트', () => {
  let store;

  const mockComment = {
    id: 1,
    content: '테스트 댓글',
    author: '작성자',
    authorId: 1,
    createdAt: '2024-03-15T00:00:00.000Z',
  };

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
        <CommentItem comment={mockComment} postId={1} />
      </Provider>
    );
  };

  it('댓글 내용을 렌더링한다', () => {
    renderComponent();
    
    expect(screen.getByText('테스트 댓글')).toBeInTheDocument();
    expect(screen.getByText('작성자')).toBeInTheDocument();
  });

  it('작성자나 관리자에게만 수정/삭제 버튼이 보인다', () => {
    renderComponent();
    
    const menuButton = screen.getByRole('button', { name: '' });
    fireEvent.click(menuButton);

    expect(screen.getByText('수정')).toBeInTheDocument();
    expect(screen.getByText('삭제')).toBeInTheDocument();

    // 다른 사용자로 다시 렌더링
    store = mockStore({
      auth: {
        user: {
          id: 2,
          username: 'otheruser',
          role: 'USER',
        },
      },
    });

    renderComponent();
    expect(screen.queryByRole('button', { name: '' })).not.toBeInTheDocument();
  });

  it('수정 버튼 클릭 시 수정 모드로 전환된다', () => {
    renderComponent();
    
    const menuButton = screen.getByRole('button', { name: '' });
    fireEvent.click(menuButton);
    const editButton = screen.getByText('수정');
    fireEvent.click(editButton);

    expect(screen.getByDisplayValue('테스트 댓글')).toBeInTheDocument();
    expect(screen.getByText('취소')).toBeInTheDocument();
    expect(screen.getByText('수정')).toBeInTheDocument();
  });

  it('수정 취소 시 원래 내용으로 돌아간다', () => {
    renderComponent();
    
    // 수정 모드로 전환
    const menuButton = screen.getByRole('button', { name: '' });
    fireEvent.click(menuButton);
    const editButton = screen.getByText('수정');
    fireEvent.click(editButton);

    // 내용 수정
    const commentInput = screen.getByDisplayValue('테스트 댓글');
    fireEvent.change(commentInput, { target: { value: '수정된 댓글' } });

    // 취소
    const cancelButton = screen.getByText('취소');
    fireEvent.click(cancelButton);

    expect(screen.getByText('테스트 댓글')).toBeInTheDocument();
    expect(screen.queryByDisplayValue('수정된 댓글')).not.toBeInTheDocument();
  });

  it('댓글 수정이 성공하면 수정 모드가 종료된다', async () => {
    renderComponent();
    
    // 수정 모드로 전환
    const menuButton = screen.getByRole('button', { name: '' });
    fireEvent.click(menuButton);
    const editButton = screen.getByText('수정');
    fireEvent.click(editButton);

    // 내용 수정
    const commentInput = screen.getByDisplayValue('테스트 댓글');
    fireEvent.change(commentInput, { target: { value: '수정된 댓글' } });

    // 수정 완료
    const submitButton = screen.getByText('수정');
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(store.getActions()).toContainEqual(
        expect.objectContaining({
          type: 'comments/updateComment/pending',
          payload: {
            postId: 1,
            commentId: 1,
            content: '수정된 댓글',
          },
        })
      );
    });
  });

  it('삭제 버튼 클릭 시 댓글을 삭제한다', async () => {
    renderComponent();
    
    const menuButton = screen.getByRole('button', { name: '' });
    fireEvent.click(menuButton);
    const deleteButton = screen.getByText('삭제');
    fireEvent.click(deleteButton);

    await waitFor(() => {
      expect(store.getActions()).toContainEqual(
        expect.objectContaining({
          type: 'comments/deleteComment/pending',
          payload: {
            postId: 1,
            commentId: 1,
          },
        })
      );
    });
  });

  it('댓글 내용이 비어있을 때 에러 메시지를 표시한다', async () => {
    renderComponent();
    
    // 수정 모드로 전환
    const menuButton = screen.getByRole('button', { name: '' });
    fireEvent.click(menuButton);
    const editButton = screen.getByText('수정');
    fireEvent.click(editButton);

    // 내용 삭제
    const commentInput = screen.getByDisplayValue('테스트 댓글');
    fireEvent.change(commentInput, { target: { value: '' } });

    // 수정 시도
    const submitButton = screen.getByText('수정');
    fireEvent.click(submitButton);

    expect(await screen.findByText('댓글 내용을 입력해주세요')).toBeInTheDocument();
  });
}); 