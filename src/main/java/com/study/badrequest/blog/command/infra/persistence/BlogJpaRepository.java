package com.study.badrequest.blog.command.infra.persistence;

import com.study.badrequest.blog.command.domain.Blog;
import com.study.badrequest.blog.command.domain.BlogRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogJpaRepository extends JpaRepository<Blog, Long>, BlogRepository {
}
