package com.study.badrequest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain httpSecurity(HttpSecurity security) throws Exception {

        security.formLogin().disable();
        security.cors().disable();
        security.authorizeRequests().antMatchers("/").permitAll();

        return security.build();
    }
}
