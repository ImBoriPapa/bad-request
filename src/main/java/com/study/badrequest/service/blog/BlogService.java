package com.study.badrequest.service.blog;

import com.study.badrequest.bolg.command.domain.Blog;

public interface BlogService {
    Blog createBlog(Long memberId);
}
