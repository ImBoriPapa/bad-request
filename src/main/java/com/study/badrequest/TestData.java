package com.study.badrequest;

import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.question.command.domain.QuestionRepository;
import com.study.badrequest.question.command.domain.QuestionStatus;
import com.study.badrequest.question.command.infra.redis.QuestionRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class TestData {
    private final QuestionRepository questionRepository;
    public void init() {
        data();
    }

    @Transactional
    public void data() {
        log.info("INIT DATA");
        IntStream.rangeClosed(1, 50000).forEach(i -> {
            String title = "title" + i;
            String contents = "contents" + i;
            List<QuestionStatus> asList = Arrays.asList(QuestionStatus.POSTED, QuestionStatus.DELETE);

            Question question = Question.builder()
                    .title(title)
                    .contents(contents)
                    .status(asList.get(new Random().nextInt(2)))
                    .countOfAnswer(new Random().nextInt(1, 1000))
                    .countOfView(new Random().nextInt(1, 1000))
                    .countOfRecommend(new Random().nextInt(1, 1000))
                    .countOfUnRecommend(new Random().nextInt(1, 1000))
                    .askedAt(LocalDateTime.now())
                    .build();
            questionRepository.save(question);
        });
        log.info("INIT DATA FINISH");
    }
}
