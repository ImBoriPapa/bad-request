package com.study.badrequest.commons.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.utils.converter.BindingParamToCategory;
import com.study.badrequest.utils.converter.BindingParamToTopic;
import com.study.badrequest.utils.converter.LogLevelConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.format.FormatterRegistry;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import javax.persistence.EntityManager;
import javax.sql.DataSource;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new LogLevelConverter());
        registry.addConverter(new BindingParamToCategory());
        registry.addConverter(new BindingParamToTopic());
    }
}

