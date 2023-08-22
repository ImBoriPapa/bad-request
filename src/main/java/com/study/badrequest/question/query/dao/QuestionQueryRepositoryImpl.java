package com.study.badrequest.question.query.dao;

import com.amazonaws.util.CollectionUtils;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;

import com.querydsl.jpa.impl.JPAQueryFactory;


import com.study.badrequest.common.status.ExposureStatus;

import com.study.badrequest.question.query.dto.*;

import com.study.badrequest.recommandation.command.domain.QuestionRecommendation;


import com.study.badrequest.question.command.domain.QuestionSortType;
import com.study.badrequest.question.command.domain.QuestionTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;


import static com.study.badrequest.question.command.domain.QuestionSortType.*;


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
        Optional<QuestionRecommendation> optional = findRecommendationByQuestionId(detail.getId());

        if (optional.isPresent()) {
            QuestionRecommendation questionRecommendation = optional.get();
            detail.getMetrics().setHasRecommendationAndKind(true, questionRecommendation.getKind());
        }

    }

    private List<TagDto> findTagsDtoByQuestionId(Long questionId) {
        log.debug("[QUERY]==> findTagsDtoByQuestionId- QuestionID: {}", questionId);
        return findQuestionTagsByQuestionId(questionId)
                .stream()
                .map(tag -> new TagDto(tag.getId(), tag.getTag().getName()))
                .collect(Collectors.toList());
    }

    private Optional<QuestionRecommendation> findRecommendationByQuestionId(Long questionId) {
        log.debug("[QUERY]==> findRecommendationByQuestionId- QuestionID: {}", questionId);

        return null;
    }


    private List<QuestionTag> findQuestionTagsByQuestionId(Long questionId) {
        log.debug("[QUERY]==> findQuestionTagsByQuestionId- QuestionID: {}", questionId);
        return null;
    }

    private Optional<QuestionDetail> getQuestionDetailByQuestionIdAndExposureStatus(Long questionId, ExposureStatus exposureStatus) {
        log.debug("[QUERY]==> getQuestionDetailByQuestionIdAndExposureStatus- QuestionID: {}, ExposureStatus: {}", questionId, exposureStatus);
        return null;
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

        return null;
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
        return null;
    }

    private List<Long> findQuestionIdWithSortType(Long lastData, Integer limitSize, QuestionSortType sort) {
        return null;
    }


    private List<QuestionDto> selectQuestionDtoInIds(List<Long> questionIds) {
        log.debug("selectQuestionDtoInIds- QuestionID: {}", questionIds);
        return null;
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
        return null;
    }

    private List<Long> findQuestionTagsInQuestionIds(List<Long> questionIds) {
        log.debug("[QUERY]==> findQuestionTagsInQuestionIds QuestionID: {}", questionIds);
        return null;
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
        return null;
    }

    private Map<Long, List<QuestionTagDto>> groupQuestionTagsByQuestionId(List<QuestionTagDto> questionTagList) {
        return questionTagList.stream()
                .collect(Collectors.groupingBy(QuestionTagDto::getQuestionId));
    }

    private BooleanExpression lastIndexCursor(Long lastOfData) {

        long index = initializationIndex(lastOfData);

        return null;
    }

    private Predicate lastRecommendCursor(Long lastOfData) {

        long index = initializationIndex(lastOfData);

        return null;
    }


    private Predicate lastViewCursor(Long lastOfData) {

        long index = initializationIndex(lastOfData);

        return null;
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
        return null;
    }

    private int initializationLimitSize(Integer limitSize) {

        final int defaultLimitSize = 10;

        return limitSize == null ? defaultLimitSize : limitSize;
    }

    private Predicate eqTagName(String hashTag) {
        return null;
    }

    private Predicate tagNamesIn(List<String> extractedTags) {
        return null;
    }

    private List<String> getExtractedTags(String searchWord) {
        List<String> collect = Arrays.stream(searchWord.split("#")).collect(Collectors.toList());
        return collect.subList(1, collect.size()).stream().map(tag -> "#" + tag.toLowerCase()).collect(Collectors.toList());
    }


}
