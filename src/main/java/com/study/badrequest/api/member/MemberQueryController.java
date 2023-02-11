package com.study.badrequest.api.member;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.custom_exception.MemberException;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.domain.member.dto.MemberInfoDto;
import com.study.badrequest.domain.member.dto.MemberResponse;
import com.study.badrequest.domain.member.repository.MemberQueryRepository;
import com.study.badrequest.utils.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.study.badrequest.commons.consts.CustomURL.BASE_URL;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(BASE_URL)
public class MemberQueryController {
    private final MemberValidator validator;
    private final MemberQueryRepository memberQueryRepository;

    /**
     * 클라이언트에서 로그인 상태를 확인용 API 데이터
     */

    @GetMapping("/member/info")
    public ResponseEntity getMemberInfo(@AuthenticationPrincipal User user) {
        log.info("User= {}", user.getUsername());

        MemberInfoDto memberInfoDto = memberQueryRepository.findIdAndAuthorityByUsername(user.getUsername())
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER));

        return ResponseEntity
                .ok()
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, memberInfoDto));
    }

    @GetMapping("/member/{memberId}")
    @CustomLogTracer
    public ResponseEntity<ResponseForm.Of> getMember(@PathVariable Long memberId) {

        return null;
    }

    @GetMapping("/member/email")
    @CustomLogTracer
    public ResponseEntity<ResponseForm.Of> getMemberEmail(@RequestParam(value = "email", defaultValue = "empty") String email) {
        // TODO: 2023/01/31 이메일 형식 검증 추가
        if (email.equals("empty")) {
            throw new IllegalArgumentException("Email Empty");
        }

        validator.validateEmail(email);

        return ResponseEntity.ok()
                .body(new ResponseForm
                        .Of<>(CustomStatus.SUCCESS, new MemberResponse.ValidateEmail(false, email)));
    }

}
