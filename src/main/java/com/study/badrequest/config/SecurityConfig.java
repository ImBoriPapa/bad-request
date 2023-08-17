package com.study.badrequest.config;

import com.study.badrequest.member.command.domain.Authority;
import com.study.badrequest.filter.JwtAccessDeniedFilter;
import com.study.badrequest.filter.JwtAuthenticationEntryPointFilter;
import com.study.badrequest.filter.JwtAuthenticationFilter;
import com.study.badrequest.handler.Oauth2AuthenticationFailHandler;
import com.study.badrequest.handler.Oauth2AuthenticationSuccessHandler;
import com.study.badrequest.member.command.domain.CustomAuthorizationRequestRepository;
import com.study.badrequest.member.command.application.JwtUserDetailService;
import com.study.badrequest.member.command.application.OAuthUserDetailService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


import static com.study.badrequest.common.constants.ApiURL.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPointFilter jwtAuthenticationEntryPointFilter;
    private final JwtUserDetailService jwtUserDetailService;
    private final JwtAccessDeniedFilter accessDeniedFilter;
    private final OAuthUserDetailService oauthUserDetailService;
    private final Oauth2AuthenticationFailHandler oauth2AuthenticationFailHandler;
    private final Oauth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;
    private final CustomAuthorizationRequestRepository authorizationRequestRepository;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security
                .httpBasic()
                .disable()

                .csrf().disable()

                .formLogin().disable()

                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPointFilter)
                .accessDeniedHandler(accessDeniedFilter)

                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(jwtUserDetailService)

                .authorizeRequests()
                //REST Docs
                .antMatchers("/docs/**").permitAll()
                //login
                .antMatchers("/", EMAIL_LOGIN_URL, LOGOUT_URL, TOKEN_REISSUE_URL, ONE_TIME_CODE_LOGIN)
                .permitAll()
                //oauth2
                .antMatchers(HttpMethod.GET, "/oauth", "/api/v2/oauth/authorization/*", "/api/v2/oauth/client/*").permitAll()
                //members
                .antMatchers(HttpMethod.POST, POST_MEMBER_URL, POST_MEMBER_TEMPORARY_PASSWORD_ISSUE_URL, POST_MEMBER_SEND_EMAIL_AUTHENTICATION_CODE).permitAll()
                .antMatchers(HttpMethod.GET, GET_MEMBER_PROFILE).permitAll()
                //Question
                .antMatchers(HttpMethod.GET, "/api/v2/questions", "/api/v2/questions/hashTags", "/api/v2/questions/{questionId}")
                .permitAll()
                //Recommendation
                .antMatchers("/api/v2/questions/{questionId}/recommendations")
                .permitAll()
                //Answer
                .antMatchers(HttpMethod.GET, "/api/v2/question/{questionId}/answers")
                .permitAll()

                //image
                .antMatchers("/api/v1/image/board-image")
                .permitAll()
                //admin
                .antMatchers("/api/v2/admin/**").hasAuthority(Authority.ADMIN.name())

                //static
                .antMatchers("/static/**", "/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico")
                .permitAll()

                .antMatchers("/test")
                .permitAll()

                //test
                .antMatchers("/testing")
                .permitAll()

                .anyRequest().authenticated()
                //oauth2
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri(OAUTH2_LOGIN_URL)
                .authorizationRequestRepository(authorizationRequestRepository)
                .and()
                .redirectionEndpoint()
                .baseUri(OAUTH2_REDIRECT_URL)
                .and()
                .userInfoEndpoint()
                .userService(oauthUserDetailService)
                .and()
                .successHandler(oauth2AuthenticationSuccessHandler)
                .failureHandler(oauth2AuthenticationFailHandler);


        return security.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader(HttpHeaders.AUTHORIZATION);//긴급 배포
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


}
