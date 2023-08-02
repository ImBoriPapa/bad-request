package com.study.badrequest.api.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.filter.JwtAuthenticationFilter;
import com.study.badrequest.member.command.application.MemberService;
import com.study.badrequest.member.command.interfaces.MemberAccountApiController;
import com.study.badrequest.utils.modelAssembler.MemberResponseModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MemberAccountApiController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public abstract class MemberAccountApiTestBase {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    protected MemberService memberService;
    @MockBean
    protected MemberResponseModelAssembler memberResponseModelAssembler;

}
