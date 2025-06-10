package com.example.portal.service;

import com.example.portal.dto.post.PostRequest;
import com.example.portal.dto.post.PostResponse;
import com.example.portal.dto.post.PostFileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    /**
     * 새로운 게시글을 생성합니다.
     *
     * @param request 게시글 생성 요청
     * @param files   첨부 파일 목록
     * @return 생성된 게시글 정보
     */
    PostResponse createPost(PostRequest request, List<MultipartFile> files);

    /**
     * 게시글을 수정합니다.
     *
     * @param postId  게시글 ID
     * @param request 게시글 수정 요청
     * @return 수정된 게시글 정보
     */
    PostResponse updatePost(Long postId, PostRequest request);

    /**
     * 게시글을 삭제합니다.
     *
     * @param postId 게시글 ID
     */
    void deletePost(Long postId);

    /**
     * 게시글을 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 게시글 정보
     */
    PostResponse getPost(Long postId);

    /**
     * 게시글 목록을 조회합니다.
     *
     * @param pageable 페이지 정보
     * @return 게시글 목록
     */
    Page<PostResponse> getPosts(Pageable pageable);

    /**
     * 게시글에 좋아요를 추가합니다.
     *
     * @param postId 게시글 ID
     */
    void addLike(Long postId);

    /**
     * 게시글의 좋아요를 취소합니다.
     *
     * @param postId 게시글 ID
     */
    void removeLike(Long postId);

    /**
     * 게시글을 검색합니다.
     *
     * @param keyword  검색어
     * @param pageable 페이지 정보
     * @return 검색된 게시글 목록
     */
    Page<PostResponse> searchPosts(String keyword, Pageable pageable);

    /**
     * 게시글 첨부파일을 업로드합니다.
     *
     * @param file   업로드할 파일
     * @param postId 첨부할 게시글 ID
     * @return 업로드된 파일 정보
     */
    PostFileResponse uploadPostFile(MultipartFile file, Long postId);
}
