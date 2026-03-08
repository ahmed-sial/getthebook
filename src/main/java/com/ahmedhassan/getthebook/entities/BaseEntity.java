package com.ahmedhassan.getthebook.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @Id
    private UUID id;



    @CreatedBy
    @Column(
            updatable = false
    )
    private UUID createdBy;

    @LastModifiedBy
    private UUID updatedBy;

    @CreatedDate
    @Column(
            updatable = false,
            nullable = false
    )
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (this.id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }
}