package com.study.badrequest.config;

import com.study.badrequest.filter.JwtAccessDeniedFilter;
import com.study.badrequest.filter.JwtAuthenticationEntryPointFilter;
import com.study.badrequest.filter.JwtAuthenticationFilter;
import com.study.badrequest.domain.login.domain.service.JwtUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.study.badrequest.health.ValueController.CUSTOM_STATUS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPointFilter jwtAuthenticationEntryPointFilter;
    private final JwtUserDetailService jwtUserDetailService;
    private final JwtAccessDeniedFilter accessDeniedFilter;


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
                .accessDeniedHandler(accessDeniedFilter)
                .and()
                .userDetailsService(jwtUserDetailService)
                .authorizeRequests()
                .antMatchers("/", "/api/v1/login", "/api/v1/log-out", "/api/v1/refresh", "/docs/index.html", CUSTOM_STATUS).permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/member").permitAll()
                .antMatchers("/static/**","/log","/log-ex","/dashboard","/heap").permitAll()
                .antMatchers("/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico").permitAll()
                .antMatchers("/test/teacher").hasAuthority("ROLL_TEACHER")
                .antMatchers("/api/image").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return security.build();
    }
}
