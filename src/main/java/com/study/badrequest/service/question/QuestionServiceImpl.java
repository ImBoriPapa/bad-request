package com.study.badrequest.service.question;

import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.question.*;
import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.event.question.QuestionEventDto;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.repository.question.QuestionRepository;
import com.study.badrequest.repository.reommendation.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {

    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final RecommendationRepository recommendationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public QuestionResponse.Create creteQuestion(Long memberId, QuestionRequest.CreateForm form) {
        log.info("질문 생성 시작");

        if (form.getTags() == null || form.getTags().size() < 1) {
            throw new IllegalArgumentException("태그는 1개이상 등록해야됨");
        }

        if (form.getTags().size() > 5) {
            throw new IllegalArgumentException("태그는 최대 5개 까지 등록할수 있다");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(""));

        Question question = Question.createQuestion()
                .title(form.getTitle())
                .contents(form.getContents())
                .member(member)
                .build();

        Question save = questionRepository.save(question);
        save.addQuestionMetrics(QuestionMetrics.createQuestionMetrics(question));

        applicationEventPublisher.publishEvent(new QuestionEventDto.Create(member, save, form.getTags()));

        return new QuestionResponse.Create(save.getId(), save.getAskedAt());
    }

    @Transactional
    public QuestionResponse.Modify modifyQuestion(Long memberId, Authority authority, Long questionId, QuestionRequest.ModifyForm form) {
        log.info("질문 수정");

        Question question = questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException(""));

        if (!question.getMember().getId().equals(memberId) || authority != Authority.ADMIN) {
            throw new IllegalArgumentException("수정 권한 없음");
        }

        question.modify(form.getTitle(), form.getContents());

        return new QuestionResponse.Modify();
    }

    @Transactional
    public QuestionResponse.Delete deleteQuestion(Long memberId, Authority authority, Long questionId) {

        Question question = questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException(""));
        question.changeExposureToDelete(ExposureStatus.DELETE);

        return new QuestionResponse.Delete(questionId, question.getDeletedRequestAt());
    }

    @Transactional
    public QuestionResponse.Modify createRecommendation(Long memberId, Authority authority, Long questionId, RecommendationKind recommendationKind) {

        boolean exists = recommendationRepository.existsByMemberIdAndQuestionId(memberId, questionId);
        if (exists) {
            throw new IllegalArgumentException("질문에 추천 혹은 비추천은 한개만 가능하다.");
        }

        Member member = memberRepository.findByIdAndAuthority(memberId, authority).orElseThrow(() -> new IllegalArgumentException(""));

        Question question = questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException(""));

        Recommendation recommendation = Recommendation.createRecommendation(member, question, RecommendationKind.RECOMMENDATION);

        recommendationRepository.save(recommendation);

        return new QuestionResponse.Modify(question.getId(), question.getModifiedAt());
    }

    @Transactional
    public QuestionResponse.Modify deleteRecommendation(Long memberId, Authority authority, Long questionId) {
        Recommendation recommendation = recommendationRepository.findByMemberIdAndQuestionId(memberId, questionId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는다."));
        recommendation.changeToUnRecommendation();
        recommendationRepository.deleteById(recommendation.getId());
        return new QuestionResponse.Modify(questionId, LocalDateTime.now());
    }

    // TODO: 2023/05/08 어뷰징 방지 생각해보기
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public void incrementViewCount(Long questionId, ExposureStatus exposureStatus) {
        log.info("게시글 조회수 증가");

        Question question = questionRepository
                .findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException(""));
        log.info("조회 수 증가 ");
        question.getQuestionMetrics().incrementCountOfView();

    }


}
