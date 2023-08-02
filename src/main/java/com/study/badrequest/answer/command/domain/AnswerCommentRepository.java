package com.study.badrequest.answer.command.domain;

import com.study.badrequest.answer.command.domain.AnswerComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerCommentRepository extends JpaRepository<AnswerComment,Long> {
}
