package com.study.badrequest.repository.board;

import com.study.badrequest.domain.board.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashTagRepository extends JpaRepository<HashTag,Long> {


    List<HashTag> findAllByHashTagNameIn(Iterable<String> collect);
}
