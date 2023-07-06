package com.study.badrequest.api.member;


import com.study.badrequest.commons.annotation.LoggedInMember;
import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.domain.login.CurrentMember;
import com.study.badrequest.dto.member.MemberRequest;
import com.study.badrequest.dto.member.MemberResponse;
import com.study.badrequest.service.member.MemberService;

import com.study.badrequest.utils.header.HttpHeaderResolver;
import com.study.badrequest.utils.modelAssembler.MemberResponseModelAssembler;
import com.study.badrequest.utils.verification.RequestValidUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.net.URI;

import static com.study.badrequest.commons.constants.ApiURL.*;
import static com.study.badrequest.commons.response.ApiResponseStatus.SUCCESS;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberAccountApiController {
    private final MemberService memberService;
    private final MemberResponseModelAssembler memberResponseModelAssembler;
    @PostMapping(value = POST_MEMBER_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMember(HttpServletRequest request, @Validated @RequestBody MemberRequest.SignUp form, BindingResult bindingResult) {
        log.info("Create Member Request");
        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);
        String ipAddress = HttpHeaderResolver.ipAddressResolver(request);

        MemberResponse.Create create = memberService.signupMemberProcessingByEmail(form, ipAddress);

        URI locationUri = memberResponseModelAssembler.getLocationUri(create.getId());

        return ResponseEntity
                .created(locationUri)
                .body(ApiResponse.success(SUCCESS, memberResponseModelAssembler.createMemberModel(create)));
    }

    @PostMapping(value = POST_MEMBER_TEMPORARY_PASSWORD_ISSUE_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> issueTemporaryPassword(@Validated
                                                 @RequestBody MemberRequest.IssueTemporaryPassword form,
                                                 BindingResult bindingResult,
                                                 HttpServletRequest request
    ) {
        log.info("[임시 비밀번호 요청 email: {}]", form.getEmail());

        String ipAddress = HttpHeaderResolver.ipAddressResolver(request);

        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.TemporaryPassword issueTemporaryPassword = memberService.issueTemporaryPasswordProcessing(form.getEmail(), ipAddress);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, memberResponseModelAssembler.getIssuePasswordModel(issueTemporaryPassword)));
    }


    @PostMapping(POST_MEMBER_SEND_EMAIL_AUTHENTICATION_CODE)
    public ResponseEntity<?> sendAuthenticationEmail(@Validated @RequestBody MemberRequest.SendAuthenticationEmail form, BindingResult bindingResult) {
        log.info("[이메일 인증 번호 요청 email: {}]", form.getEmail());

        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.SendAuthenticationEmail sendAuthenticationEmail = memberService.sendAuthenticationMailProcessing(form.getEmail());

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, memberResponseModelAssembler.getSendAuthenticationMail(sendAuthenticationEmail)));
    }

    @PatchMapping(value = PATCH_MEMBER_PASSWORD_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> patchPassword(@Validated
                                        @PathVariable Long memberId,
                                        @RequestBody MemberRequest.ChangePassword form,
                                        @LoggedInMember CurrentMember.Information information,
                                        BindingResult bindingResult,
                                        HttpServletRequest request
    ) {
        log.info("[비밀번호 변경 요청 memberId: {}, password: {}, password: {}]", memberId, "PROTECTED", "PROTECTED");

        String ipAddress = HttpHeaderResolver.ipAddressResolver(request);

        RequestValidUtils.throwMemberExceptionIfNotMatchMemberId(memberId, information.getId());

        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.Update update = memberService.changePasswordProcessing(memberId, form, ipAddress);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, memberResponseModelAssembler.getChangePasswordModel(update)));
    }


    @PatchMapping(value = PATCH_MEMBER_CONTACT_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> patchContact(@Validated
                                       @PathVariable Long memberId,
                                       @RequestBody MemberRequest.UpdateContact form,
                                       @LoggedInMember CurrentMember.Information information,
                                       BindingResult bindingResult,
                                       HttpServletRequest request
    ) {
        log.info("[연락처 변경 요청 memberId: {}, contact: {}]", memberId, form.getContact());

        String ipAddress = HttpHeaderResolver.ipAddressResolver(request);

        RequestValidUtils.throwMemberExceptionIfNotMatchMemberId(memberId, information.getId());

        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.Update update = memberService.changeContactProcessing(memberId, form.getContact(), ipAddress);

        return ResponseEntity.ok()
                .body(ApiResponse.success(SUCCESS, memberResponseModelAssembler.getChangeContactModel(update)));
    }


    @DeleteMapping(DELETE_MEMBER_URL)
    public ResponseEntity<?> deleteMember(@Validated @PathVariable Long memberId,
                                       @RequestBody MemberRequest.DeleteMember form,
                                       @LoggedInMember CurrentMember.Information information,
                                       BindingResult bindingResult,
                                       HttpServletRequest request
    ) {

        log.info("[회원 탈퇴 요청 memberId: {}, password: {}]", memberId, "PROTECTED");

        String ipAddress = HttpHeaderResolver.ipAddressResolver(request);

        RequestValidUtils.throwMemberExceptionIfNotMatchMemberId(memberId, information.getId());

        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.Delete delete = memberService.withdrawalMemberProcessing(memberId, form.getPassword(), ipAddress);

        EntityModel<MemberResponse.Delete> deleteResultEntityModel = memberResponseModelAssembler.getDeleteModel(delete);

        return ResponseEntity.ok()
                .body(ApiResponse.success(SUCCESS, deleteResultEntityModel));
    }
}
