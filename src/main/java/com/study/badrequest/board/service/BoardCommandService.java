package com.study.badrequest.board.service;

import com.study.badrequest.Member.domain.entity.Member;
import com.study.badrequest.Member.domain.repository.MemberRepository;
import com.study.badrequest.board.dto.BoardRequest;
import com.study.badrequest.board.entity.Board;
import com.study.badrequest.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardCommandService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    public Board create(Long memberId, BoardRequest.Create form) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Board board = Board.createBoard()
                .title(form.getTitle())
                .context(form.getContext())
                .topic(form.getTopic())
                .category(form.getCategory())
                .member(member)
                .build();
        return boardRepository.save(board);
    }
}
