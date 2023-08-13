package com.study.badrequest.question.command.application;

import com.study.badrequest.hashtag.command.domain.Tag;
import com.study.badrequest.hashtag.command.domain.TagRepository;
import com.study.badrequest.question.command.domain.QuestionEvent;
import com.study.badrequest.question.command.domain.QuestionEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuestionEventHandler {

    private final RedissonClient redissonClient;
    private final TagRepository tagRepository;
    private final QuestionEventRepository questionEventRepository;

    public void handle(QuestionEvent questionEvent) {

        questionEventRepository.save(questionEvent);

        List<QuestionEvent> events = questionEventRepository.findAll();


        for (QuestionEvent event : events) {
            RLock lock = redissonClient.getLock(String.format("event:question:%d", event.getId()));

            List<Long> ids = event.getTags().stream().map(Tag::getId).collect(Collectors.toList());

            List<Tag> tags1 = tagRepository.findAllById(ids);

            try {
                boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

                if (!available) {
                    log.info("redisson getLock timeout");
                }

                tags1.forEach(Tag::incrementUsage);

            } catch (InterruptedException e) {
                throw new RuntimeException();
            } finally {
                lock.unlock();
            }


        }
    }
}
