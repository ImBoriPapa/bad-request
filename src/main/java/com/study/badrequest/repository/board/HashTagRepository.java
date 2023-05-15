package com.study.badrequest.repository.board;

import com.study.badrequest.domain.board.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag,Long> {


    List<HashTag> findAllByHashTagNameIn(Iterable<String> collect);

    Optional<HashTag> findByHashTagName(String tag);
}
