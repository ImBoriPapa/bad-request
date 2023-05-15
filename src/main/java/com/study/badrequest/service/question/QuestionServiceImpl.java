package com.study.badrequest.service.question;

import com.study.badrequest.domain.board.HashTag;
import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.question.*;
import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.event.question.QuestionEventDto;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.board.HashTagRepository;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.repository.question.QuestionRepository;
import com.study.badrequest.repository.question.QuestionTagRepository;
import com.study.badrequest.repository.reommendation.RecommendationRepository;
import com.study.badrequest.utils.cookie.CookieFactory;
import com.study.badrequest.utils.hash_tag.HashTagUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;

import java.util.*;

import java.util.stream.Collectors;

import static com.study.badrequest.commons.response.ApiResponseStatus.*;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final HashTagRepository hashTagRepository;
    private final QuestionTagRepository questionTagRepository;
    private final RecommendationRepository recommendationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    @Transactional
    public QuestionResponse.Create creteQuestion(Long memberId, QuestionRequest.Create form) {
        log.info("질문 생성 시작 요청 회원 아이디: {}, 제목: {}", memberId, form.getTitle());

        validateTags(form);

        Member member = findMemberById(memberId);
        // 질문 엔티티 생성
        Question question = questionRepository.save(createQuestion(form, member));
        // 질문 Metrics 생성
        question.addQuestionMetrics(QuestionMetrics.createQuestionMetrics(question));
        // 태그 저장
        saveQuestionTag(form.getTags(), question);

        //이벤트: 1.이미지 상태 변경 2. 회원 활동 점수 변경
        applicationEventPublisher.publishEvent(new QuestionEventDto.Create(member, question, form.getTags(), form.getImageIds()));

        return new QuestionResponse.Create(question.getId(), question.getAskedAt());
    }

    private Question createQuestion(QuestionRequest.Create form, Member member) {
        return Question.createQuestion()
                .title(form.getTitle())
                .contents(form.getContents())
                .member(member)
                .build();
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(NOTFOUND_MEMBER));
    }

    private void validateTags(QuestionRequest.Create form) {
        if (form.getTags().size() < 1 || form.getTags().size() > 5) {
            throw new CustomRuntimeException(AT_LEAST_ONE_TAG_MUST_BE_USED_AND_AT_MOST_FIVE_TAGS_MUST_BE_USED);
        }
    }

    private void saveQuestionTag(List<String> tags, Question question) {
        log.info("질문 태그 저장 시작");
        tags.forEach(t -> log.info("requested tag name: {}", t));

        Set<String> requestedTags = tags.stream().map(HashTagUtils::stringToHashTagString).collect(Collectors.toSet());

        List<HashTag> findHashTagByRequestedTags = hashTagRepository.findAllByHashTagNameIn(requestedTags);

        // 기존에 저장된 해시태그가 없을 경우
        if (findHashTagByRequestedTags.isEmpty()) {
            createQuestionTags(question, requestedTags);
        } else
            createQuestionTags(question, requestedTags, findHashTagByRequestedTags);
    }

    private void createQuestionTags(Question question, Set<String> requestedTags, List<HashTag> findHashTagByRequestedTags) {
        Map<String, QuestionTag> haveToSave = new HashMap<>();
        // 이미 등록된 해시태그로 질문 태그를 생성해서 haveToSave 에 저장
        findHashTagByRequestedTags.stream()
                .map(hashTag -> QuestionTag.createQuestionTag(question, hashTag))
                .forEach(questionTag -> haveToSave.put(questionTag.getHashTag().getHashTagName(), questionTag));

        // 이미 등록된 해시태그의 태그네임과 같지않은 요청된 태그네임만 새로운 해시태그로 저장
        List<HashTag> newHashTags = requestedTagMapToHashTags(requestedTags).stream()
                .filter(hashTag -> !haveToSave.containsKey(hashTag.getHashTagName()))
                .collect(Collectors.toList());

        //새롭게 저장된 해시태그를 haveToSave에 저장
        hashTagMapQuestionTag(question, newHashTags).forEach(questionTag -> haveToSave.put(questionTag.getHashTag().getHashTagName(), questionTag));

        //만들어진 모든 질문 태그를 저장
        questionTagRepository.saveAll(haveToSave.values());
    }

    private void createQuestionTags(Question question, Set<String> requestedTags) {
        List<HashTag> newHashTags = requestedTagMapToHashTags(requestedTags);

        questionTagRepository.saveAll(hashTagMapQuestionTag(question, newHashTags));
    }

    private List<HashTag> requestedTagMapToHashTags(Set<String> requestedTags) {
        return requestedTags.stream()
                .map(HashTag::new)
                .collect(Collectors.toList());
    }

    private List<QuestionTag> hashTagMapQuestionTag(Question question, List<HashTag> newHashTags) {
        return hashTagRepository.saveAll(newHashTags)
                .stream().map(hashTag -> QuestionTag.createQuestionTag(question, hashTag))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteQuestionTag(Long questionTagId) {
        QuestionTag questionTag = questionTagRepository.findById(questionTagId).orElseThrow(() -> new CustomRuntimeException(NOT_FOUND_QUESTION_TAG));
        questionTagRepository.delete(questionTag);

    }

    @Transactional
    public void addQuestionTag(Long questionId, String questionTag) {
        log.info("질문 태그 추가 시작");
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomRuntimeException(NOT_FOUND_QUESTION));

        String hashTag = HashTagUtils.stringToHashTagString(questionTag);

        QuestionTag newQuestionTag = hashTagRepository
                .findByHashTagName(hashTag)
                .map(tag -> QuestionTag.createQuestionTag(question, tag))
                .orElseGet(() -> QuestionTag.createQuestionTag(question, new HashTag(hashTag)));

        questionTagRepository.save(newQuestionTag);
    }

    @Transactional
    public QuestionResponse.Modify modifyQuestion(Long memberId, Long questionId, QuestionRequest.ModifyForm form) {
        log.info("질문 수정 시작");

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomRuntimeException(NOT_FOUND_QUESTION));

        if (!question.getMember().getId().equals(memberId)) {
            throw new CustomRuntimeException(PERMISSION_DENIED);
        }

        question.modify(form.getTitle(), form.getContents());

        applicationEventPublisher.publishEvent(new QuestionEventDto.Modify(question, form.getImageIds()));

        return new QuestionResponse.Modify(question.getId(), question.getModifiedAt());
    }

    @Transactional
    public QuestionResponse.Delete deleteQuestion(Long memberId, Long questionId) {
        log.info("질문 삭제 시작 요청 회원: {}, 질문 아이디: {}", memberId, questionId);
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomRuntimeException(NOT_FOUND_QUESTION));

        if (!question.getMember().getId().equals(memberId)) {
            throw new CustomRuntimeException(PERMISSION_DENIED);
        }

        question.changeExposureToDelete(ExposureStatus.DELETE);

        return new QuestionResponse.Delete(questionId, question.getDeletedRequestAt());
    }

    @Transactional
    public QuestionResponse.Modify createRecommendation(Long memberId, Authority authority, Long questionId, RecommendationKind recommendationKind) {

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
    public QuestionResponse.Modify deleteRecommendation(Long memberId, Authority authority, Long questionId) {
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
