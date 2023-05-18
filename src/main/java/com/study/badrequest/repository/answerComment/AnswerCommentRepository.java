package com.study.badrequest.repository.answerComment;

import com.study.badrequest.domain.answerComment.AnswerComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerCommentRepository extends JpaRepository<AnswerComment,Long> {
}
