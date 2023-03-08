package com.study.badrequest.domain.member.event;

import com.study.badrequest.domain.board.service.BoardCommandServiceImpl;
import com.study.badrequest.domain.comment.service.CommentCommendService;
import com.study.badrequest.domain.login.repository.redisRefreshTokenRepository;
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

    private final BoardCommandServiceImpl boardCommandService;
    private final CommentCommendService commentCommendService;
    private final redisRefreshTokenRepository redisRefreshTokenRepository;

    /**
     * refreshTokenRepository 와 MemberCommandService 분리
     * @param dto
     */
    @EventListener
    private void noticeDelete(ResignEventDto dto) {
        log.info("MEMBER DELETE EVENT!!");
        redisRefreshTokenRepository.findById(dto.getUsername()).ifPresent(redisRefreshTokenRepository::delete);
    }
}
