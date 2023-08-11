package com.study.badrequest.answer.command.infra.persistence;

import com.study.badrequest.answer.command.domain.AnswerComment;
import com.study.badrequest.answer.command.domain.AnswerCommentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerCommentJpaRepository extends JpaRepository<AnswerComment,Long>, AnswerCommentRepository {
}
