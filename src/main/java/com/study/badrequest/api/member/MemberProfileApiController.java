package com.study.badrequest.api.member;

import com.study.badrequest.commons.annotation.LoggedInMember;
import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.domain.login.CurrentMember;
import com.study.badrequest.dto.member.MemberRequest;
import com.study.badrequest.dto.member.MemberResponse;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.service.member.MemberProfileService;
import com.study.badrequest.utils.header.HttpHeaderResolver;
import com.study.badrequest.utils.modelAssembler.MemberResponseModelAssembler;
import com.study.badrequest.utils.verification.RequestValidUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import static com.study.badrequest.commons.constants.ApiURL.*;
import static com.study.badrequest.commons.response.ApiResponseStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberProfileApiController {
    private final MemberProfileService memberProfileService;
    private final MemberResponseModelAssembler memberResponseModelAssembler;

    /**
     * 닉네임 변경 요청
     */
    @PatchMapping(value = PATCH_MEMBER_NICKNAME, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity changeNickname(@PathVariable Long memberId,
                                         @Validated @RequestBody MemberRequest.ChangeNickname form, BindingResult bindingResult,
                                         @LoggedInMember CurrentMember.Information information,
                                         HttpServletRequest request
    ) {
        log.info("Nickname Change Request: memberId: {},", memberId);
        String ipAddress = HttpHeaderResolver.ipAddressResolver(request);
        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);
        RequestValidUtils.throwMemberExceptionIfNotMatchMemberId(memberId, information.getId());

        MemberResponse.Update response = memberProfileService.changeNickname(memberId, form, ipAddress);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, memberResponseModelAssembler.changeNicknameModel(response)));
    }

    /**
     * 프로필 이미지 변경
     */
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

        MemberResponse.Update response = memberProfileService.changeProfileImage(memberId, image, ipAddress);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, memberResponseModelAssembler.changeProfileImageModel(response)));
    }

    /**
     * 프로필 이미지 삭제 -> 기본 프로필 이미지로 변경
     */
    @DeleteMapping(DELETE_MEMBER_PROFILE_IMAGE)
    public ResponseEntity deleteProfileImage(@PathVariable Long memberId,
                                             HttpServletRequest request) {
        log.info("Delete Profile Image Request");
        String ipAddress = HttpHeaderResolver.ipAddressResolver(request);
        MemberResponse.Delete response = memberProfileService.deleteProfileImage(memberId, ipAddress);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, response));
    }

    /**
     * 자기 소개 변경 요청
     */
    @PatchMapping(value = PATCH_MEMBER_INTRODUCE, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity changeIntroduce(@PathVariable Long memberId,
                                          @RequestBody MemberRequest.ChangeIntroduce form,
                                          HttpServletRequest request) {
        log.info("Change Introduce Request");
        String ipAddress = HttpHeaderResolver.ipAddressResolver(request);
        MemberResponse.Update update = memberProfileService.changeIntroduce(memberId, form, ipAddress);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, update));
    }
}
