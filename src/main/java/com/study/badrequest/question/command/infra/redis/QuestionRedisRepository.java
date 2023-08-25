package com.study.badrequest.question.command.infra.redis;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionRedisRepository {

    private final RedissonClient redissonClient;

    public void save(){
    }
}
