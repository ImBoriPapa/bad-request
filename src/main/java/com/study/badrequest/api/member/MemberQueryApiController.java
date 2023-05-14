package com.study.badrequest.api.member;

import com.study.badrequest.api.login.LoginController;
import com.study.badrequest.commons.annotation.LoggedInMember;
import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.domain.login.CurrentLoggedInMember;
import com.study.badrequest.exception.custom_exception.MemberExceptionBasic;
import com.study.badrequest.repository.member.MemberQueryRepository;
import com.study.badrequest.repository.member.query.LoggedInMemberInformation;
import com.study.badrequest.repository.member.query.MemberDetailDto;
import com.study.badrequest.repository.member.query.MemberProfileDto;

import com.study.badrequest.utils.modelAssembler.MemberResponseModelAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static com.study.badrequest.commons.constants.ApiURL.*;
import static com.study.badrequest.commons.response.ApiResponseStatus.NOTFOUND_MEMBER;
import static com.study.badrequest.commons.response.ApiResponseStatus.NOT_MATCH_REQUEST_MEMBER_WITH_LOGGED_IN_MEMBER;
import static com.study.badrequest.utils.authority.AuthorityUtils.restrictAccessIfNotYouAndAdmin;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberQueryApiController {
    private final MemberQueryRepository memberQueryRepository;
    private final MemberResponseModelAssembler memberResponseModelAssembler;

    @GetMapping(value = GET_MEMBER_DETAIL_URL, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse.Success> retrieveMemberAccount(@LoggedInMember CurrentLoggedInMember.Information information, @PathVariable Long memberId) {
        log.info("계정정보 조회 요청 요청계정 Id: {}, 요청자 id: {}, 요청자 권한: {}", memberId, information.getId(), information.getAuthority());

        restrictAccessIfNotYouAndAdmin(information.getId(), memberId, information.getAuthority());

        MemberDetailDto memberDetailDto = memberQueryRepository.findMemberDetail(memberId)
                .orElseThrow(() -> new MemberExceptionBasic(ApiResponseStatus.NOTFOUND_MEMBER));

        EntityModel<MemberDetailDto> entityModel = memberResponseModelAssembler.retrieveMemberModel(memberDetailDto);

        return ResponseEntity
                .ok()
                .body(new ApiResponse.Success(ApiResponseStatus.SUCCESS, entityModel));
    }

    /**
     * 로그인한 회원 정보
     */
    @GetMapping(GET_LOGGED_IN_MEMBER_INFORMATION)
    public ResponseEntity getLoggedInInformation(@PathVariable Long memberId, @LoggedInMember CurrentLoggedInMember.Information information) {

        if (!memberId.equals(information.getId())) {
            throw new MemberExceptionBasic(NOT_MATCH_REQUEST_MEMBER_WITH_LOGGED_IN_MEMBER);
        }

        LoggedInMemberInformation memberInformation = memberQueryRepository.findLoggedInMemberInformation(information.getId())
                .orElseThrow(() -> new MemberExceptionBasic(NOTFOUND_MEMBER));

        EntityModel<LoggedInMemberInformation> entityModel = EntityModel.of(memberInformation);
        entityModel.add(linkTo(methodOn(MemberQueryApiController.class).getLoggedInInformation(memberId, null)).withSelfRel());
        entityModel.add(linkTo(methodOn(MemberQueryApiController.class).getProfile(memberId, null)).withRel("get profile"));
        entityModel.add(linkTo(methodOn(MemberQueryApiController.class).getActivity(memberId, null)).withRel("get activities"));
        entityModel.add(linkTo(methodOn(LoginController.class).logout(null, null)).withRel("logout"));

        return ResponseEntity.ok().body(new ApiResponse.Success<>(ApiResponseStatus.SUCCESS, entityModel));
    }

    @GetMapping("/api/v2/members/{memberId}/activities")
    public ResponseEntity getActivity(@PathVariable Long memberId, @LoggedInMember CurrentLoggedInMember.Information information) {

        return ResponseEntity.ok().body(null);
    }

    /**
     * 프로필 정보 보기
     */
    @GetMapping(GET_MEMBER_PROFILE)
    public ResponseEntity getProfile(@PathVariable Long memberId, @LoggedInMember CurrentLoggedInMember.Information information) {
        log.info("프로필 조회 요청 memberId: {}", memberId);

        MemberProfileDto memberProfileDto = memberQueryRepository.findMemberProfileByMemberId(memberId)
                .orElseThrow(() -> new MemberExceptionBasic(ApiResponseStatus.NOTFOUND_MEMBER));

        return ResponseEntity
                .ok()
                .body(memberProfileDto);
    }
}
