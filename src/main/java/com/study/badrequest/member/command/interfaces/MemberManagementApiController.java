package com.study.badrequest.member.command.interfaces;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.badrequest.common.annotation.LoggedInMember;
import com.study.badrequest.common.response.ApiResponse;
import com.study.badrequest.member.command.application.*;
import com.study.badrequest.member.command.application.dto.*;
import com.study.badrequest.login.command.domain.CustomMemberPrincipal;

import com.study.badrequest.member.command.domain.values.MemberId;
import com.study.badrequest.member.command.domain.dto.MemberChangeContact;
import com.study.badrequest.member.command.domain.dto.MemberChangePassword;
import com.study.badrequest.member.command.domain.dto.MemberResign;
import com.study.badrequest.member.query.interfaces.MemberQueryApiController;
import com.study.badrequest.utils.header.HttpHeaderResolver;
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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static com.study.badrequest.common.constants.ApiURL.*;
import static com.study.badrequest.common.constants.Regex.PASSWORD;
import static com.study.badrequest.common.response.ApiResponseStatus.SUCCESS;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberManagementApiController {
    private final MemberManagementService memberManagementService;
    @PostMapping(value = POST_MEMBER_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMember(HttpServletRequest httpServletRequest, @Validated @RequestBody MemberCreateForm memberCreateForm, BindingResult bindingResult) {
        log.info("Create Member Request");
        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        String ipAddress = HttpHeaderResolver.ipAddressResolver(httpServletRequest);

        final Long memberId = memberManagementService.signupByEmail(memberCreateForm).getId();

        return ResponseEntity
                .created(getLocationUri(memberId))
                .body(ApiResponse.success(getCreateEntityModel(new Create(memberId))));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class SignUpRequest {
        @Email(message = "형식에 맞지 않는 이메일입니다.")
        @NotEmpty(message = "이메일을 입력해주세요.")
        private String email;
        @Pattern(regexp = PASSWORD, message = "비밀번호는 숫자,문자,특수문자 포함 8~15자리")
        private String password;
        @NotEmpty(message = "닉네임을 입력해주세요")
        private String nickname;
        @NotEmpty(message = "연락처를 입력해주세요")
        private String contact;
        @NotEmpty(message = "인증 코드를 입력해주세요")
        private String authenticationCode;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Create {
        private Long id;

    }

    private URI getLocationUri(Long memberId) {
        return linkTo(methodOn(MemberQueryApiController.class).retrieveMemberAccount(null, memberId)).toUri();
    }

    private SignupForm createSignUpForm(SignUpRequest form, String ipAddress) {
        return SignupForm.builder()
                .email(form.getEmail())
                .password(form.getPassword())
                .nickname(form.getNickname())
                .contact(form.getContact())
                .authenticationCode(form.getAuthenticationCode())
                .ipAddress(ipAddress)
                .build();
    }

    private EntityModel<Create> getCreateEntityModel(Create create) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberManagementApiController.class).createMember(null, null, null)).withSelfRel()
        );

        return EntityModel.of(create, links);
    }


    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class IssueTemporaryPasswordRequest {
        @NotEmpty(message = "이메일을 입력해주세요")
        private String email;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class IssueEmailAuthenticationCodeRequest {
        @Email(message = "형식에 맞지 않는 이메일입니다.")
        @NotEmpty(message = "이메일을 입력해주세요.")
        private String email;
    }

    @PatchMapping(value = PATCH_MEMBER_PASSWORD_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> patchPassword(@Validated
                                           @PathVariable Long memberId, @RequestBody MemberChangePassword memberChangePassword,
                                           @LoggedInMember CustomMemberPrincipal information, BindingResult bindingResult, HttpServletRequest httpServletRequest
    ) {
        log.info("[비밀번호 변경 요청 memberId: {}, password: {}, password: {}]", memberId, "PROTECTED", "PROTECTED");

        String ipAddress = HttpHeaderResolver.ipAddressResolver(httpServletRequest);

        RequestValidUtils.throwMemberExceptionIfNotMatchMemberId(memberId, information.getMemberId());

        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        Long id = memberManagementService.changePassword(new MemberId(memberId),memberChangePassword).getId();

        Update update = new Update(id);

        List<Link> links = List.of(
                linkTo(methodOn(MemberManagementApiController.class).patchPassword(update.getId(), null, null, null, null)).withSelfRel(),
                linkTo(methodOn(MemberQueryApiController.class).getProfile(null, null)).withRel("Profile")
        );
        EntityModel<Update> entityModel = EntityModel.of(update, links);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(SUCCESS, entityModel));
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePasswordRequest {
        @Pattern(regexp = PASSWORD, message = "비밀번호는 숫자,문자,특수문자 포함 8~15자리")
        private String currentPassword;
        @Pattern(regexp = PASSWORD, message = "비밀번호는 숫자,문자,특수문자 포함 8~15자리")
        private String newPassword;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        private Long id;
    }


    @PatchMapping(value = PATCH_MEMBER_CONTACT_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> patchContact(@Validated
                                          @PathVariable Long memberId,
                                          @RequestBody MemberChangeContact memberChangeContact,
                                          @LoggedInMember CustomMemberPrincipal information,
                                          BindingResult bindingResult,
                                          HttpServletRequest httpServletRequest) {


        String ipAddress = HttpHeaderResolver.ipAddressResolver(httpServletRequest);

        RequestValidUtils.throwMemberExceptionIfNotMatchMemberId(memberId, information.getMemberId());

        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        ContactChangeForm contactChangeForm = new ContactChangeForm();

        Long id = memberManagementService.changeContact(new MemberId(memberId),memberChangeContact).getId();

        List<Link> links = List.of(
                linkTo(methodOn(MemberManagementApiController.class).patchContact(id, null, null, null, null)).withSelfRel(),
                linkTo(methodOn(MemberQueryApiController.class).getProfile(null, null)).withRel("Profile")
        );

        Update update = new Update(id);

        EntityModel<Update> entityModel = EntityModel.of(update, links);

        return ResponseEntity.ok()
                .body(ApiResponse.success(SUCCESS, entityModel));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ChangeContactRequest {
        @NotEmpty(message = "연락처를 입력해주세요")
        private String contact;
    }

    @DeleteMapping(DELETE_MEMBER_URL)
    public ResponseEntity<?> deleteMember(@Validated @PathVariable Long memberId,
                                          @RequestBody MemberResign memberResign,
                                          @LoggedInMember CustomMemberPrincipal information, BindingResult bindingResult,
                                          HttpServletRequest httpServletRequest
    ) {

        log.info("[회원 탈퇴 요청 memberId: {}, password: {}]", memberId, "PROTECTED");

        String ipAddress = HttpHeaderResolver.ipAddressResolver(httpServletRequest);

        RequestValidUtils.throwMemberExceptionIfNotMatchMemberId(memberId, information.getMemberId());

        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);

        LocalDateTime resignAt = memberManagementService.resign(new MemberId(memberId),memberResign);

        List<Link> links = List.of(
                linkTo(methodOn(MemberManagementApiController.class).deleteMember(null, null, null, null, null)).withSelfRel(),
                linkTo(methodOn(MemberManagementApiController.class).createMember(null, null, null)).withRel("Signup Member")
        );

        Withdrawn withdrawn = new Withdrawn(resignAt);

        EntityModel<Withdrawn> entityModel = EntityModel.of(withdrawn, links);

        return ResponseEntity.ok()
                .body(ApiResponse.success(SUCCESS, entityModel));
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WithDrawnRequest {
        @NotEmpty(message = "비밀번호를 입력해 주세요")
        private String password;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Withdrawn {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime withdrawnAt;
    }
}
