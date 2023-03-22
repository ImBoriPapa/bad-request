package com.study.badrequest.domain.board.service;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.custom_exception.MemberException;
import com.study.badrequest.domain.board.entity.BoardImageStatus;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.dto.BoardResponse;
import com.study.badrequest.domain.board.entity.Board;

import com.study.badrequest.domain.board.repository.BoardRepository;

import com.study.badrequest.commons.exception.custom_exception.BoardException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BoardCommandServiceImpl implements BoardCommendService {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BoardImageService boardImageService;

    /**
     * 게시판 생성
     */
    @CustomLogTracer
    @Override
    public BoardResponse.Create create(User user, BoardRequest.Create form) {
        log.info("BoardCommendService->create");

        Board savedBoard = saveBoard(user, form);

        boardImageService.changeStatus(form.getImageIds(), savedBoard, BoardImageStatus.SAVED);

        return new BoardResponse.Create(savedBoard.getId(), savedBoard.getCreatedAt());
    }


    @CustomLogTracer
    @Override
    public BoardResponse.Update update(User user, Long boardId, BoardRequest.Update form) {

        rejectUpdateIfNotWriterOrAdmin(user);

        Board board = findBoardByBoardId(boardId);

        board.titleUpdateIfHasChange(form.getTitle());
        board.contentsUpdateIfNotNull(form.getContents());
        board.categoryUpdateIfNotNull(form.getCategory());
        board.topicUpdateIfNotNull(form.getTopic());

        Board updated = findBoardByBoardId(boardId);

        boardImageService.update(form.getImageId(), updated);

        return new BoardResponse.Update(updated);
    }

    @CustomLogTracer
    @Override
    public void delete(User user, Long boardId) {

        rejectUpdateIfNotWriterOrAdmin(user);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        boardImageService.deleteByBoard(board);

        boardRepository.delete(board);
    }

    private void rejectUpdateIfNotWriterOrAdmin(User user) {
        Member member = findMemberByUser(user, CustomStatus.NOTFOUND_MEMBER);

        if (!member.getUsername().equals(user.getUsername())) {
            throw new BoardException(CustomStatus.NOT_MATCH_BOARD_WRITER);
        }
    }

    private Board findBoardByBoardId(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(CustomStatus.NOT_FOUND_BOARD));
    }

    private Board saveBoard(User user, BoardRequest.Create form) {
        Board board = Board.createBoard()
                .title(form.getTitle())
                .contents(form.getContents())
                .topic(form.getTopic())
                .category(form.getCategory())
                .member(findMemberByUser(user, CustomStatus.NOTFOUND_MEMBER))
                .build();

        return boardRepository.save(board);
    }

    private Member findMemberByUser(User user, CustomStatus customStatus) {
        return memberRepository
                .findMemberByUsernameAndAuthority(
                        user.getUsername(),
                        Authority.getAuthorityByAuthorities(user.getAuthorities()))
                .orElseThrow(() -> new MemberException(customStatus));
    }
}
