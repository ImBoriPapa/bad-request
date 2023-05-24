package com.study.badrequest.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.utils.converter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.persistence.EntityManager;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new LogLevelConverter());
        registry.addConverter(new BindingParamToQuestionSortCriteria());
    }

}

