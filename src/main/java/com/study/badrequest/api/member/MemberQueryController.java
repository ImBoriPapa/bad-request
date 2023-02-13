package com.study.badrequest.api.member;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.custom_exception.MemberException;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.domain.member.dto.MemberSearchCondition;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberQueryRepository;
import com.study.badrequest.domain.member.repository.query.MemberAuthDto;
import com.study.badrequest.domain.member.dto.MemberResponse;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.repository.query.MemberDetailDto;
import com.study.badrequest.domain.member.repository.query.MemberListDto;
import com.study.badrequest.domain.member.repository.query.MemberProfileDto;
import com.study.badrequest.utils.modelAssembler.MemberResponseModelAssembler;
import com.study.badrequest.utils.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;


import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;


import static com.study.badrequest.commons.consts.CustomURL.BASE_URL;

import static com.study.badrequest.domain.member.entity.Authority.getAuthorityByAuthorities;
import static com.study.badrequest.utils.authority.AuthorityUtils.restrictAccessIfNotYouAndAdmin;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(BASE_URL)
public class MemberQueryController {
    private final MemberValidator validator;
    private final MemberQueryRepository memberQueryRepositoryImpl;
    private final MemberResponseModelAssembler memberResponseModelAssembler;

    @GetMapping("/members")
    @CustomLogTracer
    public ResponseEntity getMemberList(MemberSearchCondition searchCondition) {

        MemberListDto memberList = memberQueryRepositoryImpl.findMemberList(searchCondition);
        // TODO: 2023/02/13 링크 추가
        return ResponseEntity.ok().body(memberList);
    }

    @GetMapping("/members/{memberId}")
    @CustomLogTracer
    public ResponseEntity<ResponseForm.Of> getMember(@AuthenticationPrincipal User user, @PathVariable Long memberId) {

        Authority authority = getAuthorityByAuthorities(user.getAuthorities());

        MemberAuthDto memberAuthDto = getMemberAuthDto(user, authority);

        restrictAccessIfNotYouAndAdmin(memberId, memberAuthDto.getId(), authority);

        MemberDetailDto memberDetailDto = memberQueryRepositoryImpl.findMemberDetail(memberId)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER));

        EntityModel<MemberDetailDto> entityModel = memberResponseModelAssembler.toModel(memberDetailDto, authority);

        return ResponseEntity
                .ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, entityModel));
    }

    @GetMapping("/members/{memberId}/profile")
    @CustomLogTracer
    public ResponseEntity getProfile(@PathVariable Long memberId) {

        MemberProfileDto memberProfileDto = memberQueryRepositoryImpl.findMemberProfileById(memberId)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER));



        return ResponseEntity
                .ok()
                .body(memberProfileDto);
    }

    /**
     * 클라이언트에서 로그인 상태 확인용 API 데이터
     */
    @GetMapping("/members/auth")
    @CustomLogTracer
    public ResponseEntity getMemberInfo(@AuthenticationPrincipal User user) {
        //user.getAuthorities() -> Authority
        Authority authority = getAuthorityByAuthorities(user.getAuthorities());

        MemberAuthDto memberAuthDto = getMemberAuthDto(user, authority);

        EntityModel<MemberResponse.AuthResult> entityModel = memberResponseModelAssembler.toModel(memberAuthDto);

        return ResponseEntity
                .ok()
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, entityModel));
    }

    @GetMapping("/members/email")
    @CustomLogTracer
    public ResponseEntity<ResponseForm.Of> getMemberEmail(@RequestParam(value = "email", defaultValue = "empty") String email) {
        // TODO: 2023/01/31 이메일 형식 검증 추가
        if (email.equals("empty")) {
            throw new IllegalArgumentException("Email Empty");
        }

        validator.validateEmail(email);

        return ResponseEntity.ok()
                .body(new ResponseForm
                        .Of<>(CustomStatus.SUCCESS, new MemberResponse.ValidateEmailResult(false, email)));
    }

    /**
     * username,authority 로 권한 정보 조회
     */
    public MemberAuthDto getMemberAuthDto(User user, Authority authority) {

        if (authority == Authority.ADMIN) {
            return memberQueryRepositoryImpl.findIdAndAuthorityByUsername(user.getUsername(), null)
                    .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_AUTHORITY));
        }

        return memberQueryRepositoryImpl.findIdAndAuthorityByUsername(user.getUsername(), authority)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_AUTHORITY));
    }

}
