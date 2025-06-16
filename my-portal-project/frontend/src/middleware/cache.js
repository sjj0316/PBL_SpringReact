// Redux 상태 캐싱 미들웨어
const CACHE_KEY_PREFIX = 'redux_cache_';
const DEFAULT_CACHE_TIME = 5 * 60 * 1000; // 5분

// 캐시 저장
const saveToCache = (key, data, expirationTime) => {
  const cacheData = {
    data,
    timestamp: Date.now(),
    expirationTime,
  };
  localStorage.setItem(CACHE_KEY_PREFIX + key, JSON.stringify(cacheData));
};

// 캐시에서 데이터 가져오기
const getFromCache = (key) => {
  const cachedData = localStorage.getItem(CACHE_KEY_PREFIX + key);
  if (!cachedData) return null;

  const { data, timestamp, expirationTime } = JSON.parse(cachedData);
  const now = Date.now();

  // 캐시 만료 확인
  if (now - timestamp > expirationTime) {
    localStorage.removeItem(CACHE_KEY_PREFIX + key);
    return null;
  }

  return data;
};

// 캐시 키 생성
const generateCacheKey = (action) => {
  const { type, payload } = action;
  return `${type}_${JSON.stringify(payload)}`;
};

// 캐시 미들웨어
export const cacheMiddleware = (store) => (next) => (action) => {
  // 캐시가 필요한 액션인지 확인
  if (action.meta?.cache) {
    const cacheKey = generateCacheKey(action);
    const cachedData = getFromCache(cacheKey);

    if (cachedData) {
      // 캐시된 데이터가 있으면 해당 데이터로 액션 수정
      return next({
        ...action,
        payload: cachedData,
        meta: {
          ...action.meta,
          fromCache: true,
        },
      });
    }

    // 캐시된 데이터가 없으면 원래 액션 실행
    const result = next(action);

    // 액션이 성공적으로 처리되었고 데이터가 있으면 캐시에 저장
    if (!result.error && result.payload) {
      saveToCache(
        cacheKey,
        result.payload,
        action.meta.cacheTime || DEFAULT_CACHE_TIME
      );
    }

    return result;
  }

  return next(action);
};

// 캐시 초기화
export const clearCache = (pattern) => {
  if (pattern) {
    // 특정 패턴의 캐시만 삭제
    Object.keys(localStorage)
      .filter((key) => key.startsWith(CACHE_KEY_PREFIX) && key.includes(pattern))
      .forEach((key) => localStorage.removeItem(key));
  } else {
    // 모든 캐시 삭제
    Object.keys(localStorage)
      .filter((key) => key.startsWith(CACHE_KEY_PREFIX))
      .forEach((key) => localStorage.removeItem(key));
  }
};

// 캐시 상태 확인
export const getCacheStatus = () => {
  const cacheEntries = Object.keys(localStorage)
    .filter((key) => key.startsWith(CACHE_KEY_PREFIX))
    .map((key) => {
      const data = JSON.parse(localStorage.getItem(key));
      return {
        key: key.replace(CACHE_KEY_PREFIX, ''),
        timestamp: data.timestamp,
        expirationTime: data.expirationTime,
        size: JSON.stringify(data).length,
      };
    });

  return {
    totalEntries: cacheEntries.length,
    totalSize: cacheEntries.reduce((sum, entry) => sum + entry.size, 0),
    entries: cacheEntries,
  };
}; 