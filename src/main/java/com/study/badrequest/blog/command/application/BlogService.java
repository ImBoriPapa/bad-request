package com.study.badrequest.blog.command.application;

import com.study.badrequest.blog.command.domain.Blog;

public interface BlogService {
    Blog createBlog(Long memberId);
}
