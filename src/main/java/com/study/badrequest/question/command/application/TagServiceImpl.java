package com.study.badrequest.question.command.application;

import com.study.badrequest.question.command.domain.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TagServiceImpl {

    private final TagRepository tagRepository;
    private final RedissonClient redissonClient;
    public void incrementUsage(Long tagId){

        RLock lock = redissonClient.getLock(String.format("event:tag:%d", tagId));

    }

}
