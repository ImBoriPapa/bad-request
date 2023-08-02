package com.study.badrequest.question.command.application;

import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.recommandation.command.domain.Recommendation;
import com.study.badrequest.recommandation.command.domain.RecommendationKind;
import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.member.command.domain.MemberRepository;
import com.study.badrequest.question.command.domain.QuestionRepository;
import com.study.badrequest.recommandation.command.domain.RecommendationRepository;
import com.study.badrequest.utils.cookie.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static com.study.badrequest.common.response.ApiResponseStatus.NOT_FOUND_QUESTION;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestionMetricsServiceImpl implements QuestionMetricsService {
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final RecommendationRepository recommendationRepository;

    @Transactional
    public QuestionResponse.Modify createRecommendation(Long memberId, Long questionId) {

        boolean exists = recommendationRepository.existsByMemberIdAndQuestionId(memberId, questionId);
        if (exists) {
            throw new IllegalArgumentException("질문에 추천 혹은 비추천은 한개만 가능하다.");
        }

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException(""));

        Question question = questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException(""));

        Recommendation recommendation = Recommendation.createRecommendation(member, question, RecommendationKind.RECOMMENDATION);

        recommendationRepository.save(recommendation);

        return new QuestionResponse.Modify(question.getId(), question.getModifiedAt());
    }

    @Transactional
    public QuestionResponse.Modify deleteRecommendation(Long memberId, Long questionId) {
        Recommendation recommendation = recommendationRepository.findByMemberIdAndQuestionId(memberId, questionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는다."));
        recommendation
                .getQuestion()
                .getQuestionMetrics()
                .decrementCountOfRecommendations();
        recommendationRepository.deleteById(recommendation.getId());
        return new QuestionResponse.Modify(questionId, LocalDateTime.now());
    }

    @Transactional
    public void incrementViewWithCookie(HttpServletRequest request, HttpServletResponse response, Long questionId) {
        log.info("incrementViewWithCookie");
        final String cookieName = "view_count";
        final int maxAge = 3600;

        Optional<Cookie> viewCookie = CookieUtils.getCookie(request, cookieName);

        if (viewCookie.isPresent()) {
            String value = viewCookie.get().getValue();

            String[] strings = value.contains("-") ? value.split("-") : new String[]{value};

            if (!Arrays.asList(strings).contains(questionId.toString())) {
                String added = value + "-" + questionId;
                CookieUtils.addCookie(response, cookieName, added, maxAge);
                incrementViewCount(questionId);
            }

        } else {
            CookieUtils.addCookie(response, cookieName, questionId.toString(), maxAge);
            incrementViewCount(questionId);

        }

    }

    private void incrementViewCount(Long questionId) {
        log.info("조회수 증가");
        Question question = questionRepository
                .findById(questionId)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOT_FOUND_QUESTION));
        question.getQuestionMetrics().incrementCountOfView();

    }
}
