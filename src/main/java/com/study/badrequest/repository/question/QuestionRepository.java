package com.study.badrequest.repository.question;


import com.study.badrequest.domain.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;



public interface QuestionRepository extends JpaRepository<Question,Long> {

}
