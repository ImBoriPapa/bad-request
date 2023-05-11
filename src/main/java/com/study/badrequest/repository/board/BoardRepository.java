package com.study.badrequest.repository.board;


import com.study.badrequest.domain.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Long> {

    Optional<Board> findByTitle(String title);
}