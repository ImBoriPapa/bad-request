package com.study.badrequest.Member.repository;

import com.study.badrequest.Member.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
