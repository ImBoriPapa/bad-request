package com.study.badrequest.config;

import com.study.badrequest.filter.JwtAuthenticationEntryPointFilter;
import com.study.badrequest.filter.JwtAuthenticationFilter;
import com.study.badrequest.login.domain.service.JwtUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPointFilter jwtAuthenticationEntryPointFilter;
    private final JwtUserDetailService jwtUserDetailService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security.httpBasic().disable()
                .cors().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPointFilter)
                .and()
                .userDetailsService(jwtUserDetailService)
                .authorizeRequests()
                .antMatchers("/", "/login","/log-out","/refresh","/docs/index.html").permitAll()
                .antMatchers(HttpMethod.POST,"/member").permitAll()
                .antMatchers("/static/**").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return security.build();
    }
}
