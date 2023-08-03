package com.study.badrequest.member.command.interfaces;


import com.study.badrequest.common.annotation.LoggedInMember;
import com.study.badrequest.common.response.ApiResponse;
import com.study.badrequest.member.command.domain.CurrentMember;
import com.study.badrequest.member.command.application.MemberService;

import com.study.badrequest.member.command.application.SignUpForm;
import com.study.badrequest.member.query.interfaces.MemberQueryApiController;
import com.study.badrequest.utils.header.HttpHeaderResolver;
import com.study.badrequest.utils.modelAssembler.MemberResponseModelAssembler;
import com.study.badrequest.utils.verification.RequestValidUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

import static com.study.badrequest.common.constants.ApiURL.*;
import static com.study.badrequest.common.response.ApiResponseStatus.SUCCESS;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
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

        final Long memberId = memberService.signUpWithEmail(createSignUpForm(form, ipAddress));

        return ResponseEntity
                .created(getLocationUri(memberId))
                .body(ApiResponse.success(getCreateEntityModel(new Create(memberId))));
    }

    private URI getLocationUri(Long memberId) {
        return linkTo(methodOn(MemberQueryApiController.class).retrieveMemberAccount(null, memberId)).toUri();
    }

    private EntityModel<Create> getCreateEntityModel(Create create) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberAccountApiController.class).createMember(null, null, null)).withSelfRel(),
                linkTo(methodOn(LoginController.class).loginByEmail(null, null, null)).withRel("Login")
        );

        return EntityModel.of(create, links);
    }

    private SignUpForm createSignUpForm(MemberRequest.SignUp form, String ipAddress) {
        return SignUpForm.builder()
                .email(form.getEmail())
                .password(form.getPassword())
                .nickname(form.getNickname())
                .contact(form.getContact())
                .authenticationCode(form.getAuthenticationCode())
                .ipAddress(ipAddress)
                .build();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Create {
        private Long id;

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
