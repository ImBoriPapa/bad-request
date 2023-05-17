package com.study.badrequest.config;

import com.study.badrequest.filter.JwtAccessDeniedFilter;
import com.study.badrequest.filter.JwtAuthenticationEntryPointFilter;
import com.study.badrequest.filter.JwtAuthenticationFilter;
import com.study.badrequest.handler.Oauth2AuthenticationSuccessHandler;
import com.study.badrequest.repository.login.CustomAuthorizationRequestRepository;
import com.study.badrequest.service.login.JwtUserDetailService;
import com.study.badrequest.service.login.OauthUserDetailService;
import com.study.badrequest.utils.authentication.Oauth2AuthenticationFailHandler;
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

import static com.study.badrequest.api.admin.ValueController.VALUES;
import static com.study.badrequest.commons.constants.ApiURL.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPointFilter jwtAuthenticationEntryPointFilter;
    private final JwtUserDetailService jwtUserDetailService;
    private final JwtAccessDeniedFilter accessDeniedFilter;
    private final OauthUserDetailService oauthUserDetailService;
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
                .antMatchers("/docs/index.html").permitAll()
                //login
                .antMatchers("/", EMAIL_LOGIN_URL, LOGOUT_URL, TOKEN_REISSUE_URL,ONE_TIME_CODE_LOGIN)
                .permitAll()
                //login by one time code
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
                //비회원 Board 읽기 허용
                .antMatchers(HttpMethod.GET, BOARD_LIST_URL, BOARD_DETAIL_URL)
                .permitAll()
                //BoardImage
                .antMatchers("/api/v1/image/board")
                .permitAll()
                //comment
                .antMatchers("/api/v1/board/{boardId}/comments")
                .permitAll()
                //subComment
                .antMatchers("/api/v1/comments/{commentId}/sub-comments")
                .permitAll()
                //image
                .antMatchers("/api/v1/image/board-image")
                .permitAll()
                //values
                .antMatchers(VALUES + "/*")
                .permitAll()
                //dashboard
                .antMatchers("/log", "/log-ex", "/dashboard", "/dashboard/**", "/heap", "/refresh", "/api/v1/dashboard/*")
                .permitAll()

                //static
                .antMatchers("/static/**", "/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico")
                .permitAll()

                .antMatchers("/test")
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
                .failureHandler(oauth2AuthenticationFailHandler)
                .and()
                .logout()
                .logoutUrl(LOGOUT_URL)
                .deleteCookies("JSESSIONID")
                .deleteCookies("Refresh")
                .permitAll();


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
