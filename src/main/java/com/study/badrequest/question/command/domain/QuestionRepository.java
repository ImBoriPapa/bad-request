package com.study.badrequest.question.command.domain;


import com.study.badrequest.question.command.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;



public interface QuestionRepository extends JpaRepository<Question,Long> {

}
