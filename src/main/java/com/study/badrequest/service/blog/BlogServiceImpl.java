package com.study.badrequest.service.blog;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.blog.Blog;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.blog.BlogRepository;
import com.study.badrequest.repository.member.MemberRepository;
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
        Member member = memberRepository.findById(memberId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOTFOUND_MEMBER));

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
