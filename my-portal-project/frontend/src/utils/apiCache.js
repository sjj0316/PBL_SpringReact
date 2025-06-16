// API 응답 캐싱 유틸리티

const CACHE_KEY_PREFIX = 'api_cache_';
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
const generateCacheKey = (url, params) => {
  const queryString = params ? `?${new URLSearchParams(params).toString()}` : '';
  return `${url}${queryString}`;
};

// API 요청 래퍼
export const cachedFetch = async (url, options = {}, cacheTime = DEFAULT_CACHE_TIME) => {
  const { params, ...fetchOptions } = options;
  const cacheKey = generateCacheKey(url, params);

  // GET 요청이고 캐시가 있는 경우
  if (fetchOptions.method === 'GET' || !fetchOptions.method) {
    const cachedData = getFromCache(cacheKey);
    if (cachedData) {
      return {
        data: cachedData,
        fromCache: true,
      };
    }
  }

  try {
    // API 요청 실행
    const response = await fetch(url, fetchOptions);
    const data = await response.json();

    // GET 요청이고 성공한 경우 캐시에 저장
    if ((fetchOptions.method === 'GET' || !fetchOptions.method) && response.ok) {
      saveToCache(cacheKey, data, cacheTime);
    }

    return {
      data,
      fromCache: false,
    };
  } catch (error) {
    console.error('API 요청 실패:', error);
    throw error;
  }
};

// 캐시 초기화
export const clearApiCache = (pattern) => {
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
export const getApiCacheStatus = () => {
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

// 캐시 만료 시간 설정
export const setCacheExpiration = (key, expirationTime) => {
  const cachedData = localStorage.getItem(CACHE_KEY_PREFIX + key);
  if (cachedData) {
    const data = JSON.parse(cachedData);
    data.expirationTime = expirationTime;
    localStorage.setItem(CACHE_KEY_PREFIX + key, JSON.stringify(data));
  }
};

// 캐시 자동 정리
export const cleanupExpiredCache = () => {
  Object.keys(localStorage)
    .filter((key) => key.startsWith(CACHE_KEY_PREFIX))
    .forEach((key) => {
      const data = JSON.parse(localStorage.getItem(key));
      const now = Date.now();
      if (now - data.timestamp > data.expirationTime) {
        localStorage.removeItem(key);
      }
    });
}; 