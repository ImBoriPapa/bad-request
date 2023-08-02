package com.study.badrequest.repository.image;

import com.study.badrequest.image.command.domain.QuestionImage;
import com.study.badrequest.question.command.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionImageRepository extends JpaRepository<QuestionImage,Long> {
    List<QuestionImage> findByQuestion(Question question);
}
