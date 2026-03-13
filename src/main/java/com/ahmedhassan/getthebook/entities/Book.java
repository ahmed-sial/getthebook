package com.ahmedhassan.getthebook.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Book extends BaseEntity {
    @Column(nullable = false, updatable = false, length = 100)
    private String title;
    @Column(nullable = false, updatable = false, length = 20)
    private String genre;
    @Column(nullable = false, updatable = false, unique = true, length = 13)
    private String isbn;
    @Column(nullable = false, updatable = false, length = 100)
    private String author;
    @Column(nullable = false)
    private String synopsis;
    @Column(nullable = false, updatable = false, length = 100)
    private String publisher;
    @Column(nullable = false, updatable = false)
    private Date publicationDate;
    @Column(nullable = false)
    private String bookCover;
    @Column(nullable = false)
    private Boolean isArchived;
    @Column(nullable = false)
    private Boolean isShareable;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User user;
}