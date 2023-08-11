package com.study.badrequest.question.command.domain;


import java.util.Optional;

public interface QuestionRepository  {

    Question save(Question question);

    Optional<Question> findById(Long questionId);


}
