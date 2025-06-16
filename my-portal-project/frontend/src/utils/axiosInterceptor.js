import axios from 'axios';
import { store } from '../store';
import { setToast } from '../store/slices/uiSlice';

const axiosInterceptor = () => {
  // 요청 인터셉터
  axios.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  // 응답 인터셉터
  axios.interceptors.response.use(
    (response) => response,
    (error) => {
      const { response } = error;

      // 토큰 만료 처리
      if (response?.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        store.dispatch(
          setToast({
            open: true,
            message: '세션이 만료되었습니다. 다시 로그인해 주세요.',
            severity: 'error',
          })
        );
        return Promise.reject(error);
      }

      // 서버 에러 처리
      if (response?.status >= 500) {
        store.dispatch(
          setToast({
            open: true,
            message: '서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.',
            severity: 'error',
          })
        );
        return Promise.reject(error);
      }

      // 클라이언트 에러 처리
      if (response?.status >= 400) {
        const message = response.data?.message || '요청을 처리할 수 없습니다.';
        store.dispatch(
          setToast({
            open: true,
            message,
            severity: 'error',
          })
        );
        return Promise.reject(error);
      }

      // 네트워크 에러 처리
      if (!response) {
        store.dispatch(
          setToast({
            open: true,
            message: '네트워크 연결을 확인해 주세요.',
            severity: 'error',
          })
        );
        return Promise.reject(error);
      }

      return Promise.reject(error);
    }
  );
};

export default axiosInterceptor; 