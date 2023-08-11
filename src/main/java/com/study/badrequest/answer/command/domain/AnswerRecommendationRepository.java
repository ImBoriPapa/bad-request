package com.study.badrequest.answer.command.domain;


import java.util.Optional;

public interface AnswerRecommendationRepository  {

    AnswerRecommendation save(AnswerRecommendation answerRecommendation);

    Optional<AnswerRecommendation> findById(Long id);

    void delete(AnswerRecommendation answerRecommendation);
}
