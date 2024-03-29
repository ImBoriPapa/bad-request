package com.study.badrequest.blog.command.application;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.blog.command.domain.Blog;
import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.blog.command.domain.BlogRepository;
import com.study.badrequest.member.command.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BlogServiceImpl implements BlogService {
    private final BlogRepository blogRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Blog createBlog(Long memberId) {
        log.info("Create Blog Request MemberId: {}", memberId);
        MemberEntity member = null;

        Blog blog = Blog.createBlog(member);

        if (blogRepository.findByLocation(blog.getLocation()).isPresent()) {
            blog.replaceLocationToRandom();

            while (blogRepository.findByLocation(blog.getLocation()).isPresent()) {
                blog.replaceLocationToRandom();
            }
        }


        return blogRepository.save(blog);
    }

    public Blog changeCommentNotificationSetting(Long blogId, Boolean comments) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.SERVER_ERROR));
        blog.changeCommentNotification(comments);
        return blog;
    }

    public Blog changeEmailNotificationSetting(Long blogId, Boolean email) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.SERVER_ERROR));
        blog.changeEmailNotification(email);
        return blog;
    }

    public Blog changeTitle(Long blogId, String title) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.SERVER_ERROR));
        blog.changeTitle(title);
        return blog;
    }

    public Blog changeLocation(Long blogId, String newLocation) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.SERVER_ERROR));

        if (blogRepository.findByLocation(newLocation).isPresent()) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.SERVER_ERROR);
        }

        blog.changeLocation(newLocation);
        return blog;
    }

    public Blog deleteBlog(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.SERVER_ERROR));
        blog.changeExposureToDelete();
        return blog;
    }
}
