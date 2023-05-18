package com.study.badrequest.service.question;

import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.domain.recommendation.Recommendation;
import com.study.badrequest.domain.recommendation.RecommendationKind;
import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.repository.question.QuestionRepository;
import com.study.badrequest.repository.reommendation.RecommendationRepository;
import com.study.badrequest.utils.cookie.CookieFactory;
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

import static com.study.badrequest.commons.response.ApiResponseStatus.NOT_FOUND_QUESTION;

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
        Recommendation recommendation = recommendationRepository.findByMemberIdAndQuestionId(memberId, questionId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는다."));
        recommendation.changeToUnRecommendation();
        recommendationRepository.deleteById(recommendation.getId());
        return new QuestionResponse.Modify(questionId, LocalDateTime.now());
    }

    @Transactional
    public void incrementViewWithCookie(HttpServletRequest request, HttpServletResponse response, Long questionId) {
        log.info("incrementViewWithCookie");
        final String cookieName = "view_count";
        final int maxAge = 3600;

        Optional<Cookie> viewCookie = CookieFactory.getCookie(request, cookieName);

        if (viewCookie.isPresent()) {
            String value = viewCookie.get().getValue();

            String[] strings = value.contains("-") ? value.split("-") : new String[]{value};

            if (!Arrays.asList(strings).contains(questionId.toString())) {
                String added = value + "-" + questionId;
                CookieFactory.addCookie(response, cookieName, added, maxAge);
                incrementViewCount(questionId);
            }

        } else {
            CookieFactory.addCookie(response, cookieName, questionId.toString(), maxAge);
            incrementViewCount(questionId);

        }

    }

    private void incrementViewCount(Long questionId) {
        log.info("조회수 증가");

        Question question = questionRepository
                .findById(questionId)
                .orElseThrow(() -> new CustomRuntimeException(NOT_FOUND_QUESTION));
        question.getQuestionMetrics().incrementCountOfView();

    }
}
