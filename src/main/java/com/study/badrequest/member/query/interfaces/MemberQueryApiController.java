package com.study.badrequest.member.query.interfaces;

import com.study.badrequest.member.command.interfaces.LoginController;
import com.study.badrequest.common.annotation.LoggedInMember;
import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.common.response.ApiResponse;
import com.study.badrequest.member.command.domain.CurrentMember;
import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.query.dao.MemberQueryRepository;
import com.study.badrequest.member.query.dto.LoggedInMemberInformation;
import com.study.badrequest.member.query.dto.MemberDetailDto;
import com.study.badrequest.member.query.dto.MemberProfileDto;

import com.study.badrequest.utils.modelAssembler.MemberResponseModelAssembler;
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
    private final MemberResponseModelAssembler memberResponseModelAssembler;

    @GetMapping(value = GET_MEMBER_DETAIL_URL, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity retrieveMemberAccount(@LoggedInMember CurrentMember.Information information, @PathVariable Long memberId) {
        log.info("계정정보 조회 요청 요청계정 Id: {}, 요청자 id: {}, 요청자 권한: {}", memberId, information.getId(), information.getAuthority());

        verifyPermission(memberId, information.getId(), information.getAuthority(), ApiResponseStatus.PERMISSION_DENIED);

        MemberDetailDto memberDetailDto = memberQueryRepository.findMemberDetail(memberId)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));

        EntityModel<MemberDetailDto> entityModel = memberResponseModelAssembler.retrieveMemberModel(memberDetailDto);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(ApiResponseStatus.SUCCESS, entityModel));
    }

    /**
     * 로그인한 회원 정보
     */
    @GetMapping(GET_LOGGED_IN_MEMBER_INFORMATION)
    public ResponseEntity getLoggedInInformation(@PathVariable Long memberId, @LoggedInMember CurrentMember.Information information) {

        if (!memberId.equals(information.getId())) {
            throw CustomRuntimeException.createWithApiResponseStatus(NOT_MATCH_REQUEST_MEMBER_WITH_LOGGED_IN_MEMBER);
        }

        LoggedInMemberInformation memberInformation = memberQueryRepository.findLoggedInMemberInformation(information.getId())
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));

        EntityModel<LoggedInMemberInformation> entityModel = EntityModel.of(memberInformation);
        entityModel.add(linkTo(methodOn(MemberQueryApiController.class).getLoggedInInformation(memberId, null)).withSelfRel());
        entityModel.add(linkTo(methodOn(MemberQueryApiController.class).getProfile(memberId, null)).withRel("get profile"));
        entityModel.add(linkTo(methodOn(MemberQueryApiController.class).getActivity(memberId, null)).withRel("get activities"));
        entityModel.add(linkTo(methodOn(LoginController.class).logout(null, null)).withRel("logout"));

        return ResponseEntity.ok().body(ApiResponse.success(ApiResponseStatus.SUCCESS, entityModel));
    }

    @GetMapping("/api/v2/members/{memberId}/activities")
    public ResponseEntity getActivity(@PathVariable Long memberId, @LoggedInMember CurrentMember.Information information) {

        return ResponseEntity.ok().body(null);
    }

    /**
     * 프로필 정보 보기
     */
    @GetMapping(GET_MEMBER_PROFILE)
    public ResponseEntity getProfile(@PathVariable Long memberId, @LoggedInMember CurrentMember.Information information) {
        log.info("프로필 조회 요청 memberId: {}", memberId);

        MemberProfileDto memberProfileDto = memberQueryRepository.findMemberProfileByMemberId(memberId)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOTFOUND_MEMBER));

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(memberProfileDto));
    }
}
