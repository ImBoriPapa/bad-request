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


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    public Optional<QuestionDetail> findQuestionDetail(Long questionId, Long memberId, ExposureStatus exposureStatus) {

        Optional<QuestionDetail> detailOptional = getQuestionDetailByQuestionIdAndExposureStatus(questionId, exposureStatus);

        detailOptional.ifPresent(detail -> {
            addingTags(detail);
            checkIsQuestioner(memberId, detail);
            setRecommendation(detail);
        });

        return detailOptional;
    }

    private void setRecommendation(QuestionDetail detail) {
        Optional<Recommendation> optional = findRecommendationByQuestionId(detail.getId());

        if (optional.isPresent()) {
            Recommendation recommendation = optional.get();
            detail.getMetrics().setHasRecommendationAndKind(true, recommendation.getKind());
        }

    }

    private Optional<Recommendation> findRecommendationByQuestionId(Long questionId) {
        return jpaQueryFactory
                .selectDistinct(recommendation)
                .from(recommendation)
                .join(recommendation.member, member).fetchJoin()
                .where(recommendation.question.id.eq(questionId))
                .fetch()
                .stream()
                .findFirst();
    }

    private void checkIsQuestioner(Long memberId, QuestionDetail questionDetail) {
        if (memberId != null) {
            if (questionDetail.getId().equals(memberId)) {
                questionDetail.isQuestionerToTrue();
            }
        }
    }

    private void addingTags(QuestionDetail questionDetail) {
        List<QuestionTag> questionTags = getQuestionTagsByQuestionId(questionDetail);

        List<HashTagDto> hashTagDtos = questionTags.stream().map(tag -> new HashTagDto(tag.getId(), tag.getHashTag().getHashTagName())).collect(Collectors.toList());

        questionDetail.addHashTag(hashTagDtos);
    }

    private List<QuestionTag> getQuestionTagsByQuestionId(QuestionDetail detailOptional) {
        return jpaQueryFactory
                .selectDistinct(questionTag)
                .from(questionTag)
                .join(questionTag.hashTag, hashTag)
                .fetchJoin()
                .where(questionTag.question.id.eq(detailOptional.getId()))
                .fetch();
    }

    private Optional<QuestionDetail> getQuestionDetailByQuestionIdAndExposureStatus(Long questionId, ExposureStatus exposureStatus) {
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

    @Override
    public QuestionListResult findQuestionListByCondition(QuestionSearchCondition condition) {

        final int limitSize = setLimitSize(condition.getSize());

        List<Long> questionIdList = selectQuestionIdList(condition.getLastOfIndex(), condition.getLastOfView(), condition.getLastOfRecommend(), limitSize, PUBLIC, condition.getSort());

        List<QuestionDto> questionListDto = selectQuestionFieldsByIdListAsQuestionDto(questionIdList);

        sortListByQuestionSort(condition.getSort(), questionListDto);

        addQuestionTagToListDto(questionListDto);

        int resultSize = questionListDto.size();

        boolean hasNext = resultSize > limitSize;

        if (hasNext) {
            questionListDto.remove(limitSize);
            --resultSize;
        }

        Long lastIndex = 0L;
        Integer lastView = 0;
        Integer lastRecommend = 0;

        QuestionSort sortBy = condition.getSort() == null ? QuestionSort.NEW_EAST : condition.getSort();
        switch (sortBy) {
            case NEW_EAST:
                lastIndex = questionListDto.stream()
                        .mapToLong(QuestionDto::getId)
                        .min()
                        .orElse(0);
                lastView = null;
                lastRecommend = null;
                break;
            case VIEW:
                lastView = questionListDto.stream()
                        .mapToInt(q -> q.getMetrics().getCountOfView())
                        .min()
                        .orElse(0);
                lastIndex = null;
                lastRecommend = null;
                break;
            case RECOMMEND:
                lastRecommend = questionListDto.stream()
                        .mapToInt(q -> q.getMetrics().getCountOfRecommend())
                        .min()
                        .orElse(0);
                lastIndex = null;
                lastView = null;
                break;
        }

        return new QuestionListResult(resultSize, hasNext, sortBy, lastIndex, lastView, lastRecommend, questionListDto);

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

        List<QuestionDto> questionListDto = selectQuestionFieldsByIdListAsQuestionDto(questionIds);

        addQuestionTagToListDto(questionListDto);

        questionListDto.sort(Comparator.comparing(QuestionDto::getAskedAt));

        return new QuestionListResult();
    }

    /*
        QuestionSort 에 따라 쿼리해온 데이터 정렬
        NEW_EAST: questionId 로 Order By questionId DESC 로 정렬 불필요
    */
    private void sortListByQuestionSort(QuestionSort sort, List<QuestionDto> questionListDto) {
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
     * @param lastIndex      :  조회 시작 위치
     * @param limitSize      :  조회 데이터 크기
     * @param exposureStatus : 노출 설정
     * @param sort
     * @return
     */
    private List<Long> selectQuestionIdList(Long lastIndex, Integer lastOfView, Integer lastOfRecommend, Integer limitSize, ExposureStatus exposureStatus, QuestionSort sort) {

        JPAQuery<Long> query = jpaQueryFactory.select(question.id).from(question);
        JPAQuery<Long> queryWithSort = jpaQueryFactory.select(questionMetrics.id).from(questionMetrics);

        if (sort == null) {
            query.where(lastIndexCursor(lastIndex))
                    .orderBy(question.id.desc())
                    .limit(limitSize + 1).fetch();

        }
        if (sort != null) {

            List<Long> metricsIds;


            switch (sort) {
                case VIEW:

                    metricsIds = queryWithSort.where(
                                    lastViewCursor(lastOfView),
                                    questionMetrics.exposure.eq(PUBLIC))
                            .orderBy(questionMetrics.countOfView.desc())
                            .limit(limitSize + 1)
                            .fetch();

                    query.where(question.id.in(CollectionUtils.isNullOrEmpty(metricsIds) ? new ArrayList<>() : metricsIds));
                    break;
                case RECOMMEND:

                    metricsIds = queryWithSort.where(
                                    lastRecommendCursor(lastOfRecommend),
                                    questionMetrics.exposure.eq(PUBLIC))
                            .orderBy(questionMetrics.countOfRecommend.desc())
                            .limit(limitSize + 1)
                            .fetch();

                    query.where(question.id.in(CollectionUtils.isNullOrEmpty(metricsIds) ? new ArrayList<>() : metricsIds));
                    break;
            }
        }

        List<Long> results = query.fetch();

        return CollectionUtils.isNullOrEmpty(results) ? new ArrayList<>() : results;
    }

    private Predicate lastRecommendCursor(Integer lastOfRecommend) {
        return lastOfRecommend == null ? questionMetrics.countOfRecommend.lt(Integer.MAX_VALUE) : questionMetrics.countOfRecommend.lt(lastOfRecommend);
    }


    private Predicate lastViewCursor(Integer lastOfView) {
        return lastOfView == null ? questionMetrics.countOfView.lt(Integer.MAX_VALUE) : questionMetrics.countOfView.lt(lastOfView);
    }

    private List<QuestionDto> selectQuestionFieldsByIdListAsQuestionDto(List<Long> questionIds) {
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

    private List<QuestionTagDto> findQuestionTag(List<Long> ids) {
        /**
         *1,SIMPLE,questionta0_,range,"PRIMARY,FKkt90ri7g7j1a9dd4ol9gns2ek",PRIMARY,8,null,10,Using where
         * 1,SIMPLE,hashtag1_,eq_ref,PRIMARY,PRIMARY,8,bad_request.questionta0_.hashtag_id,1,Using index
         */
        List<Long> questionTagIds = jpaQueryFactory
                .select(questionTag.id)
                .from(questionTag)
                .where(questionTag.question.id.in(ids))
                .fetch();

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

    private void addQuestionTagToListDto(List<QuestionDto> questionListDto) {

        List<Long> questionsIds = getQuestionsIds(questionListDto);

        List<QuestionTagDto> questionTagDtos = findQuestionTag(questionsIds);

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
                    dto -> dto.addHashTag(
                            questionTagMap.get(dto.getId())
                                    .stream()
                                    .map(t -> new HashTagDto(t.getHashTagId(), t.getHashTagName()))
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
