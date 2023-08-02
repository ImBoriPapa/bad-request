package com.study.badrequest.bolg.command.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "BLOG_POST")
@EqualsAndHashCode(of = "id")
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String contents;
    private String category;
    private String thumbnail;
    private Boolean exposure;
    private Boolean commentAble;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BLOG_ID")
    private Blog blog;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private BlogCategory blogCategory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
