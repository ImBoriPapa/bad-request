package com.study.badrequest.testHelper;

import com.study.badrequest.domain.member.Authority;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithCustomMockUser {
    String username() default "username";
    String memberId() default "1";
    Authority authority() default Authority.MEMBER;
}
