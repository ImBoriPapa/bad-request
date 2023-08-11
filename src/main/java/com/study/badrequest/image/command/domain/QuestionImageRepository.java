package com.study.badrequest.image.command.domain;

import com.study.badrequest.question.command.domain.Question;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionImageRepository {

    QuestionImage save(QuestionImage questionImage);

    List<QuestionImage> findAllById(Iterable<Long> ids);

    List<QuestionImage> findByQuestion(Question question);

    @Query(value = "DELETE FROM QUESTION_IMAGE WHERE QUESTION_IMAGE_ID IN (:ids)", nativeQuery = true)
    void deleteAllByQuestionImageIds(@Param("ids") List<Long> ids);
}
