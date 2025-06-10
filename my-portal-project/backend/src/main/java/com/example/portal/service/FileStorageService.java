package com.example.portal.service;

import com.example.portal.entity.Post;
import com.example.portal.entity.PostFile;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * 파일을 저장하고 PostFile 엔티티를 생성합니다.
     *
     * @param file 저장할 파일
     * @param post 연결할 게시글
     * @return 생성된 PostFile 엔티티
     */
    PostFile storeFile(MultipartFile file, Post post);

    /**
     * 파일을 삭제합니다.
     *
     * @param fileName 삭제할 파일명
     */
    void deleteFile(String fileName);

    /**
     * 파일의 URL을 생성합니다.
     *
     * @param fileName 파일명
     * @return 파일 URL
     */
    String getFileUrl(String fileName);
}