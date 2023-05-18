package com.study.badrequest.repository.questionComment;

import com.study.badrequest.domain.questionComment.QuestionComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionCommentRepository extends JpaRepository<QuestionComment,Long> {
}
