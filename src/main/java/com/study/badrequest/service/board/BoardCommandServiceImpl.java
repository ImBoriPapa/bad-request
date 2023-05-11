package com.study.badrequest.service.board;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.board.Board;
import com.study.badrequest.domain.board.BoardImageStatus;
import com.study.badrequest.domain.board.BoardTag;
import com.study.badrequest.domain.board.HashTag;
import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.dto.board.BoardRequest;
import com.study.badrequest.dto.board.BoardResponse;
import com.study.badrequest.exception.custom_exception.BoardExceptionBasic;
import com.study.badrequest.exception.custom_exception.MemberExceptionBasic;
import com.study.badrequest.repository.board.BoardRepository;
import com.study.badrequest.repository.board.HashTagRepository;
import com.study.badrequest.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BoardCommandServiceImpl implements BoardCommendService {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BoardImageService boardImageService;
    private final HashTagRepository hashTagRepository;

    /**
     * 게시판 생성
     */
    @Override
    @Transactional
    public BoardResponse.Create create(Long memberId, Authority authority, BoardRequest.Create form) {
        log.info("게시판 생성 시작");

        Board savedBoard = saveBoard(memberId, authority, form);

        boardImageService.changeStatus(form.getImageIds(), savedBoard, BoardImageStatus.SAVED);

        hashTagProcessing(form, savedBoard);

        return new BoardResponse.Create(savedBoard.getId(), savedBoard.getCreatedAt());
    }

    private void hashTagProcessing(BoardRequest.Create form, Board savedBoard) {
        if (!form.getHashTags().isEmpty()) {

            Set<String> requestedHashTag = new HashSet<>(form.getHashTags());
            //이미 저장된 HashTag
            List<HashTag> findSavedTag = hashTagRepository.findAllByHashTagNameIn(requestedHashTag.stream().map(t -> "#" + t).collect(Collectors.toSet()));
            //데이터베이스에 저장된 테그는 BoardTag 에 저장
            findSavedTag.forEach(tag -> BoardTag.createBoardTag(savedBoard, tag));
            //저장된 해시태그의 태그네임 추출
            Set<String> existingTags = findSavedTag.stream().map(HashTag::getHashTagName).collect(Collectors.toSet());
            //저장된 해시태그에 포함되지 않는 요청된 태그를 추출해서 새로운 해시태그 엔티티 생성
            Set<HashTag> newTags = requestedHashTag.stream()
                    .filter(tag -> !existingTags.contains("#" + tag))
                    .map(HashTag::new)
                    .collect(Collectors.toSet());
            //새로운 해시태그 엔티티 저장
            hashTagRepository.saveAll(newTags);
            //저장된 해시 태그를 BoardTag 에 저장
            newTags.forEach(savedTag -> BoardTag.createBoardTag(savedBoard, savedTag));
        }
    }


    @Transactional
    @Override
    public BoardResponse.Update update(User user, Long boardId, BoardRequest.Update form) {


        Board board = findBoardByBoardId(boardId);

        board.titleUpdateIfHasChange(form.getTitle());
        board.contentsUpdateIfNotNull(form.getContents());
        board.categoryUpdateIfNotNull(form.getCategory());
        board.topicUpdateIfNotNull(form.getTopic());

        Board updated = findBoardByBoardId(boardId);

        boardImageService.update(form.getImageId(), updated);

        return new BoardResponse.Update(updated);
    }

    @Transactional
    @Override
    public void delete(User user, Long boardId) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        boardImageService.deleteByBoard(board);

        boardRepository.delete(board);
    }

    private Board findBoardByBoardId(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardExceptionBasic(ApiResponseStatus.NOT_FOUND_BOARD));
    }

    private Board saveBoard(Long memberId, Authority authority, BoardRequest.Create form) {

        Board board = Board.createBoard()
                .title(form.getTitle())
                .contents(form.getContents())
                .topic(form.getTopic())
                .category(form.getCategory())
                .member(findMemberByUser(memberId, authority, ApiResponseStatus.NOTFOUND_MEMBER))
                .build();

        return boardRepository.save(board);
    }

    private Member findMemberByUser(Long memberId, Authority authority, ApiResponseStatus apiResponseStatus) {
        return memberRepository.findByIdAndAuthority(memberId, authority)
                .orElseThrow(() -> new MemberExceptionBasic(apiResponseStatus));
    }
}
