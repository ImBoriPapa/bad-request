package com.study.badrequest.domain.member.event;

import com.study.badrequest.domain.board.service.BoardCommandService;
import com.study.badrequest.domain.comment.service.CommentCommendService;
import com.study.badrequest.domain.login.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ResignEvent {

    private final BoardCommandService boardCommandService;
    private final CommentCommendService commentCommendService;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * refreshTokenRepository 와 MemberCommandService 분리
     * @param dto
     */
    @EventListener
    private void noticeDelete(ResignEventDto dto) {
        log.info("MEMBER DELETE EVENT!!");
        refreshTokenRepository.findById(dto.getUsername()).ifPresent(refreshTokenRepository::delete);
    }
}
