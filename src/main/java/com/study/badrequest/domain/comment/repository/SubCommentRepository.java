package com.study.badrequest.domain.comment.repository;

import com.study.badrequest.domain.comment.entity.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCommentRepository extends JpaRepository<SubComment,Long> {
}
