package com.study.badrequest.repository.question.query;

import com.amazonaws.util.CollectionUtils;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;


import com.study.badrequest.commons.status.ExposureStatus;
import com.study.badrequest.domain.question.*;
import com.study.badrequest.domain.recommendation.Recommendation;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

import static com.study.badrequest.commons.status.ExposureStatus.PUBLIC;
import static com.study.badrequest.domain.board.QHashTag.*;
import static com.study.badrequest.domain.member.QMember.*;
import static com.study.badrequest.domain.member.QMemberProfile.*;
import static com.study.badrequest.domain.question.QQuestion.*;
import static com.study.badrequest.domain.question.QQuestionMetrics.*;
import static com.study.badrequest.domain.question.QQuestionTag.*;
import static com.study.badrequest.domain.recommendation.QRecommendation.recommendation;


@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class QuestionQueryRepositoryImpl implements QuestionQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 질문 상세 조회
     *
     * @param questionId:       질문 식별 아이디
     * @param loggedInMemberId: 현재 로그인된 회원 아이디
     * @param exposureStatus:   노출 상태
     * @return QuestionDetail
     * Updated 2023/5/23
     */
    public Optional<QuestionDetail> findQuestionDetail(Long questionId, Long loggedInMemberId, ExposureStatus exposureStatus) {
        log.info("[QUERY]=> findQuestionDetail- Question ID: {}", questionId);
        Optional<QuestionDetail> detailOptional = getQuestionDetailByQuestionIdAndExposureStatus(questionId, exposureStatus);

        detailOptional.ifPresent(detail -> organizeQuestionDetail(loggedInMemberId, detail));

        return detailOptional;
    }

    private void organizeQuestionDetail(Long loggedInMemberId, QuestionDetail detail) {
        addTagsToQuestionDetail(detail);
        setRecommendationInformation(detail);
        checkQuestioner(loggedInMemberId, detail);
    }

    private void addTagsToQuestionDetail(QuestionDetail detail) {
        List<TagDto> tagDtoList = findTagsDtoByQuestionId(detail.getId());

        if (CollectionUtils.isNullOrEmpty(tagDtoList)) {
            log.error("[QUERY]==> Tags should not accept empty values- QuestionID: {}", detail.getId());
        }

        detail.addTag(tagDtoList);
    }

    private void checkQuestioner(Long loggedInMemberId, QuestionDetail detail) {
        if (loggedInMemberId != null && Objects.equals(loggedInMemberId, detail.getQuestioner().getId())) {
            detail.isQuestionerToTrue();
        }
    }

    private void setRecommendationInformation(QuestionDetail detail) {
        Optional<Recommendation> optional = findRecommendationByQuestionId(detail.getId());

        if (optional.isPresent()) {
            Recommendation recommendation = optional.get();
            detail.getMetrics().setHasRecommendationAndKind(true, recommendation.getKind());
        }

    }

    private List<TagDto> findTagsDtoByQuestionId(Long questionId) {
        log.debug("[QUERY]==> findTagsDtoByQuestionId- QuestionID: {}", questionId);
        return findQuestionTagsByQuestionId(questionId)
                .stream()
                .map(tag -> new TagDto(tag.getId(), tag.getHashTag().getHashTagName()))
                .collect(Collectors.toList());
    }

    private Optional<Recommendation> findRecommendationByQuestionId(Long questionId) {
        log.debug("[QUERY]==> findRecommendationByQuestionId- QuestionID: {}", questionId);
        return jpaQueryFactory
                .selectDistinct(recommendation)
                .from(recommendation)
                .join(recommendation.member, member).fetchJoin()
                .where(recommendation.question.id.eq(questionId))
                .fetch()
                .stream()
                .findFirst();
    }


    private List<QuestionTag> findQuestionTagsByQuestionId(Long questionId) {
        log.debug("[QUERY]==> findQuestionTagsByQuestionId- QuestionID: {}", questionId);
        return jpaQueryFactory
                .selectDistinct(questionTag)
                .from(questionTag)
                .join(questionTag.hashTag, hashTag)
                .fetchJoin()
                .where(questionTag.question.id.eq(questionId))
                .fetch();
    }

    private Optional<QuestionDetail> getQuestionDetailByQuestionIdAndExposureStatus(Long questionId, ExposureStatus exposureStatus) {
        log.debug("[QUERY]==> getQuestionDetailByQuestionIdAndExposureStatus- QuestionID: {}, ExposureStatus: {}", questionId, exposureStatus);
        return jpaQueryFactory
                .select(
                        Projections.fields(QuestionDetail.class,
                                question.id.as("id"),
                                question.title.as("title"),
                                question.contents.as("contents"),
                                Expressions.asBoolean(false).as("isQuestioner"),
                                Projections.fields(QuestionDetail.QuestionDetailMetrics.class,
                                        questionMetrics.countOfRecommend.as("countOfRecommend"),
                                        questionMetrics.countOfView.as("countOfView"),
                                        questionMetrics.countOfAnswer.as("countOfAnswer"),
                                        Expressions.asBoolean(false).as("hasRecommendation")
                                ).as("metrics"),
                                Projections.fields(QuestionDetail.QuestionDetailQuestioner.class,
                                        member.id.as("id"),
                                        memberProfile.nickname.as("nickname"),
                                        memberProfile.profileImage.imageLocation.as("profileImage"),
                                        memberProfile.activityScore.as("activityScore")
                                ).as("questioner"),
                                question.askedAt.as("askedAt"),
                                question.modifiedAt.as("modifiedAt"))
                )
                .from(question)
                .join(question.questionMetrics, questionMetrics)
                .join(question.member, member)
                .join(member.memberProfile, memberProfile)
                .where(question.id.eq(questionId).and(eqExposure(exposureStatus)))
                .stream()
                .findFirst();
    }

    /**
     * 질문 리스트 조회
     *
     * @param condition: Long lastOfData: 다음 데이터를 조회하기 위한 마지막 데이터의 식별 값 입니다.,
     *                   Integer size: 조회당 요청 데이터의 개수입니다.,
     *                   QuestionSort sort: 데이터 정렬 조건입니다.
     * @return QuestionListResult
     * updated: 2023/5/23
     */
    @Override
    public QuestionListResult findQuestionListByCondition(QuestionSearchCondition condition) {
        log.info("[QUERY]=> findQuestionListByCondition LastOfData: {}, SORT: {}, SIZE: {}", condition.getLastOfData(), condition.getSort(), condition.getSize());
        final int limitSize = setLimitSize(condition.getSize());

        List<Long> questionIdList = selectQuestionIdList(condition.getLastOfData(), limitSize, condition.getSort());

        List<QuestionDto> questionListDto = selectQuestionDtoInIds(questionIdList);

        sortListByQuestionSort(condition.getSort(), questionListDto);

        addQuestionTagToListDto(questionListDto);

        int resultSize = questionListDto.size();

        boolean hasNext = resultSize > limitSize;

        if (hasNext) {
            questionListDto.remove(limitSize);
            --resultSize;
        }

        long lastData = 0L;

        QuestionSort sortBy = condition.getSort() == null ? QuestionSort.NEW_EAST : condition.getSort();
        switch (sortBy) {
            case NEW_EAST:
                lastData = questionListDto.stream()
                        .mapToLong(QuestionDto::getId)
                        .min()
                        .orElse(0);
                break;
            case VIEW:
                lastData = questionListDto.stream()
                        .mapToLong(q -> q.getMetrics().getCountOfView())
                        .min()
                        .orElse(0);

                break;
            case RECOMMEND:
                lastData = questionListDto.stream()
                        .mapToLong(q -> q.getMetrics().getCountOfRecommend())
                        .min()
                        .orElse(0);
                break;
        }

        return new QuestionListResult(resultSize, hasNext, sortBy, lastData, questionListDto);

    }

    public QuestionListResult findQuestionListByHashTag(QuestionSearchConditionWithHashTag condition) {

        int limitSize = setLimitSize(condition.getSize());

        if (!isHashTag(condition.getTag())) {
            throw new IllegalArgumentException("IS NOT HASH TAG");
        }

        List<String> extractedTags = getExtractedTags(condition.getTag());

        List<QuestionTag> questionTags = jpaQueryFactory
                .selectDistinct(questionTag)
                .from(questionTag)
                .join(questionTag.hashTag, hashTag).fetchJoin()
                .where(hashTagCursor(condition.getLastIndex()),
                        getHasTagPredicate(extractedTags)
                )
                .limit(limitSize + 1)
                .fetch();

        List<Long> questionIds = findQuestionIds(questionTags);

        List<QuestionDto> questionListDto = selectQuestionDtoInIds(questionIds);

        addQuestionTagToListDto(questionListDto);

        questionListDto.sort(Comparator.comparing(QuestionDto::getAskedAt));

        return new QuestionListResult();
    }

    /*
        QuestionSort 에 따라 쿼리해온 데이터 정렬
        NEW_EAST: questionId 로 Order By questionId DESC 로 정렬 불필요
    */
    private void sortListByQuestionSort(QuestionSort sort, List<QuestionDto> questionListDto) {
        log.debug("[QUERY]==> sortListByQuestionSort Sort: {}",sort);
        if (sort != null) {

            final Comparator<QuestionDto> comparator;

            switch (sort) {
                case RECOMMEND:
                    comparator = Comparator.comparing(q -> q.getMetrics().getCountOfRecommend());
                    questionListDto.sort(comparator.reversed());
                    break;
                case VIEW:
                    comparator = Comparator.comparing(q -> q.getMetrics().getCountOfView());
                    questionListDto.sort(comparator.reversed());
                    break;

            }
        }
    }

    /**
     * Question.questionId 리스트 조회
     *
     * @param lastOfData  :  조회 시작 위치
     * @param limitSize :  조회 데이터 크기
     * @param sort
     * @return List<Long>: Question ID
     */
    private List<Long> selectQuestionIdList(Long lastOfData, Integer limitSize, QuestionSort sort) {
        log.debug("[QUERY]==> selectQuestionIdList- LastOfData: {}, LimitSize: {}, SORT: {}", lastOfData, limitSize, sort);
        final JPAQuery<Long> selectIdFromQuestion = jpaQueryFactory.select(question.id).from(question);

        if (sort == null) {
            log.debug("[QUERY]===> selectIdFromQuestion SortBy NEW");
            selectIdFromQuestion
                    .where(lastIndexCursor(lastOfData), eqExposure(PUBLIC))
                    .orderBy(question.id.desc())
                    .limit(limitSize + 1);
        }

        if (sort != null) {
            List<Long> questionIds = selectQuestionIdWithSort(lastOfData, limitSize, sort);
            selectIdFromQuestion
                    .where(question.id.in(questionIds));
        }

        List<Long> results = selectIdFromQuestion.fetch();

        return CollectionUtils.isNullOrEmpty(results) ? new ArrayList<>() : results;
    }

    private List<Long> selectQuestionIdWithSort(Long lastData, Integer limitSize, QuestionSort sort) {
        log.debug("[QUERY]===> selectQuestionIdWithSort SORT: {}",sort);
        final JPAQuery<Long> queryWithSort = jpaQueryFactory
                .select(questionMetrics.question.id)
                .from(questionMetrics);

        switch (sort) {
            case VIEW:
                return queryWithSort.where(
                                lastViewCursor(lastData),
                                questionMetrics.exposure.eq(PUBLIC))
                        .orderBy(questionMetrics.countOfView.desc(), questionMetrics.id.desc())
                        .limit(limitSize + 1)
                        .fetch();

            case RECOMMEND:
                return queryWithSort.where(
                                lastRecommendCursor(lastData),
                                questionMetrics.exposure.eq(PUBLIC))
                        .orderBy(questionMetrics.countOfRecommend.desc(), questionMetrics.id.desc())
                        .limit(limitSize + 1)
                        .fetch();

        }
        return new ArrayList<>();
    }


    private Predicate lastRecommendCursor(Long lastOfData) {
        return lastOfData == null ? questionMetrics.countOfRecommend.lt(Integer.MAX_VALUE) : questionMetrics.countOfRecommend.lt(lastOfData.intValue());
    }


    private Predicate lastViewCursor(Long lastOfData) {
        return lastOfData == null ? questionMetrics.countOfView.lt(Integer.MAX_VALUE) : questionMetrics.countOfView.lt(lastOfData.intValue());
    }

    private List<QuestionDto> selectQuestionDtoInIds(List<Long> questionIds) {
        log.debug("[QUERY]==> selectQuestionDtoInIds- QuestionID: {}",questionIds);
        return jpaQueryFactory
                .select(Projections.fields(QuestionDto.class,
                        question.id.as("id"),
                        question.title.as("title"),
                        question.preview.as("preview"),
                        Projections.fields(QuestionDto.Metrics.class,
                                questionMetrics.countOfRecommend.as("countOfRecommend"),
                                questionMetrics.countOfView.as("countOfView"),
                                questionMetrics.countOfAnswer.as("countOfAnswer")
                        ).as("metrics"),
                        Projections.fields(QuestionDto.Questioner.class,
                                member.id.as("id"),
                                memberProfile.nickname.as("nickname"),
                                memberProfile.profileImage.imageLocation.as("profileImage"),
                                memberProfile.activityScore.as("activityScore")
                        ).as("questioner"),
                        question.askedAt.as("askedAt")))
                .from(question)
                .join(question.questionMetrics, questionMetrics)
                .join(question.member, member)
                .join(member.memberProfile, memberProfile)
                .where(question.id.in(questionIds))
                .orderBy(question.id.desc())
                .fetch();
    }

    private List<QuestionTagDto> findQuestionTagDtoByQuestionIds(List<Long> ids) {
        List<Long> questionTagIds = findQuestionTagsInQuestionIds(ids);

        if(CollectionUtils.isNullOrEmpty(questionTagIds)){
            log.error("[QUERY]===> findQuestionTagsInQuestionIds Not Allow EMPTY");
        }

        return findQuestionTagDtosByQuestionTagIds(questionTagIds);
    }

    private List<QuestionTagDto> findQuestionTagDtosByQuestionTagIds(List<Long> questionTagIds) {
        log.debug("[QUERY]==> findQuestionTagDtosByQuestionTagIds");
        return jpaQueryFactory
                .select(
                        Projections.fields(QuestionTagDto.class,
                                questionTag.id.as("id"),
                                questionTag.question.id.as("questionId"),
                                hashTag.id.as("hashTagId"),
                                hashTag.hashTagName.as("hashTagName")
                        ))
                .from(questionTag)
                .join(questionTag.hashTag, hashTag)
                .where(questionTag.id.in(questionTagIds))
                .orderBy(questionTag.id.asc())
                .fetch();
    }

    private List<Long> findQuestionTagsInQuestionIds(List<Long> questionIds) {
        log.debug("[QUERY]==> findQuestionTagsInQuestionIds QuestionID: {}",questionIds);
        return jpaQueryFactory
                .select(questionTag.id)
                .from(questionTag)
                .where(questionTag.question.id.in(questionIds))
                .fetch();
    }

    private void addQuestionTagToListDto(List<QuestionDto> questionListDto) {

        List<Long> questionsIds = getQuestionsIds(questionListDto);

        List<QuestionTagDto> questionTagDtos = findQuestionTagDtoByQuestionIds(questionsIds);

        Map<Long, List<QuestionTagDto>> longListMap = groupQuestionTagsByQuestionId(questionTagDtos);

        addGroupedQuestionTagsToQuestionDto(questionListDto, longListMap);
    }


    private List<Long> findQuestionIds(List<QuestionTag> questionTagsInHashTags) {
        return questionTagsInHashTags.stream()
                .map(t -> t.getQuestion().getId())
                .collect(Collectors.toList());
    }

    private boolean isHashTag(String searchBy) {
        return searchBy != null && searchBy.startsWith("#");
    }

    private List<Long> getQuestionsIds(List<QuestionDto> questionListDto) {
        return questionListDto.stream()
                .map(QuestionDto::getId)
                .collect(Collectors.toList());
    }

    private void addGroupedQuestionTagsToQuestionDto(List<QuestionDto> questionListDto, Map<Long, List<QuestionTagDto>> questionTagMap) {
        if (questionTagMap != null && !questionTagMap.isEmpty()) {
            questionListDto.forEach(
                    dto -> dto.addTags(
                            questionTagMap.get(dto.getId())
                                    .stream()
                                    .map(t -> new TagDto(t.getId(), t.getHashTagName()))
                                    .collect(Collectors.toList())
                    ));
        }
    }

    private Predicate getHasTagPredicate(List<String> hashTags) {
        return hashTags.size() > 1 ? tagNamesIn(hashTags) : eqTagName(hashTags.get(0));
    }

    private BooleanExpression containsTitle(String keyword) {
        return keyword == null ? null : question.title.containsIgnoreCase(keyword);
    }

    private BooleanExpression eqExposure(ExposureStatus status) {
        return status == null ? question.exposure.eq(PUBLIC) : question.exposure.eq(status);
    }

    private Map<Long, List<QuestionTagDto>> groupQuestionTagsByQuestionId(List<QuestionTagDto> questionTagList) {
        return questionTagList.stream()
                .collect(Collectors.groupingBy(QuestionTagDto::getQuestionId));
    }

    private BooleanExpression lastIndexCursor(Long questionId) {

        long index = 0L;

        if (questionId != null) {
            index = questionId;
        }
        return index <= 0 ? null : question.id.lt(questionId);
    }

    private BooleanExpression hashTagCursor(Long questionId) {
        long index = 0L;

        if (questionId != null) {
            index = questionId;
        }
        return index <= 0 ? null : questionTag.question.id.lt(questionId);
    }

    private int setLimitSize(Integer limitSize) {

        final int defaultLimitSize = 10;

        return limitSize == null ? defaultLimitSize : limitSize;
    }

    private Predicate eqTagName(String hashTag) {
        return questionTag.hashTag.hashTagName.eq(hashTag);
    }

    private Predicate tagNamesIn(List<String> extractedTags) {
        return questionTag.hashTag.hashTagName.in(extractedTags);
    }

    private List<String> getExtractedTags(String searchWord) {
        List<String> collect = Arrays.stream(searchWord.split("#")).collect(Collectors.toList());
        return collect.subList(1, collect.size()).stream().map(tag -> "#" + tag.toLowerCase()).collect(Collectors.toList());
    }


}
