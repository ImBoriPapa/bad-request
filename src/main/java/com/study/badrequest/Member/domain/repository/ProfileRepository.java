package com.study.badrequest.Member.domain.repository;

import com.study.badrequest.Member.domain.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
