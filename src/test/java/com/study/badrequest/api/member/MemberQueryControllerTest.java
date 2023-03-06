package com.study.badrequest.api.member;

import com.study.badrequest.base.BaseMemberTest;
import com.study.badrequest.domain.login.dto.LoginResponse;
import com.study.badrequest.domain.login.service.JwtLoginService;
import com.study.badrequest.domain.member.dto.MemberSearchCondition;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberQueryRepository;
import com.study.badrequest.domain.member.repository.MemberQueryRepositoryImpl;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.member.repository.query.MemberListDto;
import com.study.badrequest.utils.modelAssembler.MemberResponseModelAssembler;
import com.study.badrequest.utils.validator.MemberValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;

import java.util.List;

import static com.study.badrequest.SampleUserData.SAMPLE_PASSWORD;
import static com.study.badrequest.SampleUserData.SAMPLE_USER_EMAIL;
import static com.study.badrequest.commons.consts.JwtTokenHeader.AUTHORIZATION_HEADER;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static reactor.core.publisher.Mono.when;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Slf4j
class MemberQueryControllerTest extends BaseMemberTest {
    @Autowired
    JwtLoginService loginService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberValidator memberValidator;
    @Autowired
    MockMvc mockMvc;
    private Long testId;

    @BeforeEach
    void before() {
        Member member = Member.createMember()
                .email("email@email.com")
                .password(passwordEncoder.encode("password1234!@"))
                .nickname("nickname")
                .contact("010-1011-2131")
                .authority(Authority.MEMBER)
                .build();
        Member save = memberRepository.save(member);
        this.testId = save.getId();
    }

    @Test
    @DisplayName("회원 상세정보 조회")
    void 회원_상세정보_조회() throws Exception {
        //given
        Member member = memberRepository.findById(testId).get();

        LoginResponse.LoginDto loginDto = loginService.loginProcessing(member.getEmail(), "password1234!@");
        //when

        //then
        mockMvc.perform(get("/api/v1/members/{memberId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginDto.getAccessToken())
                )
                .andDo(print());
    }
}