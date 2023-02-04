package com.study.badrequest.domain.board.repository;

import com.study.badrequest.domain.Member.domain.entity.Member;
import com.study.badrequest.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Long> {

    Optional<Board> findByTitle(String title);
}
