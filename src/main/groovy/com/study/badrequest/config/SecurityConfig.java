package com.study.badrequest.config;

import com.study.badrequest.filter.JwtAuthenticationEntryPointFilter;
import com.study.badrequest.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPointFilter jwtAuthenticationEntryPointFilter;

    @Bean
    public SecurityFilterChain httpSecurity(HttpSecurity security) throws Exception {
        security
                .httpBasic().disable()
                .cors().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPointFilter)
                .and()
                .authorizeRequests()
                .antMatchers("/", "/login").permitAll()
                .antMatchers(HttpMethod.POST, "/member").permitAll()
                .anyRequest()
                .authenticated();

        return security.build();
    }
}
