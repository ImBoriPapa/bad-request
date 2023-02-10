package com.study.badrequest.config;

import com.study.badrequest.filter.JwtAccessDeniedFilter;
import com.study.badrequest.filter.JwtAuthenticationEntryPointFilter;
import com.study.badrequest.filter.JwtAuthenticationFilter;
import com.study.badrequest.domain.login.service.JwtUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static com.study.badrequest.health.ValueController.VALUES;

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
                .cors()
                .and()
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
                .antMatchers("/", "/api/v1/login", "/api/v1/log-out", "/api/v1/refresh", "/docs/index.html", "/api/v1/member/email")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/member")
                .permitAll()
                //비회원 Board 허용
                .antMatchers("/api/v1/board", "/api/v1/board/*")
                .permitAll()
                //comment
                .antMatchers("/api/v1/board/{boardId}/comments")
                .permitAll()
                //values
                .antMatchers(VALUES + "/*")
                .permitAll()
                //dashboard
                .antMatchers("/log", "/log-ex", "/dashboard", "/dashboard/**", "/heap", "/refresh")
                .permitAll()
                //static
                .antMatchers("/static/**", "/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico")
                .permitAll()
                //test
                .antMatchers("/test/teacher")
                .hasAuthority("ROLL_TEACHER")
                .antMatchers("/api/image", "/test","/graph","/sse")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return security.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
