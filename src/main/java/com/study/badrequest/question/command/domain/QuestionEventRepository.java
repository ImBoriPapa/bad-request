package com.study.badrequest.question.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionEventRepository extends JpaRepository<QuestionEvent,Long> {
}
