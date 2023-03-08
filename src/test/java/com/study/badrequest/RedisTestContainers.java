package com.study.badrequest;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.dynamic.output.OutputType;
import io.lettuce.core.protocol.CommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import org.mockito.Captor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@DisplayName("Redis Test Containers")
@ActiveProfiles("test")
@Slf4j
public class RedisTestContainers {
    /**
     * 레디스 테스트를 컨테이너 환경에서 실행하여 운영환경과 분리 하기 위한 기능
     * 레디스를 테스트하는 곳에서 상속받아서 사용
     */
    private static final String REDIS_DOCKER_IMAGE = "redis:5.0.3-alpine";
    private static GenericContainer<?> redisContainer;

    @BeforeAll
    static void setUp() {    // (1)
        try {
            log.info("RedisTestContainers  Open");
            redisContainer = new GenericContainer<>(DockerImageName.parse(REDIS_DOCKER_IMAGE))
                    .withExposedPorts(6379)
                    .withReuse(true);

            redisContainer.start();    // (2)
            // (3)
            System.setProperty("spring.redis.host", redisContainer.getHost());
            System.setProperty("spring.redis.port", redisContainer.getMappedPort(6379).toString());
        } catch (Exception e) {
            log.info("RedisTestContainers Has Exception ={}", e);
        }
    }

    @AfterAll
    static void tearDown() {
        if (redisContainer != null)
            redisContainer.close();
        log.info("RedisTestContainers  Closed");
    }
}


