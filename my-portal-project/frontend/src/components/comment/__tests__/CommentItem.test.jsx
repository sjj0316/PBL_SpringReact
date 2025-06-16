import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import CommentItem from '../CommentItem';

const mockStore = configureStore([thunk]);

const mockComment = {
  id: 1,
  content: '테스트 댓글입니다.',
  author: {
    id: 1,
    username: 'testuser',
    profileImage: 'https://example.com/profile.jpg',
  },
  createdAt: '2024-03-15T12:00:00.000Z',
  likeCount: 3,
  isLiked: false,
  isAuthor: true,
};

const renderWithRedux = (component, initialState = {}) => {
  const store = mockStore(initialState);
  return {
    ...render(
      <Provider store={store}>
        {component}
      </Provider>
    ),
    store,
  };
};

describe('CommentItem', () => {
  it('renders comment information correctly', () => {
    renderWithRedux(<CommentItem comment={mockComment} onMenuOpen={jest.fn()} />);

    expect(screen.getByText(mockComment.content)).toBeInTheDocument();
    expect(screen.getByText(mockComment.author.username)).toBeInTheDocument();
    expect(screen.getByText(`${mockComment.likeCount}`)).toBeInTheDocument();
  });

  it('handles like button click', () => {
    const { store } = renderWithRedux(
      <CommentItem comment={mockComment} onMenuOpen={jest.fn()} />
    );

    const likeButton = screen.getByRole('button', { name: /좋아요/i });
    fireEvent.click(likeButton);

    const actions = store.getActions();
    expect(actions[0].type).toBe('comment/toggleCommentLike/pending');
  });

  it('shows menu button for author', () => {
    renderWithRedux(<CommentItem comment={mockComment} onMenuOpen={jest.fn()} />);

    const menuButton = screen.getByRole('button', { name: /more/i });
    expect(menuButton).toBeInTheDocument();
  });

  it('does not show menu button for non-author', () => {
    const nonAuthorComment = { ...mockComment, isAuthor: false };
    renderWithRedux(
      <CommentItem comment={nonAuthorComment} onMenuOpen={jest.fn()} />
    );

    const menuButton = screen.queryByRole('button', { name: /more/i });
    expect(menuButton).not.toBeInTheDocument();
  });

  it('calls onMenuOpen when menu button is clicked', () => {
    const handleMenuOpen = jest.fn();
    renderWithRedux(
      <CommentItem comment={mockComment} onMenuOpen={handleMenuOpen} />
    );

    const menuButton = screen.getByRole('button', { name: /more/i });
    fireEvent.click(menuButton);

    expect(handleMenuOpen).toHaveBeenCalled();
  });

  it('displays formatted date', () => {
    renderWithRedux(<CommentItem comment={mockComment} onMenuOpen={jest.fn()} />);

    const dateElement = screen.getByText(/전$/);
    expect(dateElement).toBeInTheDocument();
  });
}); 