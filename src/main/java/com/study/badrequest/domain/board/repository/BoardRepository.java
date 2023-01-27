package com.study.badrequest.domain.board.repository;

import com.study.badrequest.domain.Member.domain.entity.Member;
import com.study.badrequest.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board,Long> {

    Board findByTitle(String title);
}
