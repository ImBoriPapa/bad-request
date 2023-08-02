package com.study.badrequest.blog.command.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "BLOG_CATEGORY")
@EqualsAndHashCode(of = "id")
public class BlogCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    private String name;
    private Integer countOfPost;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BLOG_ID")
    private Blog blog;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
