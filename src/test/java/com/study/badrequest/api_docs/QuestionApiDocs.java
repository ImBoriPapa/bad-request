package com.study.badrequest.api_docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.question.command.interfaces.QuestionApiController;
import com.study.badrequest.question.query.interfaces.QuestionQueryApiController;
import com.study.badrequest.question.command.domain.QuestionSortType;
import com.study.badrequest.recommandation.command.domain.RecommendationKind;
import com.study.badrequest.question.query.interfaces.QuestionRequest;
import com.study.badrequest.question.query.interfaces.QuestionResponse;
import com.study.badrequest.filter.JwtAuthenticationFilter;
import com.study.badrequest.question.query.QuestionDetail;
import com.study.badrequest.question.query.TagDto;
import com.study.badrequest.question.query.QuestionDto;
import com.study.badrequest.question.query.QuestionListResult;
import com.study.badrequest.question.command.application.QuestionMetricsService;
import com.study.badrequest.question.command.application.QuestionQueryService;
import com.study.badrequest.question.command.application.QuestionService;
import com.study.badrequest.question.command.application.QuestionTagService;
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

import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.study.badrequest.common.constants.ApiURL.*;
import static com.study.badrequest.common.constants.AuthenticationHeaders.ACCESS_TOKEN_PREFIX;
import static com.study.badrequest.common.constants.AuthenticationHeaders.AUTHORIZATION_HEADER;
import static com.study.badrequest.member.command.domain.Authority.*;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentRequest;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;


