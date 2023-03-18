package com.study.badrequest.api.member;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.custom_exception.MemberException;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.domain.member.dto.MemberResponse;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.repository.MemberQueryRepository;
import com.study.badrequest.domain.member.repository.query.MemberAuthDto;
import com.study.badrequest.domain.member.repository.query.MemberDetailDto;
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

import static com.study.badrequest.commons.consts.CustomURL.BASE_API_VERSION_URL;
import static com.study.badrequest.domain.member.entity.Authority.getAuthorityByAuthorities;
import static com.study.badrequest.utils.authority.AuthorityUtils.restrictAccessIfNotYouAndAdmin;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(BASE_API_VERSION_URL)
public class MemberQueryController {
    private final MemberValidator validator;
    private final MemberQueryRepository memberQueryRepositoryImpl;
    private final MemberResponseModelAssembler memberResponseModelAssembler;

    /**
     * 회원 정보 상세 보기
     */
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

    /**
     * 프로필 정보 보기
     */
    @GetMapping("/members/{memberId}/profile")
    @CustomLogTracer
    public ResponseEntity getProfile(@PathVariable Long memberId) {

        MemberProfileDto memberProfileDto = memberQueryRepositoryImpl.findMemberProfileByMemberId(memberId)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER));

        return ResponseEntity
                .ok()
                .body(memberProfileDto);
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
