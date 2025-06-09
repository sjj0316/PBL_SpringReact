-- OAuth2 관련 필드 추가
ALTER TABLE users ADD COLUMN provider VARCHAR(20);
ALTER TABLE users ADD COLUMN provider_id VARCHAR(255);
ALTER TABLE users ADD COLUMN email VARCHAR(255) UNIQUE;
ALTER TABLE users ADD COLUMN profile_image VARCHAR(255);

-- 기존 사용자들의 provider를 LOCAL로 설정
UPDATE users SET provider = 'LOCAL' WHERE provider IS NULL; 