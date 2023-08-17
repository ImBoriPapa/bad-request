package com.study.badrequest.question.command.application;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.common.response.ApiResponseStatus;

import com.study.badrequest.question.command.domain.Tag;
import com.study.badrequest.question.command.domain.TagRepository;
import com.study.badrequest.question.command.application.dto.CreateQuestionForm;
import com.study.badrequest.question.command.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class QuestionCreateServiceImpl implements QuestionCreateService {
    private final MemberInformationRepository memberInformationRepository;
    private final WriterRepository writerRepository;
    private final QuestionRepository questionRepository;
    private final QuestionTagRepository questionTagRepository;
    private final TagRepository tagRepository;

    /**
     * 질문 생성
     *
     * @param form CreateQuestionForm
     * @return memberId (Long)
     * @ImplNote 1. memberInformationRepository.findById(Long memberId) 를 사용해서 Writer 엔티티 생성을 위한 회원 정보를 조회합니다.
     * 2. Writer 엔티티를 생성하고 영속화합니다.
     * 3. Question 엔티티를 생성하고 영속화합니다.
     * 4. tagIds 로 태그를 조회
     * 5. QuestionTag 를 생성하고 영속화
     * 6. Question 을 생성하고 영속화
     * 7. QuestionCreate 이벤트를 발행
     * @see MemberInformationRepository#findById(Long)
     * @see WriterRepository#save(Writer)
     * @see TagRepository#findAllById(Iterable)
     * @see QuestionTagRepository#saveAllQuestionTag(Iterable)
     * @see Question#createQuestion(String, String, Writer, List, QuestionMetrics)
     * @see QuestionRepository#save(Question)
     */
    @Transactional
    @Override
    public Long createQuestion(CreateQuestionForm form) {

        final String title = form.getTitle();
        final String contents = form.getContents();

        MemberInformation memberInformation = getMemberInformation(form);

        Writer writer = createAndPersisteWriter(memberInformation);

        List<Tag> tags = tagRepository.findAllById(form.getTagIds());

        List<QuestionTag> questionTags = createQuestionTags(tags);

        Question question = persisteQuestion(title, contents, writer, questionTags);

        return question.getId();
    }

    private Question persisteQuestion(String title, String contents, Writer writer, List<QuestionTag> questionTags) {
        Question question = Question.createQuestion(title, contents, writer, questionTags, QuestionMetrics.createQuestionMetrics());
        return questionRepository.save(question);
    }

    private List<QuestionTag> createQuestionTags(List<Tag> tags) {
        List<QuestionTag> questionTags = new ArrayList<>();

        for (Tag tag : tags) {
            questionTags.add(QuestionTag.createQuestionTag(tag));
        }

        return questionTagRepository.saveAllQuestionTag(questionTags);
    }

    private MemberInformation getMemberInformation(CreateQuestionForm form) {
        return memberInformationRepository.findById(form.getMemberId())
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOTFOUND_MEMBER));
    }

    private Writer createAndPersisteWriter(MemberInformation memberInformation) {
        return writerRepository.save(Writer.createWriter(memberInformation.getMemberId(), memberInformation.getNickname(), memberInformation.getProfileImage(), memberInformation.getActivityScore(), WriterType.MEMBER));
    }
}
