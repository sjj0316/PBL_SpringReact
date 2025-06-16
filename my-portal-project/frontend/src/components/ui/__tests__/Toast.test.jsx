import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import { renderWithProviders, mockInitialState } from '../../../utils/test-utils';
import Toast from '../Toast';

describe('Toast Integration', () => {
  const initialState = {
    ...mockInitialState,
    ui: {
      ...mockInitialState.ui,
      toast: {
        open: false,
        message: '',
        severity: 'info',
      },
    },
  };

  it('renders toast with success message', () => {
    const successState = {
      ...mockInitialState,
      ui: {
        ...mockInitialState.ui,
        toast: {
          open: true,
          message: '성공적으로 처리되었습니다.',
          severity: 'success',
        },
      },
    };

    renderWithProviders(<Toast />, {
      preloadedState: successState,
    });

    expect(screen.getByText('성공적으로 처리되었습니다.')).toBeInTheDocument();
    expect(screen.getByRole('alert')).toHaveClass('MuiAlert-standardSuccess');
  });

  it('renders toast with error message', () => {
    const errorState = {
      ...mockInitialState,
      ui: {
        ...mockInitialState.ui,
        toast: {
          open: true,
          message: '오류가 발생했습니다.',
          severity: 'error',
        },
      },
    };

    renderWithProviders(<Toast />, {
      preloadedState: errorState,
    });

    expect(screen.getByText('오류가 발생했습니다.')).toBeInTheDocument();
    expect(screen.getByRole('alert')).toHaveClass('MuiAlert-standardError');
  });

  it('renders toast with warning message', () => {
    const warningState = {
      ...mockInitialState,
      ui: {
        ...mockInitialState.ui,
        toast: {
          open: true,
          message: '주의가 필요합니다.',
          severity: 'warning',
        },
      },
    };

    renderWithProviders(<Toast />, {
      preloadedState: warningState,
    });

    expect(screen.getByText('주의가 필요합니다.')).toBeInTheDocument();
    expect(screen.getByRole('alert')).toHaveClass('MuiAlert-standardWarning');
  });

  it('renders toast with info message', () => {
    const infoState = {
      ...mockInitialState,
      ui: {
        ...mockInitialState.ui,
        toast: {
          open: true,
          message: '알림 메시지입니다.',
          severity: 'info',
        },
      },
    };

    renderWithProviders(<Toast />, {
      preloadedState: infoState,
    });

    expect(screen.getByText('알림 메시지입니다.')).toBeInTheDocument();
    expect(screen.getByRole('alert')).toHaveClass('MuiAlert-standardInfo');
  });

  it('auto-closes toast after duration', async () => {
    const { store } = renderWithProviders(<Toast />, {
      preloadedState: {
        ...mockInitialState,
        ui: {
          ...mockInitialState.ui,
          toast: {
            open: true,
            message: '자동으로 닫힐 메시지입니다.',
            severity: 'info',
          },
        },
      },
    });

    await waitFor(() => {
      expect(store.getState().ui.toast.open).toBe(false);
    }, { timeout: 3000 });
  });

  it('does not render when closed', () => {
    renderWithProviders(<Toast />, {
      preloadedState: initialState,
    });

    expect(screen.queryByRole('alert')).not.toBeInTheDocument();
  });
}); 