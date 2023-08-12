package com.study.badrequest.question.command.domain;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.hashtag.command.domain.HashTag;
import com.study.badrequest.hashtag.command.domain.HashTagRepository;
import com.study.badrequest.member.command.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class QuestionCreateServiceImpl {
    private final QuestionMemberRepository questionMemberRepository;
    private final QuestionRepository questionRepository;
    private final QuestionTagRepository questionTagRepository;
    private final HashTagRepository hashTagRepository;
    @Transactional
    public Long createQuestion(CreateQuestionForm form) {

        final String title = form.getTitle();
        final String contents = form.getContents();

        Member member = questionMemberRepository.findById(form.getMemberId())
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOTFOUND_MEMBER));

        Writer writer = Writer.createWriter(member.getId(), member.getMemberProfile().getNickname(), member.getMemberProfile().getProfileImage().getImageLocation(), member.getMemberProfile().getActivityScore(), WriterType.MEMBER);

        Question question = Question.createQuestion(title, contents, writer, QuestionMetrics.createQuestionMetrics());

        List<Long> tags = form.getHashTagIds();

        List<HashTag> hashTags = hashTagRepository.findAllById(tags);



        return questionRepository.save(question).getId();
    }
}
