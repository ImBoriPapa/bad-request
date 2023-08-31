package com.study.badrequest.question.query.dao;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.question.command.domain.values.QuestionStatus;
import com.study.badrequest.question.command.infra.persistence.question.QQuestionEntity;
import com.study.badrequest.question.command.infra.persistence.question.QuestionEntity;
import com.study.badrequest.question.command.infra.persistence.questionTag.QQuestionTagEntity;
import com.study.badrequest.question.command.infra.persistence.tag.QTagEntity;
import com.study.badrequest.question.command.infra.persistence.writer.QWriterEntity;
import com.study.badrequest.question.query.dto.QuestionDto;
import com.study.badrequest.question.query.dto.QuestionListResult;
import com.study.badrequest.question.query.dto.QuestionSearchCondition;
import com.study.badrequest.question.query.dto.TagDto;
import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.study.badrequest.question.command.domain.values.QuestionStatus.*;
import static com.study.badrequest.question.command.infra.persistence.question.QQuestionEntity.questionEntity;
import static com.study.badrequest.question.command.infra.persistence.questionTag.QQuestionTagEntity.questionTagEntity;
import static com.study.badrequest.question.command.infra.persistence.tag.QTagEntity.tagEntity;
import static com.study.badrequest.question.command.infra.persistence.writer.QWriterEntity.*;


@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestionQuery {
    private final JPAQueryFactory jpaQueryFactory;

    public QuestionListResult findQuestionsWithCondition(int offSet, int limit, Pageable pageable) {
        PageRequest.of(offSet, limit, Sort.Direction.DESC, "question_id");
        List<QuestionDto> fetch = jpaQueryFactory
                .select(
                        Projections.fields(QuestionDto.class,
                                questionEntity.id.as("id"),
                                questionEntity.id.as("title"),
                                questionEntity.id.as("preview"),
                                questionEntity.id.as("metrics"),
                                questionEntity.id.as("id")
                        )
                )
                .from(questionEntity)
                .innerJoin(questionEntity.writer, writerEntity)
                .where(questionEntity.questionStatus.eq(POSTED))
                .orderBy(questionEntity.id.desc())
                .limit(limit)
                .offset(offSet)
                .fetch();

        List<Long> ids = fetch.stream().map(QuestionDto::getId).toList();

        List<TagDto> tagDtos = jpaQueryFactory
                .select(
                        Projections.fields(TagDto.class,
                                questionTagEntity.id.as("id"),
                                tagEntity.name.as("tagName")
                        )
                )
                .from(questionTagEntity)
                .innerJoin(questionTagEntity.tag, tagEntity)
                .where(questionTagEntity.question.id.in(ids))
                .fetch();

        Long count = jpaQueryFactory.select(questionEntity.count())
                .from(questionEntity)
                .where(questionEntity.questionStatus.eq(POSTED))
                .fetchOne();

        PageImpl<QuestionDto> page = new PageImpl<>(fetch, pageable, count);
        Pageable pagePageable = page.getPageable();
        pagePageable.hasPrevious();
        pagePageable.getPageNumber();
        pagePageable.getSort();
        pagePageable.getOffset();
        pagePageable.first();
        page.hasNext();
        long totalElements = page.getTotalElements();
        boolean last = page.isLast();
        int totalPages = page.getTotalPages();
        QuestionListResult questionListResult = new QuestionListResult();


        return questionListResult;
    }
}