import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
    @DisplayName("질문 수정")
    @WithCustomMockUser(memberId = "321312", authority = MEMBER)
    void 질문수정() throws Exception {
        //given
        Long questionId = 5321L;
        String title = "제목입니다.";
        String markdownContents = "내용입니다. ----- * 내용은 * 마크다운형식입니다.";
        List<Long> imageIds = List.of(23L, 134L, 5213L);

        String accessToken = UUID.randomUUID().toString();
        QuestionRequest.Modify request = new QuestionRequest.Modify(title, markdownContents, imageIds);
        QuestionResponse.Modify response = new QuestionResponse.Modify(questionId, LocalDateTime.now());
        //when
        when(questionService.modifyQuestionProcessing(any(), any(), any())).thenReturn(response);
        //then
        mockMvc.perform(patch(QUESTION_PATCH_URL, questionId)
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
    @DisplayName("질문 삭제")
    @WithCustomMockUser(memberId = "5324", authority = MEMBER)
    void 질문삭제() throws Exception {
        //given
        Long memberId = 5324L;
        Long questionId = 23214L;
        String accessToken = UUID.randomUUID().toString();
        QuestionResponse.Delete response = new QuestionResponse.Delete(questionId, LocalDateTime.now());
        //when
        given(questionService.deleteQuestionProcess(any(), any())).willReturn(response);
        //then
        mockMvc.perform(delete(QUESTION_DELETE_URL, questionId)
                        .header(AUTHORIZATION_HEADER, ACCESS_TOKEN_PREFIX + accessToken))
                .andDo(print())
                .andDo(document("question-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("Access Token")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("result.id").type(NUMBER).description("질문 식별 아이디"),
                                fieldWithPath("result.deletedAt").type(STRING).description("질문 삭제 시간"),
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

        List<TagDto> tagDtos1 = List.of(
                new TagDto(54L, "#java"),
                new TagDto(34L, "#spring")
        );

        List<TagDto> tagDtos2 = List.of(
                new TagDto(2L, "#javascript"),
                new TagDto(14L, "#react")
        );

        List<TagDto> tagDtos3 = List.of(
                new TagDto(31L, "#mysql"),
                new TagDto(7L, "#database")
        );

        List<QuestionDto> questionDtos = List.of(
                new QuestionDto(99L, "질문1", "질문내용 미리보기1..", metrics1, questioner1, tagDtos1, LocalDateTime.now()),
                new QuestionDto(98L, "질문1", "질문내용 미리보기1..", metrics2, questioner2, tagDtos2, LocalDateTime.now()),
                new QuestionDto(97L, "질문1", "질문내용 미리보기1..", metrics3, questioner3, tagDtos3, LocalDateTime.now())
        );

        QuestionListResult questionListResult = new QuestionListResult(3, true, QuestionSortType.NEW_EAST, 97L, questionDtos);
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
                                fieldWithPath("result.lastOfData").type(NUMBER).description("다음 데이터를 요청하기 위한 마지막 데이터 식별값"),
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
                                fieldWithPath("result.results.[0].tags").type(ARRAY).description("태그"),
                                fieldWithPath("result.results.[0].tags.[0].id").type(NUMBER).description("질문태그 식별 아이디"),
                                fieldWithPath("result.results.[0].tags.[0].tagName").type(STRING).description("해시태그 네임"),
                                fieldWithPath("result.results.[0].tags.[0].links.[0].rel").type(STRING).description("태그 검색"),
                                fieldWithPath("result.results.[0].tags.[0].links.[0].href").type(STRING).description("경로 "),
                                fieldWithPath("result.results.[0].links.[0].rel").type(STRING).description("rel"),
                                fieldWithPath("result.results.[0].links.[0].href").type(STRING).description("href")
                        )
                ));

    }


    @Test
    @DisplayName("질문 상세 조회")
    void 질문상세조회() throws Exception {
        //given
        String accessToken = UUID.randomUUID().toString();
        Long questionId = 563L;
        String title = "제목입니다.";
        String contents = "<p>내용입니다.</p>";
        boolean isQuestioner = true;
        int countOfRecommend = 100;
        int countOfView = 632;
        int countOfAnswer = 3;
        boolean hasRecommendation = false;
        RecommendationKind kind = null;
        Long memberId = 242L;
        String nickname = "닉네임입니다";
        String imageLocation = "https://my-bucket-s3/profile/my_image.png";
        QuestionDetail.QuestionDetailMetrics metrics = new QuestionDetail.QuestionDetailMetrics(countOfRecommend, countOfView, countOfAnswer, hasRecommendation, kind);
        int activityScore = 240;
        QuestionDetail.QuestionDetailQuestioner questioner = new QuestionDetail.QuestionDetailQuestioner(memberId, nickname, imageLocation, activityScore);
        TagDto tag1 = new TagDto(24L, "#java");
        TagDto tag2 = new TagDto(26L, "#spring");
        List<TagDto> tags = List.of(tag1, tag2);
        LocalDateTime askedAt = LocalDateTime.now();
        LocalDateTime modifiedAt = LocalDateTime.now();
        QuestionDetail questionDetail = new QuestionDetail(242L, title, contents, isQuestioner, metrics, questioner, tags, askedAt, modifiedAt);
        //when
        given(questionQueryService.getQuestionDetail(any(), any(), any(), any())).willReturn(questionDetail);
        //then
        mockMvc.perform(get(QUESTION_DETAIL_URL, questionId)
                        .header(AUTHORIZATION_HEADER, accessToken))
                .andDo(print())
                .andDo(document("question-detail",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(headerWithName(AUTHORIZATION_HEADER).description("Access Token").optional()),
                                pathParameters(
                                        parameterWithName("questionId").description("질문 식별 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(STRING).description("응답 상태"),
                                        fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                                        fieldWithPath("result.id").type(NUMBER).description("질문 식별 아이디"),
                                        fieldWithPath("result.title").type(STRING).description("질문 제목"),
                                        fieldWithPath("result.contents").type(STRING).description("질문 내용"),
                                        fieldWithPath("result.isQuestioner").type(BOOLEAN).description("질문자"),
                                        fieldWithPath("result.metrics.countOfRecommend").type(NUMBER).description("추천수"),
                                        fieldWithPath("result.metrics.countOfView").type(NUMBER).description("조회수"),
                                        fieldWithPath("result.metrics.countOfAnswer").type(NUMBER).description("답변수"),
                                        fieldWithPath("result.metrics.hasRecommendation").type(BOOLEAN).description("추천 여부"),
                                        fieldWithPath("result.metrics.kind").type(NULL).description("추천 종류"),
                                        fieldWithPath("result.questioner.id").type(NUMBER).description("질문자 식별 아이디"),
                                        fieldWithPath("result.questioner.nickname").type(STRING).description("질문자 닉네임"),
                                        fieldWithPath("result.questioner.profileImage").type(STRING).description("프로필 이미지"),
                                        fieldWithPath("result.questioner.activityScore").type(NUMBER).description("활동 점수"),
                                        fieldWithPath("result.tag.[0].id").type(NUMBER).description("질문 태그 아이디"),
                                        fieldWithPath("result.tag.[0].tagName").type(STRING).description("태그명"),
                                        fieldWithPath("result.tag.[0].links.[0].rel").type(STRING).description(""),
                                        fieldWithPath("result.tag.[0].links.[0].href").type(STRING).description(""),
                                        fieldWithPath("result.askedAt").type(STRING).description("질문일"),
                                        fieldWithPath("result.modifiedAt").type(STRING).description("수정일"),
                                        fieldWithPath("result.links.[0].rel").type(STRING).description(""),
                                        fieldWithPath("result.links.[0].href").type(STRING).description("")
                                )


                        )
                );


    }
}
