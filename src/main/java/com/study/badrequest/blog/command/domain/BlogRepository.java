package com.study.badrequest.blog.command.domain;



import java.util.Optional;

public interface BlogRepository  {

    Blog save(Blog blog);

    Optional<Blog> findById(Long id);
    Optional<Blog> findByLocation(String location);
}
