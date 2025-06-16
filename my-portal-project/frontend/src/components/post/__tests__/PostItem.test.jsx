import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import PostItem from '../PostItem';

const mockPost = {
  id: 1,
  title: '테스트 게시물',
  content: '테스트 내용입니다.',
  author: {
    id: 1,
    username: 'testuser',
  },
  createdAt: '2024-03-15T12:00:00.000Z',
  likeCount: 5,
  commentCount: 3,
};

const renderWithRouter = (component) => {
  return render(
    <BrowserRouter>
      {component}
    </BrowserRouter>
  );
};

describe('PostItem', () => {
  it('renders post information correctly', () => {
    renderWithRouter(<PostItem post={mockPost} />);

    expect(screen.getByText(mockPost.title)).toBeInTheDocument();
    expect(screen.getByText(mockPost.content)).toBeInTheDocument();
    expect(screen.getByText(`좋아요 ${mockPost.likeCount}`)).toBeInTheDocument();
    expect(screen.getByText(`댓글 ${mockPost.commentCount}`)).toBeInTheDocument();
  });

  it('navigates to post detail page when clicked', () => {
    renderWithRouter(<PostItem post={mockPost} />);

    const postCard = screen.getByText(mockPost.title).closest('div[role="button"]');
    fireEvent.click(postCard);

    expect(window.location.pathname).toBe(`/posts/${mockPost.id}`);
  });

  it('displays formatted date', () => {
    renderWithRouter(<PostItem post={mockPost} />);

    const dateElement = screen.getByText(/전$/);
    expect(dateElement).toBeInTheDocument();
  });

  it('applies hover styles', () => {
    renderWithRouter(<PostItem post={mockPost} />);

    const postCard = screen.getByText(mockPost.title).closest('div[role="button"]');
    expect(postCard).toHaveStyle({
      cursor: 'pointer',
    });
  });
}); 