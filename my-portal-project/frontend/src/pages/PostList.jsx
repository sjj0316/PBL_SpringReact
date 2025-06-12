// src/pages/PostList.jsx
import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Grid,
  Chip,
  TextField,
  InputAdornment,
  IconButton,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  CircularProgress,
} from '@mui/material';
import {
  Add as AddIcon,
  Search as SearchIcon,
  Clear as ClearIcon,
  ThumbUp as ThumbUpIcon,
  Visibility as VisibilityIcon,
  Sort as SortIcon,
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import {
  getPosts,
  searchPosts,
  searchPostsByCategory,
  getRecentPosts,
  getPopularPosts,
} from '../api/postApi';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import PageContainer from '../components/common/PageContainer';
import { useInfiniteScroll } from '../hooks/useInfiniteScroll';

const SORT_OPTIONS = [
  { value: "latest", label: "최신순" },
  { value: "popular", label: "인기순" },
  { value: "views", label: "조회수순" },
];

export default function PostList() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [sortBy, setSortBy] = useState('latest');
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  const fetchPosts = useCallback(async (pageNum = 0, isNewSearch = false) => {
    try {
      setLoading(true);
      setError(null);
      let response;
      
      if (searchKeyword) {
        response = await searchPosts(searchKeyword, pageNum);
      } else {
        switch (sortBy) {
          case "popular":
            response = await getPopularPosts(pageNum);
            break;
          case "latest":
          default:
            response = await getRecentPosts(pageNum);
            break;
        }
      }

      const newPosts = response.content;
      setPosts(prev => isNewSearch ? newPosts : [...prev, ...newPosts]);
      setHasMore(!response.last);
      setPage(pageNum);
    } catch (error) {
      setError(error.message || '게시글을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  }, [searchKeyword, sortBy]);

  const { lastElementRef } = useInfiniteScroll({
    loading,
    hasMore,
    onLoadMore: () => fetchPosts(page + 1),
  });

  const handleSearch = () => {
    setPage(0);
    fetchPosts(0, true);
  };

  const handleClearSearch = () => {
    setSearchKeyword('');
    setSelectedCategory('');
    setPage(0);
    fetchPosts(0, true);
  };

  const handleSortChange = (event) => {
    setSortBy(event.target.value);
    setPage(0);
    fetchPosts(0, true);
  };

  useEffect(() => {
    fetchPosts(0, true);
  }, [sortBy]);

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <ErrorMessage
        title="게시글 로딩 실패"
        message={error}
        onRetry={fetchPosts}
      />
    );
  }

  return (
    <PageContainer>
      <Box sx={{ mb: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography variant="h4" component="h1">
            게시판
          </Typography>
          {user && (
            <Button
              variant="contained"
              color="primary"
              startIcon={<AddIcon />}
              onClick={() => navigate('/write')}
            >
              새 글 작성
            </Button>
          )}
        </Box>

        <Box sx={{ mb: 3, display: 'flex', gap: 2, alignItems: 'center' }}>
          <TextField
            fullWidth
            variant="outlined"
            placeholder="검색어를 입력하세요"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
              endAdornment: searchKeyword && (
                <InputAdornment position="end">
                  <IconButton onClick={handleClearSearch} edge="end">
                    <ClearIcon />
                  </IconButton>
                </InputAdornment>
              ),
            }}
          />
          <FormControl sx={{ minWidth: 120 }}>
            <InputLabel>카테고리</InputLabel>
            <Select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              label="카테고리"
            >
              <MenuItem value="">전체</MenuItem>
              <MenuItem value="notice">공지사항</MenuItem>
              <MenuItem value="free">자유게시판</MenuItem>
              <MenuItem value="qna">Q&A</MenuItem>
            </Select>
          </FormControl>
          <Button
            variant="contained"
            onClick={handleSearch}
            disabled={!searchKeyword}
          >
            검색
          </Button>
        </Box>

        <Box sx={{ mb: 3, display: 'flex', gap: 1 }}>
          <Button
            variant={sortBy === 'latest' ? 'contained' : 'outlined'}
            onClick={() => setSortBy('latest')}
          >
            최신순
          </Button>
          <Button
            variant={sortBy === 'popular' ? 'contained' : 'outlined'}
            onClick={() => setSortBy('popular')}
          >
            인기순
          </Button>
        </Box>

        <Grid container spacing={3}>
          {posts.map((post, index) => (
            <Grid item xs={12} sm={6} md={4} key={post.id}
              ref={index === posts.length - 1 ? lastElementRef : null}
            >
              <Card
                sx={{
                  cursor: 'pointer',
                  transition: 'transform 0.2s',
                  '&:hover': {
                    transform: 'translateY(-4px)',
                  },
                }}
                onClick={() => navigate(`/posts/${post.id}`)}
              >
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                    <Typography variant="h6" component="h2" sx={{ flex: 1 }}>
                      {post.title}
                    </Typography>
                    <Chip
                      label={post.category}
                      color="primary"
                      size="small"
                      sx={{ ml: 2 }}
                    />
                  </Box>
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      display: '-webkit-box',
                      WebkitLineClamp: 2,
                      WebkitBoxOrient: 'vertical',
                    }}
                  >
                    {post.content}
                  </Typography>
                  <Box sx={{ mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Typography variant="caption" color="text.secondary">
                      작성자: {post.author}
                    </Typography>
                    <Box sx={{ display: 'flex', gap: 2 }}>
                      <Typography variant="caption" color="text.secondary">
                        조회수: {post.viewCount}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        좋아요: {post.likeCount}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        작성일: {new Date(post.createdAt).toLocaleDateString()}
                      </Typography>
                    </Box>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>

        {loading && (
          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
            <CircularProgress />
          </Box>
        )}

        {!loading && posts.length === 0 && (
          <Box sx={{ textAlign: 'center', mt: 4 }}>
            <Typography variant="h6" color="text.secondary">
              게시글이 없습니다.
            </Typography>
          </Box>
        )}
      </Box>
    </PageContainer>
  );
}
