// 이메일 유효성 검사
export const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

// 비밀번호 유효성 검사
export const validatePassword = (password) => {
  // 최소 8자, 최소 하나의 문자, 하나의 숫자, 하나의 특수문자
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
  return passwordRegex.test(password);
};

// 사용자명 유효성 검사
export const validateUsername = (username) => {
  // 3-20자, 영문자, 숫자, 언더스코어만 허용
  const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/;
  return usernameRegex.test(username);
};

// 게시물 제목 유효성 검사
export const validatePostTitle = (title) => {
  return title.length >= 2 && title.length <= 100;
};

// 게시물 내용 유효성 검사
export const validatePostContent = (content) => {
  return content.length >= 10 && content.length <= 5000;
};

// 댓글 내용 유효성 검사
export const validateCommentContent = (content) => {
  return content.length >= 1 && content.length <= 1000;
};

// 파일 크기 유효성 검사 (MB 단위)
export const validateFileSize = (file, maxSizeMB) => {
  const maxSizeBytes = maxSizeMB * 1024 * 1024;
  return file.size <= maxSizeBytes;
};

// 파일 타입 유효성 검사
export const validateFileType = (file, allowedTypes) => {
  return allowedTypes.includes(file.type);
};

// 폼 데이터 유효성 검사
export const validateForm = (formData, rules) => {
  const errors = {};

  Object.keys(rules).forEach((field) => {
    const value = formData[field];
    const fieldRules = rules[field];

    if (fieldRules.required && !value) {
      errors[field] = '필수 입력 항목입니다.';
    } else if (value) {
      if (fieldRules.minLength && value.length < fieldRules.minLength) {
        errors[field] = `최소 ${fieldRules.minLength}자 이상 입력해 주세요.`;
      }
      if (fieldRules.maxLength && value.length > fieldRules.maxLength) {
        errors[field] = `최대 ${fieldRules.maxLength}자까지 입력 가능합니다.`;
      }
      if (fieldRules.pattern && !fieldRules.pattern.test(value)) {
        errors[field] = fieldRules.message || '유효하지 않은 형식입니다.';
      }
      if (fieldRules.validate) {
        const customError = fieldRules.validate(value);
        if (customError) {
          errors[field] = customError;
        }
      }
    }
  });

  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  };
}; 