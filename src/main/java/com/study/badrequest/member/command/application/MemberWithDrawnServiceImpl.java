package com.study.badrequest.member.command.application;

import com.study.badrequest.member.command.application.dto.MemberWithDawnForm;
import com.study.badrequest.member.command.domain.*;
import com.study.badrequest.common.exception.CustomRuntimeException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

import static com.study.badrequest.common.response.ApiResponseStatus.*;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class MemberWithDrawnServiceImpl implements MemberWithDrawnService {
    private final MemberPasswordEncoder memberPasswordEncoder;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public LocalDateTime withdrawalMember(MemberWithDawnForm form) {
        log.info("Withdrawal Member Process memberId: {}", form.getMemberId());
        Member member = findMemberById(form.getMemberId());

        member.changeToWithDrawn(form.getPassword(), memberPasswordEncoder);

        eventPublisher.publishEvent(new MemberEventDto.Delete(member.getId(), "회원 탈퇴 요청", form.getIpAddress(), member.getWithdrawalAt()));

        return member.getWithdrawalAt();
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));
    }


}
