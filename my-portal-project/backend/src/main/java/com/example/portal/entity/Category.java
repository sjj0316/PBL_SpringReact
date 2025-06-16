package com.example.portal.entity;

import com.example.portal.entity.common.BaseTimeEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Category extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "display_order")
    private int displayOrder;

    @Column(name = "is_active")
    private boolean isActive;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (displayOrder == 0) {
            displayOrder = 0;
        }
        if (!isActive) {
            isActive = true;
        }
    }

    public void update(String name, String description, Integer displayOrder, boolean isActive) {
        this.name = name;
        this.description = description;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
    }

    public void updateDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public void updateStatus(boolean isActive) {
        this.isActive = isActive;
    }
}