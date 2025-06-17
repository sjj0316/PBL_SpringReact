import React, { Suspense, lazy } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Provider } from 'react-redux';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import store from './store';
import theme from './theme';
import Navbar from './components/layout/Navbar';
import ProtectedRoute from './components/auth/ProtectedRoute';
import Loading from './components/ui/Loading';
import ErrorBoundary from './components/error/ErrorBoundary';

// Lazy load components
const Home = lazy(() => import('./components/Home'));
const Login = lazy(() => import('./components/auth/Login'));
const Signup = lazy(() => import('./components/auth/Signup'));
const PostList = lazy(() => import('./components/post/PostList'));
const PostDetail = lazy(() => import('./components/post/PostDetail'));
const PostForm = lazy(() => import('./components/post/PostForm'));
const Profile = lazy(() => import('./components/user/Profile'));
const NotFound = lazy(() => import('./components/ui/NotFound'));

function App() {
  return (
    <Provider store={store}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Router>
          <ErrorBoundary>
            <Navbar />
            <Suspense fallback={<Loading />}>
              <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/signup" element={<Signup />} />
                <Route path="/posts" element={<PostList />} />
                <Route path="/posts/:id" element={<PostDetail />} />
                <Route
                  path="/posts/new"
                  element={
                    <ProtectedRoute>
                      <PostForm />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/posts/:id/edit"
                  element={
                    <ProtectedRoute>
                      <PostForm />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/profile"
                  element={
                    <ProtectedRoute>
                      <Profile />
                    </ProtectedRoute>
                  }
                />
                <Route path="*" element={<NotFound />} />
              </Routes>
            </Suspense>
          </ErrorBoundary>
        </Router>
      </ThemeProvider>
    </Provider>
  );
}

export default App;
