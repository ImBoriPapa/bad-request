package com.study.badrequest.question.command.domain.repository;


import com.study.badrequest.question.command.domain.model.Question;

import java.util.Optional;

public interface QuestionRepository  {

    Question save(Question question);

    Optional<Question> findById(Long questionId);


}
