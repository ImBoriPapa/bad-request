package com.study.badrequest.domain.log.repositoey.query;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.domain.log.entity.LogLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.study.badrequest.domain.log.entity.QLog.*;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LogQueryRepositoryImpl {

    private final JPAQueryFactory jpaQueryFactory;

    public List<LogDto> findAllLog(int size, String localDateTime, LogLevel logLevel, String clientIp, String username) {

        return jpaQueryFactory
                .select(Projections.fields(LogDto.class,
                        log.id.as("id"),
                        log.logTime.as("logTime"),
                        log.logLevel.as("logLevel"),
                        log.className.as("className"),
                        log.methodName.as("methodName"),
                        log.message.as("message"),
                        log.clientIp.as("clientIp"),
                        log.requestURI.as("requestURI"),
                        log.username.as("username"),
                        log.stackTrace.as("stackTrace")
                ))
                .from(log)
                .where()
                .orderBy(log.id.desc())
                .limit(size)
                .fetch();
    }
}
