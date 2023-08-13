package com.study.badrequest.question.query;

import com.amazonaws.util.CollectionUtils;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;


import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.common.status.ExposureStatus;
import com.study.badrequest.hashtag.command.domain.QTag;
import com.study.badrequest.recommandation.command.domain.Recommendation;


import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.question.command.domain.QuestionSortType;
import com.study.badrequest.question.command.domain.QuestionTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

import static com.study.badrequest.common.status.ExposureStatus.PUBLIC;

import static com.study.badrequest.hashtag.command.domain.QTag.*;
import static com.study.badrequest.member.command.domain.QMember.member;
import static com.study.badrequest.member.command.domain.QMemberProfile.memberProfile;
import static com.study.badrequest.question.command.domain.QQuestion.question;
import static com.study.badrequest.question.command.domain.QQuestionMetrics.questionMetrics;
import static com.study.badrequest.question.command.domain.QQuestionTag.questionTag;
import static com.study.badrequest.question.command.domain.QuestionSortType.*;
import static com.study.badrequest.recommandation.command.domain.QRecommendation.recommendation;


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
        log.info("findQuestionDetail questionId: {}", questionId);
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
                .map(tag -> new TagDto(tag.getId(), tag.getTag().getName()))
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
                .join(questionTag.tag, tag)
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
     *                   QuestionSortType sort: 조회 정렬 조건입니다.
     * @return QuestionListResult
     * updated: 2023/7/25
     */
    @Override
    public QuestionListResult findQuestionListByCondition(QuestionSearchCondition condition) {
        log.info("findQuestionListByCondition");
        final long LAST_OF_DATA = initializationLastOfData(condition.getLastOfData());
        final int LIMIT_SIZE = initializationLimitSize(condition.getSize());
        final QuestionSortType SORT_TYPE = initializationSortType(condition.getSort());

        List<Long> questionIdList = selectQuestionIdList(LAST_OF_DATA, LIMIT_SIZE, SORT_TYPE);

        List<QuestionDto> questionListDto = selectQuestionDtoInIds(questionIdList);

        sortListByQuestionSortType(SORT_TYPE, questionListDto);

        addQuestionTagsToQuestionListDto(questionListDto);

        return createQuestionListResult(LIMIT_SIZE, SORT_TYPE, questionListDto);

    }

    private long initializationLastOfData(Long lastOfData) {
        return lastOfData == null ? -1L : lastOfData;
    }


    private QuestionListResult createQuestionListResult(int LIMIT_SIZE, QuestionSortType SORT_TYPE, List<QuestionDto> questionListDto) {
        boolean hasNext = questionListDto.size() > LIMIT_SIZE;

        int resultSize = initializationResultSize(LIMIT_SIZE, questionListDto, hasNext);

        long lastOfData = initializationLastOfData(SORT_TYPE, questionListDto);

        return new QuestionListResult(resultSize, hasNext, SORT_TYPE, lastOfData, questionListDto);
    }

    private int initializationResultSize(int LIMIT_SIZE, List<QuestionDto> questionListDto, boolean hasNext) {
        int resultSize = questionListDto.size();

        if (hasNext) {
            questionListDto.remove(LIMIT_SIZE);
            --resultSize;
        }
        return resultSize;
    }

    private long initializationLastOfData(QuestionSortType SORT_TYPE, List<QuestionDto> questionListDto) {
        long lastData = 0L;

        switch (SORT_TYPE) {
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
        return lastData;
    }

    private static QuestionSortType initializationSortType(QuestionSortType sortType) {
        return sortType == null ? NEW_EAST : sortType;
    }

    public QuestionListResult findQuestionListByHashTag(QuestionSearchConditionWithHashTag condition) {

        int limitSize = initializationLimitSize(condition.getSize());

        if (!isHashTag(condition.getTag())) {
            throw new IllegalArgumentException("IS NOT HASH TAG");
        }

        List<String> extractedTags = getExtractedTags(condition.getTag());

        List<QuestionTag> questionTags = jpaQueryFactory
                .selectDistinct(questionTag)
                .from(questionTag)
                .join(questionTag.tag, tag).fetchJoin()
                .where(hashTagCursor(condition.getLastIndex()),
                        getHasTagPredicate(extractedTags)
                )
                .limit(limitSize + 1)
                .fetch();

        List<Long> questionIds = findQuestionIds(questionTags);

        List<QuestionDto> questionListDto = selectQuestionDtoInIds(questionIds);

        addQuestionTagsToQuestionListDto(questionListDto);

        questionListDto.sort(Comparator.comparing(QuestionDto::getAskedAt));

        return new QuestionListResult();
    }

    /*
        QuestionSort 에 따라 쿼리해온 데이터 정렬
        NEW_EAST: questionId 로 Order By questionId DESC 로 정렬 불필요
    */
    private void sortListByQuestionSortType(QuestionSortType sort, List<QuestionDto> questionListDto) {
        log.info("sortListByQuestionSort() sort: {}", sort);

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

    /*
     *  Question ID
     *  QuestionSortType == NEW_EAST : Question Primary key로 조회
     *  QuestionSortType != NEW_EAST : QuestionMetrics 에서 조회
     */
    private List<Long> selectQuestionIdList(Long lastOfData, Integer limitSize, QuestionSortType sort) {
        log.info("selectQuestionIdList() lastOfData: {}, limitSize: {}, sortType: {}", lastOfData, limitSize, sort);

        if (sort != NEW_EAST) {
            return findQuestionIdWithSortType(lastOfData, limitSize, sort);
        }

        return findQuestionIdById(lastOfData, limitSize);
    }

    private List<Long> findQuestionIdById(Long lastOfData, Integer limitSize) {
        log.info("selectIdFromQuestion sortBy: NEW_EAST");
        List<Long> fetch = jpaQueryFactory
                .select(question.id)
                .from(question)
                .where(lastIndexCursor(lastOfData), eqExposure(PUBLIC))
                .orderBy(question.id.desc())
                .limit(limitSize + 1)
                .fetch();
        return CollectionUtils.isNullOrEmpty(fetch) ? new ArrayList<>() : fetch;
    }

    private List<Long> findQuestionIdWithSortType(Long lastData, Integer limitSize, QuestionSortType sort) {
        log.info("selectQuestionIdWithSort sortType: {}", sort);
        final JPAQuery<Long> queryWithSort = jpaQueryFactory
                .select(questionMetrics.question.id)
                .from(questionMetrics)
                .join(questionMetrics.question, question);
        final BooleanExpression exposure = questionMetrics.exposure.eq(PUBLIC);
        final OrderSpecifier<Integer> variableSortingCriteria;
        final Predicate cursor;

        if (sort == VIEW) {
            cursor = lastViewCursor(lastData);
            variableSortingCriteria = questionMetrics.countOfView.desc();
        } else if (sort == RECOMMEND) {
            cursor = lastRecommendCursor(lastData);
            variableSortingCriteria = questionMetrics.countOfRecommend.desc();
        } else {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOT_EXIST_SORT_VALUE);
        }

        List<Long> questionIds = queryWithSort
                .where(cursor, exposure)
                .orderBy(variableSortingCriteria)
                .limit(limitSize + 1)
                .fetch();

        return CollectionUtils.isNullOrEmpty(questionIds) ? new ArrayList<>() : questionIds;
    }


    private List<QuestionDto> selectQuestionDtoInIds(List<Long> questionIds) {
        log.debug("selectQuestionDtoInIds- QuestionID: {}", questionIds);
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
                .join(member.memberProfile, memberProfile)
                .where(question.id.in(questionIds))
                .orderBy(question.id.desc())
                .fetch();
    }

    private List<QuestionTagDto> findQuestionTagDtoByQuestionIds(List<Long> ids) {
        List<Long> questionTagIds = findQuestionTagsInQuestionIds(ids);

        if (CollectionUtils.isNullOrEmpty(questionTagIds)) {
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
                                tag.id.as("hashTagId"),
                                tag.name.as("hashTagName")
                        ))
                .from(questionTag)
                .join(questionTag.tag, tag)
                .where(questionTag.id.in(questionTagIds))
                .orderBy(questionTag.id.asc())
                .fetch();
    }

    private List<Long> findQuestionTagsInQuestionIds(List<Long> questionIds) {
        log.debug("[QUERY]==> findQuestionTagsInQuestionIds QuestionID: {}", questionIds);
        return jpaQueryFactory
                .select(questionTag.id)
                .from(questionTag)
                .where(questionTag.question.id.in(questionIds))
                .fetch();
    }

    private void addQuestionTagsToQuestionListDto(List<QuestionDto> questionListDto) {

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

    private BooleanExpression eqExposure(ExposureStatus status) {
        return status == null ? question.exposure.eq(PUBLIC) : question.exposure.eq(status);
    }

    private Map<Long, List<QuestionTagDto>> groupQuestionTagsByQuestionId(List<QuestionTagDto> questionTagList) {
        return questionTagList.stream()
                .collect(Collectors.groupingBy(QuestionTagDto::getQuestionId));
    }

    private BooleanExpression lastIndexCursor(Long lastOfData) {

        long index = initializationIndex(lastOfData);

        return index <= 0 ? null : question.id.lt(lastOfData);
    }

    private Predicate lastRecommendCursor(Long lastOfData) {

        long index = initializationIndex(lastOfData);

        return index <= 0 ? questionMetrics.countOfRecommend.lt(Integer.MAX_VALUE) : questionMetrics.countOfRecommend.lt(lastOfData.intValue());
    }


    private Predicate lastViewCursor(Long lastOfData) {

        long index = initializationIndex(lastOfData);

        return index <= 0 ? questionMetrics.countOfView.lt(Integer.MAX_VALUE) : questionMetrics.countOfView.lt(lastOfData.intValue());
    }

    private long initializationIndex(Long lastOfData) {
        long index = 0L;

        if (lastOfData != null) {
            index = lastOfData <= -1 ? 0 : lastOfData;
        }
        return index;
    }


    private BooleanExpression hashTagCursor(Long questionId) {
        long index = 0L;

        if (questionId != null) {
            index = questionId;
        }
        return index <= 0 ? null : questionTag.question.id.lt(questionId);
    }

    private int initializationLimitSize(Integer limitSize) {

        final int defaultLimitSize = 10;

        return limitSize == null ? defaultLimitSize : limitSize;
    }

    private Predicate eqTagName(String hashTag) {
        return questionTag.tag.name.eq(hashTag);
    }

    private Predicate tagNamesIn(List<String> extractedTags) {
        return questionTag.tag.name.in(extractedTags);
    }

    private List<String> getExtractedTags(String searchWord) {
        List<String> collect = Arrays.stream(searchWord.split("#")).collect(Collectors.toList());
        return collect.subList(1, collect.size()).stream().map(tag -> "#" + tag.toLowerCase()).collect(Collectors.toList());
    }


}
