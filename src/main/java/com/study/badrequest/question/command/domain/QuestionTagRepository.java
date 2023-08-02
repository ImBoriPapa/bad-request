package com.study.badrequest.question.command.domain;

import com.study.badrequest.question.command.domain.QuestionTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionTagRepository extends JpaRepository<QuestionTag,Long> {
}
