package com.study.badrequest.member.query.interfaces;

import com.study.badrequest.login.command.interfaces.LoginApiController;
import com.study.badrequest.common.annotation.LoggedInMember;
import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.common.response.ApiResponse;
import com.study.badrequest.login.command.domain.CustomMemberPrincipal;
import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.query.dao.MemberQueryRepository;
import com.study.badrequest.member.query.dto.LoggedInMemberInformation;
import com.study.badrequest.member.query.dto.MemberDetailDto;
import com.study.badrequest.member.query.dto.MemberProfileDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static com.study.badrequest.common.constants.ApiURL.*;
import static com.study.badrequest.common.response.ApiResponseStatus.NOTFOUND_MEMBER;
import static com.study.badrequest.common.response.ApiResponseStatus.NOT_MATCH_REQUEST_MEMBER_WITH_LOGGED_IN_MEMBER;
import static com.study.badrequest.utils.authority.AuthorityUtils.verifyPermission;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberQueryApiController {
    private final MemberQueryRepository memberQueryRepository;

    @GetMapping(value = GET_MEMBER_DETAIL_URL, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity retrieveMemberAccount(@LoggedInMember CustomMemberPrincipal information, @PathVariable Long memberId) {
        log.info("계정정보 조회 요청 요청계정 Id: {}, 요청자 id: {}, 요청자 권한: {}", memberId, information.getMemberId(), information.getAuthority());

        verifyPermission(memberId, information.getMemberId(), information.getAuthority(), ApiResponseStatus.PERMISSION_DENIED);

        MemberDetailDto memberDetailDto = memberQueryRepository.findMemberDetail(memberId)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(ApiResponseStatus.SUCCESS, memberDetailDto));
    }

    /**
     * 로그인한 회원 정보
     */
    @GetMapping(GET_LOGGED_IN_MEMBER_INFORMATION)
    public ResponseEntity getLoggedInInformation(@PathVariable Long memberId, @LoggedInMember CustomMemberPrincipal information) {

        if (!memberId.equals(information.getMemberId())) {
            throw CustomRuntimeException.createWithApiResponseStatus(NOT_MATCH_REQUEST_MEMBER_WITH_LOGGED_IN_MEMBER);
        }

        LoggedInMemberInformation memberInformation = memberQueryRepository.findLoggedInMemberInformation(information.getMemberId())
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));

        EntityModel<LoggedInMemberInformation> entityModel = EntityModel.of(memberInformation);
        entityModel.add(linkTo(methodOn(MemberQueryApiController.class).getLoggedInInformation(memberId, null)).withSelfRel());
        entityModel.add(linkTo(methodOn(MemberQueryApiController.class).getProfile(memberId, null)).withRel("get profile"));
        entityModel.add(linkTo(methodOn(MemberQueryApiController.class).getActivity(memberId, null)).withRel("get activities"));
        entityModel.add(linkTo(methodOn(LoginApiController.class).logout(null, null)).withRel("logout"));

        return ResponseEntity.ok().body(ApiResponse.success(ApiResponseStatus.SUCCESS, entityModel));
    }

    @GetMapping("/api/v2/members/{memberId}/activities")
    public ResponseEntity getActivity(@PathVariable Long memberId, @LoggedInMember CustomMemberPrincipal information) {

        return ResponseEntity.ok().body(null);
    }

    /**
     * 프로필 정보 보기
     */
    @GetMapping(GET_MEMBER_PROFILE)
    public ResponseEntity getProfile(@PathVariable Long memberId, @LoggedInMember CustomMemberPrincipal information) {
        log.info("프로필 조회 요청 memberId: {}", memberId);

        MemberProfileDto memberProfileDto = memberQueryRepository.findMemberProfileByMemberId(memberId)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOTFOUND_MEMBER));

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(memberProfileDto));
    }
}
