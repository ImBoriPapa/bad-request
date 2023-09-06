package com.study.badrequest.testHelper;


import com.study.badrequest.member.command.domain.values.Authority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;



import static com.study.badrequest.utils.authentication.AuthenticationFactory.generateAuthentication;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        String memberId = annotation.memberId();
        Authority authority = annotation.authority();
        Authentication authentication = generateAuthentication(Long.valueOf(memberId),authority);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
