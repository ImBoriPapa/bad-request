package com.study.badrequest.repository.board;

import com.study.badrequest.domain.board.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface HashTagRepository extends JpaRepository<HashTag,Long> {

    Optional<HashTag> findByTagName(String hashTag);

    List<HashTag> findAllByTagNameIn(Iterable<String> collect);
}
