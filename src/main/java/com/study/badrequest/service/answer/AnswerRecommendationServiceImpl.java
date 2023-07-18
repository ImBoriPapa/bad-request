package com.study.badrequest.service.answer;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.answer.Answer;
import com.study.badrequest.domain.answerRecommendation.AnswerRecommendation;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.recommendation.RecommendationKind;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.answer.AnswerRepository;
import com.study.badrequest.repository.answerRecommendation.AnswerRecommendationRepository;
import com.study.badrequest.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnswerRecommendationServiceImpl implements AnswerRecommendationService {
    private final AnswerRecommendationRepository answerRecommendationRepository;
    private final MemberRepository memberRepository;
    private final AnswerRepository answerRepository;

    @Transactional
    public void createRecommendation(Long memberId, Long answerId, RecommendationKind kind) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOTFOUND_MEMBER));
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOT_FOUND_ANSWER));
        AnswerRecommendation recommendation = AnswerRecommendation.createRecommendation(member, answer, kind);

        AnswerRecommendation answerRecommendation = answerRecommendationRepository.save(recommendation);
    }

    @Transactional
    public void deleteRecommendation(Long recommendationId) {
        AnswerRecommendation recommendation = answerRecommendationRepository.findById(recommendationId).orElseThrow(() -> new IllegalArgumentException(""));
        recommendation.getAnswer().decrementRecommendation();

        answerRecommendationRepository.delete(recommendation);
    }

}
