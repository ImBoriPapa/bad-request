package com.study.badrequest.member.command.interfaces;

import com.study.badrequest.common.response.ApiResponse;
import com.study.badrequest.member.command.application.MemberSignupService;
import com.study.badrequest.member.command.application.SignUpForm;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import java.net.URI;
import java.util.List;

import static com.study.badrequest.common.constants.ApiURL.POST_MEMBER_URL;
import static com.study.badrequest.common.constants.Regex.PASSWORD;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberSignupApiController {
    private final MemberSignupService memberSignupService;

    @PostMapping(value = POST_MEMBER_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMember(HttpServletRequest request, @Validated @RequestBody SignUp form, BindingResult bindingResult) {
        log.info("Create Member Request");
        RequestValidUtils.throwValidationExceptionIfErrors(bindingResult);
        String ipAddress = HttpHeaderResolver.ipAddressResolver(request);

        final Long memberId = memberSignupService.signupByEmail(createSignUpForm(form, ipAddress));

        return ResponseEntity
                .created(getLocationUri(memberId))
                .body(ApiResponse.success(getCreateEntityModel(new Create(memberId))));
    }

    private SignUpForm createSignUpForm(SignUp form, String ipAddress) {
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
    public static class SignUp {
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

    private EntityModel<Create> getCreateEntityModel(Create create) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberAccountApiController.class).createMember(null, null, null)).withSelfRel(),
                linkTo(methodOn(LoginController.class).loginByEmail(null, null, null)).withRel("Login")
        );

        return EntityModel.of(create, links);
    }
}
