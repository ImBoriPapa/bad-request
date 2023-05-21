package com.study.badrequest.api_docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.api.question.QuestionApiController;
import com.study.badrequest.api.question.QuestionQueryApiController;
import com.study.badrequest.domain.question.QuestionSort;
import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.filter.JwtAuthenticationFilter;
import com.study.badrequest.repository.question.query.HashTagDto;
import com.study.badrequest.repository.question.query.QuestionDto;
import com.study.badrequest.repository.question.query.QuestionListResult;
import com.study.badrequest.repository.question.query.QuestionQueryRepository;
import com.study.badrequest.service.question.QuestionMetricsService;
import com.study.badrequest.service.question.QuestionQueryService;
import com.study.badrequest.service.question.QuestionService;
import com.study.badrequest.service.question.QuestionTagService;
import com.study.badrequest.testHelper.WithCustomMockUser;
import com.study.badrequest.utils.modelAssembler.QuestionModelAssembler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.study.badrequest.commons.constants.ApiURL.QUESTION_PATCH_URL;
import static com.study.badrequest.commons.constants.JwtTokenHeader.ACCESS_TOKEN_PREFIX;
import static com.study.badrequest.commons.constants.JwtTokenHeader.AUTHORIZATION_HEADER;
import static com.study.badrequest.domain.member.Authority.*;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentRequest;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static com.study.badrequest.commons.constants.ApiURL.QUESTION_BASE_URL;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;


