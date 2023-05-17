package com.study.badrequest.api.member;


import com.study.badrequest.commons.annotation.LoggedInMember;
import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.domain.login.CurrentLoggedInMember;
import com.study.badrequest.dto.member.MemberRequestForm;
import com.study.badrequest.dto.member.MemberResponse;
import com.study.badrequest.service.member.MemberCommandService;
import com.study.badrequest.service.member.MemberProfileService;

import com.study.badrequest.utils.modelAssembler.MemberResponseModelAssembler;
import com.study.badrequest.utils.verification.RequestValidUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import static com.study.badrequest.commons.constants.ApiURL.*;
import static com.study.badrequest.commons.response.ApiResponseStatus.SUCCESS;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {
    private final MemberCommandService memberCommandService;
    private final MemberResponseModelAssembler memberResponseModelAssembler;
    private final MemberProfileService memberProfileService;
    private final RequestValidUtils requestValidUtils;

    /**
     * 회원가입
     *
     * @param form: String email, String password, String nickname, String contact
     * @return 201 created, memberId, createdAt
     */
    @PostMapping(value = POST_MEMBER_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse.Success> createMember(@Validated @RequestBody MemberRequestForm.SignUp form, BindingResult bindingResult) {
        log.info("[회원 생성 요청 email: {}, contact: {}, password: {}, nickname: {}]", form.getEmail(), form.getContact(), "PROTECTED", form.getNickname());

        requestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.Create create = memberCommandService.signupMemberProcessing(form);

        URI locationUri = memberResponseModelAssembler.getLocationUri(create.getId());

        return ResponseEntity
                .created(locationUri)
                .body(ApiResponse.success(SUCCESS, memberResponseModelAssembler.createMemberModel(create)));
    }

    /**
     * 닉네임 변경 요청
     */
    @PatchMapping(value = PATCH_MEMBER_NICKNAME, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity changeNickname(@PathVariable Long memberId,
                                         @RequestBody MemberRequestForm.ChangeNickname form,
                                         @LoggedInMember CurrentLoggedInMember.Information information) {
        log.info("[회원 닉네임 변경 요청 요청 memberId: {}, 로그인된 memberId: {}]",memberId,information.getId());

        requestValidUtils.throwMemberExceptionIfNotMatchMemberId(memberId, information.getId());

        MemberResponse.Update update = memberProfileService.changeNickname(memberId, form);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, memberResponseModelAssembler.changeNicknameModel(update)));
    }

    /**
     * 자시 소개 변경 요청
     */
    @PatchMapping(value = PATCH_MEMBER_INTRODUCE, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity changeIntroduce(@PathVariable Long memberId,
                                          @RequestBody MemberRequestForm.ChangeIntroduce form) {
        log.info("[회원 소개 변경 요청]");
        MemberResponse.Update update = memberProfileService.changeIntroduce(memberId, form);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, update));
    }
    // TODO: 2023/04/18 완성하기

    /**
     * 기본 프로필 이미지로 변경
     */
    @PatchMapping(PATCH_MEMBER_PROFILE_IMAGE_TO_DEFAULT)
    public ResponseEntity changeProfileImageToDefault(@PathVariable Long memberId) {
        log.info("[회원 프로필 이미지 기본 이미지로 변경 요청]");
        MemberResponse.Update update = memberProfileService.changeProfileImageToDefault(memberId);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, update));
    }

    /**
     * 프로필 이미지 변경
     */
    @PatchMapping(PATCH_MEMBER_PROFILE_IMAGE)
    public ResponseEntity changeProfileImage(@PathVariable Long memberId,
                                             @RequestPart MultipartFile image) {
        log.info("[회원 프로필 이미지 변경 요청]");
        MemberResponse.Update update = memberProfileService.changeProfileImage(memberId, image);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, update));
    }

    /**
     * 임시 비밀번호 요청
     *
     * @param form: String email
     * @return 200 : String email, LocalDateTime issuedAt
     */
    @PostMapping(value = POST_MEMBER_TEMPORARY_PASSWORD_ISSUE_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse.Success> issueTemporaryPassword(@Validated
                                                                  @RequestBody MemberRequestForm.IssueTemporaryPassword form,
                                                                      BindingResult bindingResult) {
        log.info("[임시 비밀번호 요청 email: {}]", form.getEmail());

        requestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.TemporaryPassword issueTemporaryPassword = memberCommandService.issueTemporaryPasswordProcessing(form.getEmail());

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, memberResponseModelAssembler.getIssuePasswordModel(issueTemporaryPassword)));
    }

    /**
     * 인증메일 발송 요청
     * @param form : String
     * @return 200 ok
     */
    @PostMapping(POST_MEMBER_SEND_EMAIL_AUTHENTICATION_CODE)
    public ResponseEntity sendAuthenticationEmail(@Validated @RequestBody MemberRequestForm.SendAuthenticationEmail form, BindingResult bindingResult) {
        log.info("[이메일 인증 번호 요청 email: {}]", form.getEmail());

        requestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.SendAuthenticationEmail sendAuthenticationEmail = memberCommandService.sendAuthenticationMailProcessing(form.getEmail());

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
    public ResponseEntity<ApiResponse.Success> patchPassword(@Validated
                                                         @PathVariable Long memberId,
                                                             @RequestBody MemberRequestForm.ChangePassword form,
                                                             @LoggedInMember CurrentLoggedInMember.Information information,
                                                             BindingResult bindingResult
    ) {
        log.info("[비밀번호 변경 요청 memberId: {}, password: {}, password: {}]", memberId, "PROTECTED", "PROTECTED");

        requestValidUtils.throwMemberExceptionIfNotMatchMemberId(memberId, information.getId());

        requestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.Update update = memberCommandService.changePasswordProcessing(memberId, form);

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
    public ResponseEntity<ApiResponse.Success> patchContact(@Validated
                                                        @PathVariable Long memberId,
                                                            @RequestBody MemberRequestForm.UpdateContact form,
                                                            @LoggedInMember CurrentLoggedInMember.Information information,
                                                            BindingResult bindingResult) {
        log.info("[연락처 변경 요청 memberId: {}, contact: {}]", memberId, form.getContact());

        requestValidUtils.throwMemberExceptionIfNotMatchMemberId(memberId, information.getId());

        requestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.Update update = memberCommandService.updateContactProcessing(memberId, form.getContact());

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
    public ResponseEntity<ApiResponse.Success> deleteMember(@Validated @PathVariable Long memberId,
                                                            @RequestBody MemberRequestForm.DeleteMember form,
                                                            @LoggedInMember CurrentLoggedInMember.Information information,
                                                            BindingResult bindingResult) {

        log.info("[회원 탈퇴 요청 memberId: {}, password: {}]", memberId, "PROTECTED");

        requestValidUtils.throwMemberExceptionIfNotMatchMemberId(memberId, information.getId());

        requestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        MemberResponse.Delete delete = memberCommandService.resignMemberProcessing(memberId, form.getPassword());

        EntityModel<MemberResponse.Delete> deleteResultEntityModel = memberResponseModelAssembler.getDeleteModel(delete);

        return ResponseEntity.ok()
                .body(ApiResponse.success(SUCCESS, deleteResultEntityModel));
    }
}
