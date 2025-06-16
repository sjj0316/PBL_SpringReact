import React, { useState, useEffect } from 'react';
import { Box } from '@mui/material';
import { getImageOptimizationConfig, createBlurPlaceholder } from '../../utils/imageOptimizer';

const OptimizedImage = ({
  src,
  alt,
  width,
  height,
  quality,
  format,
  lazyLoad,
  blurPlaceholder,
  style,
  ...props
}) => {
  const [isLoaded, setIsLoaded] = useState(false);
  const [placeholder, setPlaceholder] = useState(null);
  const [error, setError] = useState(false);

  useEffect(() => {
    if (blurPlaceholder) {
      createBlurPlaceholder(src)
        .then(setPlaceholder)
        .catch(() => setError(true));
    }
  }, [src, blurPlaceholder]);

  const handleLoad = () => {
    setIsLoaded(true);
  };

  const handleError = () => {
    setError(true);
  };

  const imageConfig = getImageOptimizationConfig(src, {
    width,
    quality,
    format,
    lazyLoad,
    blurPlaceholder,
  });

  return (
    <Box
      sx={{
        position: 'relative',
        width: width || '100%',
        height: height || 'auto',
        overflow: 'hidden',
        ...style,
      }}
    >
      {placeholder && !isLoaded && (
        <Box
          component="img"
          src={placeholder}
          alt={`${alt} placeholder`}
          sx={{
            position: 'absolute',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%',
            filter: 'blur(10px)',
            transform: 'scale(1.1)',
            transition: 'opacity 0.3s ease-in-out',
            opacity: isLoaded ? 0 : 1,
          }}
        />
      )}
      <Box
        component="img"
        src={imageConfig.src}
        alt={alt}
        loading={imageConfig.loading}
        decoding={imageConfig.decoding}
        onLoad={handleLoad}
        onError={handleError}
        sx={{
          width: '100%',
          height: '100%',
          objectFit: 'cover',
          transition: 'opacity 0.3s ease-in-out',
          opacity: isLoaded ? 1 : 0,
        }}
        {...props}
      />
      {error && (
        <Box
          sx={{
            position: 'absolute',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            backgroundColor: 'grey.200',
            color: 'text.secondary',
          }}
        >
          이미지를 불러올 수 없습니다
        </Box>
      )}
    </Box>
  );
};

export default OptimizedImage; 