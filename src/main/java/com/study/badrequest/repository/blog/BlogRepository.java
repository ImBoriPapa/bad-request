package com.study.badrequest.repository.blog;

import com.study.badrequest.domain.blog.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog,Long> {
    Optional<Blog> findByLocation(String location);
}
