package com.study.badrequest.api.member;


import com.study.badrequest.commons.annotation.LoggedInMember;
import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.domain.login.CurrentMember;
import com.study.badrequest.dto.member.MemberRequestForm;
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
public class MemberApiController {
    private final MemberService memberService;
    private final MemberResponseModelAssembler memberResponseModelAssembler;

    /**
     * 회원가입
     *
     * @param form: String email, String password, String nickname, String contact
     * @return 201 created, memberId, createdAt
     */
    @PostMapping(value = POST_MEMBER_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity createMember(HttpServletRequest request,
                                       @Validated @RequestBody MemberRequestForm.SignUp form, BindingResult bindingResult) {
        log.info("Create Member Request");
        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);
        String ipAddress = HttpHeaderResolver.ipAddressResolver(request);

        MemberResponse.Create create = memberService.signupMember(form, ipAddress);

        URI locationUri = memberResponseModelAssembler.getLocationUri(create.getId());

        return ResponseEntity
                .created(locationUri)
                .body(ApiResponse.success(SUCCESS, memberResponseModelAssembler.createMemberModel(create)));
    }


    /**
     * 임시 비밀번호 요청
     *
     * @param form: String email
     * @return 200 : String email, LocalDateTime issuedAt
     */
    @PostMapping(value = POST_MEMBER_TEMPORARY_PASSWORD_ISSUE_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity issueTemporaryPassword(@Validated
                                                 @RequestBody MemberRequestForm.IssueTemporaryPassword form,
                                                 BindingResult bindingResult) {
        log.info("[임시 비밀번호 요청 email: {}]", form.getEmail());

        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.TemporaryPassword issueTemporaryPassword = memberService.issueTemporaryPasswordProcessing(form.getEmail());

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, memberResponseModelAssembler.getIssuePasswordModel(issueTemporaryPassword)));
    }

    /**
     * 인증메일 발송 요청
     *
     * @param form : String
     * @return 200 ok
     */
    @PostMapping(POST_MEMBER_SEND_EMAIL_AUTHENTICATION_CODE)
    public ResponseEntity sendAuthenticationEmail(@Validated @RequestBody MemberRequestForm.SendAuthenticationEmail form, BindingResult bindingResult) {
        log.info("[이메일 인증 번호 요청 email: {}]", form.getEmail());

        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.SendAuthenticationEmail sendAuthenticationEmail = memberService.sendAuthenticationMailProcessing(form.getEmail());

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, memberResponseModelAssembler.getSendAuthenticationMail(sendAuthenticationEmail)));
    }


    /**
     * 비밀변호 변경
     *
     * @param memberId : Long memberId
     * @param form     : String password, String newPassword
     * @return 200 Ok, memberId, updatedAt
     */
    @PatchMapping(value = PATCH_MEMBER_PASSWORD_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity patchPassword(@Validated
                                        @PathVariable Long memberId,
                                        @RequestBody MemberRequestForm.ChangePassword form,
                                        @LoggedInMember CurrentMember.Information information,
                                        BindingResult bindingResult
    ) {
        log.info("[비밀번호 변경 요청 memberId: {}, password: {}, password: {}]", memberId, "PROTECTED", "PROTECTED");

        RequestValidUtils.throwMemberExceptionIfNotMatchMemberId(memberId, information.getId());

        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.Update update = memberService.changePasswordProcessing(memberId, form);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, memberResponseModelAssembler.getChangePasswordModel(update)));
    }


    /**
     * 연락처 변경
     *
     * @param memberId : Long memberId
     * @param form:    String contact
     * @return 200 Ok, memberId, updatedAt
     */
    @PatchMapping(value = PATCH_MEMBER_CONTACT_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity patchContact(@Validated
                                       @PathVariable Long memberId,
                                       @RequestBody MemberRequestForm.UpdateContact form,
                                       @LoggedInMember CurrentMember.Information information,
                                       BindingResult bindingResult) {
        log.info("[연락처 변경 요청 memberId: {}, contact: {}]", memberId, form.getContact());

        RequestValidUtils.throwMemberExceptionIfNotMatchMemberId(memberId, information.getId());

        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.Update update = memberService.updateContactProcessing(memberId, form.getContact());

        return ResponseEntity.ok()
                .body(ApiResponse.success(SUCCESS, memberResponseModelAssembler.getChangeContactModel(update)));
    }

    /**
     * 회원 탈퇴
     *
     * @param memberId : Long memberId
     * @param form:    String password
     * @return 200 Ok
     */
    @DeleteMapping(DELETE_MEMBER_URL)
    public ResponseEntity deleteMember(@Validated @PathVariable Long memberId,
                                       @RequestBody MemberRequestForm.DeleteMember form,
                                       @LoggedInMember CurrentMember.Information information,
                                       BindingResult bindingResult) {

        log.info("[회원 탈퇴 요청 memberId: {}, password: {}]", memberId, "PROTECTED");

        RequestValidUtils.throwMemberExceptionIfNotMatchMemberId(memberId, information.getId());

        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.Delete delete = memberService.resignMemberProcessing(memberId, form.getPassword());

        EntityModel<MemberResponse.Delete> deleteResultEntityModel = memberResponseModelAssembler.getDeleteModel(delete);

        return ResponseEntity.ok()
                .body(ApiResponse.success(SUCCESS, deleteResultEntityModel));
    }
}
