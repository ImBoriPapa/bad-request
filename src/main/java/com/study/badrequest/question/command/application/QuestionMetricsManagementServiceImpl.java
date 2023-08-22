package com.study.badrequest.question.command.application;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.question.command.domain.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionMetricsManagementServiceImpl {
    private final QuestionRepository questionRepository;
    private final RedissonClient redissonClient;

    public void incrementRecommend(Long questionId, String taskName) {
        String uniqueRockId = UUID.randomUUID().toString();

        RLock redissonClientLock = redissonClient.getLock("increment:question_recommend:" + uniqueRockId);

        try {
            boolean available = redissonClientLock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!available) {
                throw new IllegalArgumentException("Time Out");
            }

            this.incrementRecommendQuestion(questionId);

        } catch (InterruptedException e) {

            throw new IllegalArgumentException("");
        } finally {

            if (redissonClientLock.isHeldByCurrentThread()) {
                redissonClientLock.unlock();
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long incrementRecommendQuestion(Long questionId) {
        Question question = getQuestionByQuestionId(questionId);
        question.incrementCountOfRecommendation();
        return question.getId();
    }

    public Long decrementRecommendation(Long questionId) {
        Question question = getQuestionByQuestionId(questionId);
        question.decrementCountOfRecommendation();
        return question.getId();
    }

    public Long incrementUnRecommendQuestion(Long questionId) {
        Question question = getQuestionByQuestionId(questionId);
        return question.getId();
    }

    public Long decrementUnRecommendQuestion(Long questionId) {
        Question question = getQuestionByQuestionId(questionId);
        return question.getId();
    }

    private Question getQuestionByQuestionId(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOT_FOUND_QUESTION));
    }
}
