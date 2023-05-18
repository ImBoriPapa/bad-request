package com.study.badrequest.service.questionComment;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.domain.questionComment.QuestionComment;
import com.study.badrequest.dto.questionComment.QuestionCommentRequest;
import com.study.badrequest.dto.questionComment.QuestionCommentResponse;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.repository.question.QuestionRepository;
import com.study.badrequest.repository.questionComment.QuestionCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.study.badrequest.commons.response.ApiResponseStatus.NOT_FOUND_COMMENT;
import static com.study.badrequest.commons.response.ApiResponseStatus.PERMISSION_DENIED;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionCommentServiceImpl implements QuestionCommentService {
    private final QuestionCommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public QuestionCommentResponse.Add addComment(Long memberId, Long questionId, QuestionCommentRequest.Add form) {
        log.info("질문글 댓글 추가");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.NOTFOUND_MEMBER));
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.NOT_FOUND_QUESTION));

        QuestionComment comment = QuestionComment.builder()
                .contents(form.getContents())
                .writer(member)
                .question(question)
                .addedAt(LocalDateTime.now())
                .build();

        QuestionComment saveComment = commentRepository.save(comment);
        return new QuestionCommentResponse.Add(saveComment.getId(), saveComment.getAddedAt());
    }

    @Transactional
    public QuestionCommentResponse.Delete deleteComment(Long memberId, Long questionCommentId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.NOTFOUND_MEMBER));

        QuestionComment comment = commentRepository.findById(questionCommentId).orElseThrow(() -> new CustomRuntimeException(NOT_FOUND_COMMENT));

        if (!member.getId().equals(comment.getWriter().getId())) {
            throw new CustomRuntimeException(PERMISSION_DENIED);
        }

        comment.statusToDelete();

        return new QuestionCommentResponse.Delete(comment.getDeletedAt());
    }
}
