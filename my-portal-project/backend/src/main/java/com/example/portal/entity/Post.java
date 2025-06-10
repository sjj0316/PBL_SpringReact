package com.example.portal.entity;

import com.example.portal.dto.post.PostRequest;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostLike> likes = new HashSet<>();

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private boolean isDeleted;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Post(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public static Post from(PostRequest request, User user) {
        return Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }

    public void addFile(PostFile file) {
        files.add(file);
        file.setPost(this);
    }

    public void removeFile(PostFile file) {
        files.remove(file);
        file.setPost(null);
    }

    public void addFiles(List<PostFile> files) {
        this.files.addAll(files);
    }

    public void clearFiles() {
        this.files.clear();
    }

    public void addLike(User user) {
        PostLike like = PostLike.builder()
                .post(this)
                .user(user)
                .build();
        this.likes.add(like);
    }

    public void removeLike(User user) {
        this.likes.removeIf(like -> like.getUser().equals(user));
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}
