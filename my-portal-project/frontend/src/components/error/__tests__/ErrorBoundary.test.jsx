import React from 'react';
import { screen } from '@testing-library/react';
import { renderWithProviders } from '../../../utils/test-utils';
import ErrorBoundary from '../ErrorBoundary';

const ThrowError = () => {
  throw new Error('테스트 에러');
};

describe('ErrorBoundary Integration', () => {
  beforeEach(() => {
    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    console.error.mockRestore();
  });

  it('renders children when there is no error', () => {
    renderWithProviders(
      <ErrorBoundary>
        <div>정상적인 컴포넌트</div>
      </ErrorBoundary>
    );

    expect(screen.getByText('정상적인 컴포넌트')).toBeInTheDocument();
  });

  it('renders error UI when there is an error', () => {
    renderWithProviders(
      <ErrorBoundary>
        <ThrowError />
      </ErrorBoundary>
    );

    expect(screen.getByText(/오류가 발생했습니다/i)).toBeInTheDocument();
    expect(screen.getByText(/테스트 에러/i)).toBeInTheDocument();
  });

  it('renders retry button when there is an error', () => {
    renderWithProviders(
      <ErrorBoundary>
        <ThrowError />
      </ErrorBoundary>
    );

    expect(screen.getByRole('button', { name: /다시 시도/i })).toBeInTheDocument();
  });

  it('handles retry action', () => {
    const { rerender } = renderWithProviders(
      <ErrorBoundary>
        <ThrowError />
      </ErrorBoundary>
    );

    const retryButton = screen.getByRole('button', { name: /다시 시도/i });
    retryButton.click();

    rerender(
      <ErrorBoundary>
        <div>정상적인 컴포넌트</div>
      </ErrorBoundary>
    );

    expect(screen.getByText('정상적인 컴포넌트')).toBeInTheDocument();
  });

  it('renders error details in development mode', () => {
    const originalNodeEnv = process.env.NODE_ENV;
    process.env.NODE_ENV = 'development';

    renderWithProviders(
      <ErrorBoundary>
        <ThrowError />
      </ErrorBoundary>
    );

    expect(screen.getByText(/Error: 테스트 에러/i)).toBeInTheDocument();
    expect(screen.getByText(/at ThrowError/i)).toBeInTheDocument();

    process.env.NODE_ENV = originalNodeEnv;
  });

  it('does not render error details in production mode', () => {
    const originalNodeEnv = process.env.NODE_ENV;
    process.env.NODE_ENV = 'production';

    renderWithProviders(
      <ErrorBoundary>
        <ThrowError />
      </ErrorBoundary>
    );

    expect(screen.queryByText(/at ThrowError/i)).not.toBeInTheDocument();

    process.env.NODE_ENV = originalNodeEnv;
  });

  it('logs error to console', () => {
    renderWithProviders(
      <ErrorBoundary>
        <ThrowError />
      </ErrorBoundary>
    );

    expect(console.error).toHaveBeenCalled();
  });
}); 