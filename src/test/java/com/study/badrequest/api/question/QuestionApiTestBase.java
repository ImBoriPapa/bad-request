package com.study.badrequest.api.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.filter.JwtAuthenticationFilter;
import com.study.badrequest.question.command.interfaces.QuestionApiController;
import com.study.badrequest.question.query.interfaces.QuestionQueryApiController;
import com.study.badrequest.service.question.QuestionMetricsService;
import com.study.badrequest.service.question.QuestionQueryService;
import com.study.badrequest.service.question.QuestionService;
import com.study.badrequest.service.question.QuestionTagService;
import com.study.badrequest.utils.modelAssembler.QuestionModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {QuestionApiController.class, QuestionQueryApiController.class})
@Import(QuestionModelAssembler.class)
@ActiveProfiles("test")
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
public abstract class QuestionApiTestBase {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected QuestionQueryService questionQueryService;
    @MockBean
    protected QuestionService questionService;
    @MockBean
    protected QuestionTagService questionTagService;
    @MockBean
    protected QuestionMetricsService questionMetricsService;
    @MockBean
    protected JwtAuthenticationFilter jwtAuthenticationFilter;
}
