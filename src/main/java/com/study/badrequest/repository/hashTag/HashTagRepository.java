package com.study.badrequest.repository.hashTag;


import com.study.badrequest.domain.hashTag.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface HashTagRepository extends JpaRepository<HashTag,Long> {
    List<HashTag> findAllByHashTagNameIn(Set<String> tags);

    Optional<HashTag> findByHashTagName(String tag);
}