import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest(controllers = {QuestionQueryApiController.class, QuestionApiController.class})
@Import(QuestionModelAssembler.class)
@ActiveProfiles("test")
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
public class QuestionApiDocs {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private QuestionQueryService questionQueryService;
    @MockBean
    private QuestionService questionService;
    @MockBean
    private QuestionTagService questionTagService;
    @MockBean
    private QuestionMetricsService questionMetricsService;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("질문 생성")
    @WithCustomMockUser(memberId = "321312", authority = MEMBER)
    void 질문생성() throws Exception {
        //given
        Long memberId = 321312L;
        Long questionId = 424211L;
        String title = "제목입니다.";
        String markdownContents = "내용입니다. ----- * 내용은 * 마크다운형식입니다.";
        List<String> tags = List.of("Java", "Spring");
        List<Long> imageIds = List.of(53L, 34L, 53L);
        String accessToken = UUID.randomUUID().toString();
        QuestionRequest.Create create = new QuestionRequest.Create(title, markdownContents, tags, imageIds);
        QuestionResponse.Create response = new QuestionResponse.Create(questionId, LocalDateTime.now());
        //when
        when(questionService.createQuestion(any(), any())).thenReturn(response);
        //then
        mockMvc.perform(post(QUESTION_BASE_URL)
                        .header(AUTHORIZATION_HEADER, ACCESS_TOKEN_PREFIX + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create))
                )
                .andDo(print())
                .andDo(document("question-create",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("Access Token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Resource Location")
                        ),
                        requestFields(
                                fieldWithPath("title").type(STRING).description("질문 제목"),
                                fieldWithPath("contents").type(STRING).description("질문 내용"),
                                fieldWithPath("tags").type(ARRAY).description("태그"),
                                fieldWithPath("imageIds").type(ARRAY).description("업로드된 이미지 식별 아이디").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("result.id").type(NUMBER).description("질문 식별 아이디"),
                                fieldWithPath("result.askedAt").type(STRING).description("질문 생성 시간"),
                                fieldWithPath("result.links.[0].rel").type(STRING).description("self"),
                                fieldWithPath("result.links.[0].href").type(STRING).description("uri")
                        )
                ));

    }

    @Test
    @DisplayName("질문 수정")
    @WithCustomMockUser(memberId = "321312", authority = MEMBER)
    void 질문수정() throws Exception {
        //given
        Long questionId = 5321L;
        String title = "제목입니다.";
        String markdownContents = "내용입니다. ----- * 내용은 * 마크다운형식입니다.";
        List<Long> imageIds = List.of(23L, 134L, 5213L);

        String accessToken = UUID.randomUUID().toString();
        QuestionRequest.Modify request = new QuestionRequest.Modify(title,markdownContents,imageIds);
        QuestionResponse.Modify response = new QuestionResponse.Modify(questionId,LocalDateTime.now());
        //when
        when(questionService.modifyQuestion(any(),any() ,any())).thenReturn(response);
        //then
        mockMvc.perform(patch(QUESTION_PATCH_URL,questionId)
                        .header(AUTHORIZATION_HEADER, ACCESS_TOKEN_PREFIX + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(document("question-modify",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("Access Token")
                        ),
                        requestFields(
                                fieldWithPath("title").type(STRING).description("수정된 질문 제목"),
                                fieldWithPath("contents").type(STRING).description("수정된 질문 내용"),
                                fieldWithPath("imageIds").type(ARRAY).description("수정된 이미지 식별 아이디").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("result.id").type(NUMBER).description("질문 식별 아이디"),
                                fieldWithPath("result.modifiedAt").type(STRING).description("질문 수정 시간"),
                                fieldWithPath("result.links.[0].rel").type(STRING).description("self"),
                                fieldWithPath("result.links.[0].href").type(STRING).description("uri")
                        )
                ));

    }

    @Test
    @DisplayName("질문 리스트 조회")
    void 질문리스트조회() throws Exception {
        //given
        QuestionDto.Metrics metrics1 = new QuestionDto.Metrics(421, 100, 2);
        QuestionDto.Metrics metrics2 = new QuestionDto.Metrics(10, 50, 0);
        QuestionDto.Metrics metrics3 = new QuestionDto.Metrics(100, 412, 13);

        QuestionDto.Questioner questioner1 = new QuestionDto.Questioner(3214L, "닉네임1", "https://bori-market-bucket.s3.ap-northeast-2.amazonaws.com/default/profile.jpg", 100);
        QuestionDto.Questioner questioner2 = new QuestionDto.Questioner(594L, "닉네임2", "https://bori-market-bucket.s3.ap-northeast-2.amazonaws.com/default/profile.jpg", 620);
        QuestionDto.Questioner questioner3 = new QuestionDto.Questioner(9525L, "닉네임3", "https://bori-market-bucket.s3.ap-northeast-2.amazonaws.com/default/profile.jpg", 350);

        List<HashTagDto> tagDtos1 = List.of(
                new HashTagDto(54L, "#java"),
                new HashTagDto(34L, "#spring")
        );

        List<HashTagDto> tagDtos2 = List.of(
                new HashTagDto(2L, "#javascript"),
                new HashTagDto(14L, "#react")
        );

        List<HashTagDto> tagDtos3 = List.of(
                new HashTagDto(31L, "#mysql"),
                new HashTagDto(7L, "#database")
        );

        List<QuestionDto> questionDtos = List.of(
                new QuestionDto(99L, "질문1", "질문내용 미리보기1..", metrics1, questioner1, tagDtos1, LocalDateTime.now()),
                new QuestionDto(98L, "질문1", "질문내용 미리보기1..", metrics2, questioner2, tagDtos2, LocalDateTime.now()),
                new QuestionDto(97L, "질문1", "질문내용 미리보기1..", metrics3, questioner3, tagDtos3, LocalDateTime.now())
        );

        QuestionListResult questionListResult = new QuestionListResult(3, true, QuestionSort.NEW_EAST, 97L, null, null, questionDtos);
        //when
        given(questionQueryService.getQuestionList(any())).willReturn(questionListResult);
        //then
        mockMvc.perform(get(QUESTION_BASE_URL))
                .andDo(print())
                .andDo(document("question-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("result.size").type(NUMBER).description("데이터 크기"),
                                fieldWithPath("result.hasNext").type(BOOLEAN).description("다음 데이터 존재 유무"),
                                fieldWithPath("result.sortBy").type(STRING).description("정렬 방식"),
                                fieldWithPath("result.lastOfIndex").type(NUMBER).description("현재 조회된 데이터의 마지막 인덱스"),
                                fieldWithPath("result.lastOfView").type(NULL).description("현재 조회된 데이터의 마지막 조회수"),
                                fieldWithPath("result.lastOfRecommend").type(NULL).description("조회된 데이터의 마지막 추천수"),
                                fieldWithPath("result.links.[0].rel").type(STRING).description("요청된 url"),
                                fieldWithPath("result.links.[0].href").type(STRING).description("요청 url"),
                                fieldWithPath("result.links.[1].rel").type(STRING).description("다음 데이터"),
                                fieldWithPath("result.links.[1].href").type(STRING).description("url"),
                                fieldWithPath("result.links.[2].rel").type(STRING).description("데이터 사이즈 조절"),
                                fieldWithPath("result.links.[2].href").type(STRING).description("url"),
                                fieldWithPath("result.links.[3].rel").type(STRING).description("조회수로 정렬"),
                                fieldWithPath("result.links.[3].href").type(STRING).description("url"),
                                fieldWithPath("result.links.[4].rel").type(STRING).description("추천수로 정렬"),
                                fieldWithPath("result.links.[4].href").type(STRING).description("url"),
                                fieldWithPath("result.links.[5].rel").type(STRING).description("답변 유무로 필터"),
                                fieldWithPath("result.links.[5].href").type(STRING).description("url"),
                                fieldWithPath("result.results").type(ARRAY).description("결과 목록"),
                                fieldWithPath("result.results.[0].id").type(NUMBER).description("질문 식별 아이디"),
                                fieldWithPath("result.results.[0].title").type(STRING).description("질문 제목"),
                                fieldWithPath("result.results.[0].preview").type(STRING).description("질문 내용 미리보기"),
                                fieldWithPath("result.results.[0].askedAt").type(STRING).description("질문 등록 일시"),
                                fieldWithPath("result.results.[0].metrics").type(OBJECT).description("지표 정보"),
                                fieldWithPath("result.results.[0].metrics.countOfRecommend").type(NUMBER).description("추천수"),
                                fieldWithPath("result.results.[0].metrics.countOfView").type(NUMBER).description("조회수"),
                                fieldWithPath("result.results.[0].metrics.countOfAnswer").type(NUMBER).description("답변수"),
                                fieldWithPath("result.results.[0].questioner").type(OBJECT).description("질문자 정보"),
                                fieldWithPath("result.results.[0].questioner.id").type(NUMBER).description("질문자 식별 아이디(memberId)"),
                                fieldWithPath("result.results.[0].questioner.nickname").type(STRING).description("닉네임"),
                                fieldWithPath("result.results.[0].questioner.profileImage").type(STRING).description("프로필 이미지"),
                                fieldWithPath("result.results.[0].questioner.activityScore").type(NUMBER).description("활동점수"),
                                fieldWithPath("result.results.[0].hashTag").type(ARRAY).description("해시 태그"),
                                fieldWithPath("result.results.[0].hashTag.[0].id").type(NUMBER).description("해시태그 식별아이디"),
                                fieldWithPath("result.results.[0].hashTag.[0].hashTagName").type(STRING).description("해시태그네임"),
                                fieldWithPath("result.results.[0].links.[0].rel").type(STRING).description("rel"),
                                fieldWithPath("result.results.[0].links.[0].href").type(STRING).description("href")
                        )
                ));

    }


}
