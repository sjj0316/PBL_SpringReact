package com.example.portal.entity;

import com.example.portal.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostFile extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String storedName;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public PostFile(String originalName, String storedName, String url, String fileType, Long fileSize) {
        this.originalName = originalName;
        this.storedName = storedName;
        this.url = url;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}