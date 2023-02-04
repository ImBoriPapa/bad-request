package com.study.badrequest.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.utils.converter.BindingParamToCategory;
import com.study.badrequest.utils.converter.BindingParamToTopic;
import com.study.badrequest.utils.converter.LogLevelConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import javax.persistence.EntityManager;

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

