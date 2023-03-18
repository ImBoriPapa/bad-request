package com.study.badrequest.domain.mentor.repository;

import com.study.badrequest.domain.mentor.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
}
