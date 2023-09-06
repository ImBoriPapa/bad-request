package com.study.badrequest.member.command.interfaces;

import com.study.badrequest.common.annotation.LoggedInMember;
import com.study.badrequest.common.response.ApiResponse;
import com.study.badrequest.login.command.domain.CustomMemberPrincipal;
import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.application.MemberProfileService;
import com.study.badrequest.member.command.domain.values.MemberId;
import com.study.badrequest.utils.header.HttpHeaderResolver;
import com.study.badrequest.utils.verification.RequestValidUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import static com.study.badrequest.common.constants.ApiURL.*;
import static com.study.badrequest.common.response.ApiResponseStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberProfileApiController {
    private final MemberProfileService memberProfileService;
    @PatchMapping(value = PATCH_MEMBER_NICKNAME, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity changeNickname(@PathVariable Long memberId,
                                         @Validated @RequestBody MemberRequest.ChangeNickname form, BindingResult bindingResult,
                                         @LoggedInMember CustomMemberPrincipal information,
                                         HttpServletRequest request
    ) {
        log.info("Nickname Change Request: memberId: {},", memberId);
        String ipAddress = HttpHeaderResolver.ipAddressResolver(request);
        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);
        RequestValidUtils.throwMemberExceptionIfNotMatchMemberId(memberId, information.getMemberId());

        memberProfileService.changeNickname(new MemberId(memberId),null);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, null));
    }


    @PatchMapping(PATCH_MEMBER_PROFILE_IMAGE)
    public ResponseEntity changeProfileImage(@PathVariable Long memberId,
                                             @RequestPart(name = "image", required = false) MultipartFile image,
                                             HttpServletRequest request
    ) {
        log.info("Change Profile Image Request");

        String ipAddress = HttpHeaderResolver.ipAddressResolver(request);

        if (image == null) {
            throw CustomRuntimeException.createWithApiResponseStatus(NOT_FOUND_IMAGE_FILE);
        }

        if (image.getSize() > 500000) {
            throw CustomRuntimeException.createWithApiResponseStatus(TOO_BIG_PROFILE_IMAGE_SIZE);
        }

        memberProfileService.changeProfileImage(new MemberId(memberId), image, ipAddress);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, null));
    }

    @DeleteMapping(DELETE_MEMBER_PROFILE_IMAGE)
    public ResponseEntity deleteProfileImage(@PathVariable Long memberId,
                                             HttpServletRequest request) {
        log.info("Delete Profile Image Request");
        String ipAddress = HttpHeaderResolver.ipAddressResolver(request);

        memberProfileService.deleteProfileImage(new MemberId(memberId), ipAddress);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, null));
    }


    @PatchMapping(value = PATCH_MEMBER_INTRODUCE, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity changeIntroduce(@PathVariable Long memberId,
                                          @RequestBody MemberRequest.ChangeIntroduce form,
                                          HttpServletRequest request) {
        log.info("Change Introduce Request");
        String ipAddress = HttpHeaderResolver.ipAddressResolver(request);
        memberProfileService.changeIntroduce(new MemberId(memberId), form, ipAddress);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, null));
    }
}
