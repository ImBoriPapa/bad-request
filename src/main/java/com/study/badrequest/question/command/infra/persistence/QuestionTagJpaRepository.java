package com.study.badrequest.question.command.infra.persistence;

import com.study.badrequest.question.command.domain.QuestionTag;
import com.study.badrequest.question.command.domain.QuestionTagRepository;
import org.springframework.data.jpa.repository.JpaRepository;


public interface QuestionTagJpaRepository extends JpaRepository<QuestionTag, Long>,QuestionTagCustomRepository ,QuestionTagRepository {

}
