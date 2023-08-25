package com.study.badrequest.question.command.domain;


import java.util.List;
import java.util.Optional;

public interface QuestionTagRepository  {

    QuestionTag save(QuestionTag questionTag);

    Optional<QuestionTag> findById(Long id);

    void delete(QuestionTag questionTag);
    List<QuestionTag> saveAllQuestionTag(List<QuestionTag> questionTags);


}
